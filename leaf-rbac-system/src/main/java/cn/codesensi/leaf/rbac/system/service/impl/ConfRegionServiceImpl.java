package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.exception.SystemException;
import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import cn.codesensi.leaf.rbac.framework.event.CacheRegionEvent;
import cn.codesensi.leaf.rbac.system.dto.RegionApiResponse;
import cn.codesensi.leaf.rbac.system.dto.RegionDTO;
import cn.codesensi.leaf.rbac.system.entity.ConfRegion;
import cn.codesensi.leaf.rbac.system.mapper.ConfRegionMapper;
import cn.codesensi.leaf.rbac.system.service.ConfRegionService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.codesensi.leaf.rbac.system.entity.table.ConfRegionTableDef.CONF_REGION;

/**
 * 行政区划配置表 服务层实现。
 *
 * @author codesensi
 * @since 2026-07-10
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class ConfRegionServiceImpl extends ServiceImpl<ConfRegionMapper, ConfRegion> implements ConfRegionService {

    private final ConfRegionMapper confRegionMapper;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 从民政部导入行政区划
     */
    @Override
    public Integer importFromMCA() {

        // 1. 调用 API 获取数据
        String result = HttpUtil.get(appProperties.getMcaDmfwApi());
        RegionApiResponse response = JSONUtil.toBean(result, RegionApiResponse.class);
        if (ObjUtil.isNull(response) || ObjUtil.isNull(response.getData())) {
            log.error("调用民政部 API 失败，响应为空");
            throw new SystemException("获取行政区划数据失败");
        }

        // 2. 统计并打印 API 返回的数据总量
        RegionApiResponse.RegionNode root = response.getData();
        int apiTotal = countNodes(root);
        log.info("民政部 API 返回行政区划数据总量: {} 条", apiTotal);

        // 3. 物理删除旧数据（绕过全局逻辑删除）
        int deleted = confRegionMapper.deleteAll();
        log.info("清空旧数据完成，删除 {} 条记录", deleted);

        // 4. 递归处理并补全省级下的虚拟市级节点
        List<ConfRegion> entities = new ArrayList<>();

        // 根节点是 "00" 全国，它的 children 是各省/直辖市/自治区
        for (RegionApiResponse.RegionNode provinceNode : root.getChildren()) {
            // 处理省级节点及其下级
            processProvince(provinceNode, entities);
        }

        // 5. 批量插入
        if (!entities.isEmpty()) {
            int inserted = confRegionMapper.insertBatchSelective(entities);
            log.info("行政区划数据导入完成，共导入 {} 条记录", inserted);

            // 发布缓存刷新事件，触发缓存清空
            eventPublisher.publishEvent(new CacheRegionEvent(inserted, "民政部导入"));
            return inserted;
        }
        return null;
    }

    /**
     * 根据行政区划代码查询行政区划下属节点
     *
     * @param code
     */
    @Cacheable(value = CacheConst.REGION_PCODE, key = "#code")
    @Override
    public List<RegionDTO> listChildrenByCode(String code) {
        List<ConfRegion> confRegions = QueryChain.of(confRegionMapper)
                .select(CONF_REGION.CODE, CONF_REGION.NAME)
                .where(CONF_REGION.PCODE.eq(code))
                .list();
        return BeanUtil.copyToList(confRegions, RegionDTO.class);
    }

    /**
     * 处理省级节点
     */
    private void processProvince(RegionApiResponse.RegionNode provinceNode, List<ConfRegion> entities) {
        // 1. 行政区划编码只取前6位
        String sourceCode = provinceNode.getCode();
        String provinceName = provinceNode.getName();
        if (StrUtil.isBlank(sourceCode) || sourceCode.length() < 6) {
            log.info("[跳过] 行政区划编码为空或者格式错误的数据: code={}, name={}", sourceCode, provinceName);
            return;
        }
        String provinceCode = sourceCode.substring(0, 6);

        // 2. 保存省级节点（level=1），pcode 为 "0"
        ConfRegion provinceEntity = buildEntity(provinceNode, "0", "/" + provinceCode + "/");
        entities.add(provinceEntity);

        // 3. 检查是否有子节点（市级）
        List<RegionApiResponse.RegionNode> provinceChildren = provinceNode.getChildren();
        if (provinceChildren != null && !provinceChildren.isEmpty()) {
            // 检查子节点中是否包含 level=2 的节点（市/地区）
            boolean hasLevel2 = provinceChildren
                    .stream()
                    .anyMatch(c -> c.getLevel() != null && c.getLevel() == 2);

            if (hasLevel2) {
                // 分离 level=2（地级市）和 level=3（省直辖县级单位）
                List<RegionApiResponse.RegionNode> level2Nodes = new ArrayList<>();
                List<RegionApiResponse.RegionNode> level3Orphans = new ArrayList<>();

                for (RegionApiResponse.RegionNode cityNode : provinceChildren) {
                    if (cityNode.getLevel() != null && cityNode.getLevel() == 2) {
                        level2Nodes.add(cityNode);
                    } else if (cityNode.getLevel() != null && cityNode.getLevel() == 3) {
                        level3Orphans.add(cityNode);
                    } else {
                        log.warn("[跳过] 省级({})直属非level=2/3节点: code={}, name={}, level={}", provinceName, cityNode.getCode(), cityNode.getName(), cityNode.getLevel());
                    }
                }

                // 处理 level=2 地级市
                for (RegionApiResponse.RegionNode cityNode : level2Nodes) {
                    processCity(cityNode, provinceCode, entities);
                }

                // 处理省直辖县级单位（level=3）：按 code 前4位分组创建虚拟市
                if (!level3Orphans.isEmpty()) {
                    Map<String, List<RegionApiResponse.RegionNode>> groups = new LinkedHashMap<>();
                    for (RegionApiResponse.RegionNode orphan : level3Orphans) {
                        String orphanCode = orphan.getCode().substring(0, 6);
                        String groupKey = orphanCode.substring(0, 4) + "00";
                        groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(orphan);
                    }

                    for (Map.Entry<String, List<RegionApiResponse.RegionNode>> entry : groups.entrySet()) {
                        String virtualCityCode = entry.getKey();
                        List<RegionApiResponse.RegionNode> orphans = entry.getValue();

                        ConfRegion virtualCity = new ConfRegion();
                        virtualCity.setCode(virtualCityCode);
                        virtualCity.setPcode(provinceCode);
                        virtualCity.setName("省直辖");
                        virtualCity.setLevel(2);
                        virtualCity.setFullPath(provinceEntity.getFullPath() + virtualCityCode + "/");
                        entities.add(virtualCity);

                        log.info("为省级({})创建虚拟市级节点: code={}, name=省直辖, 包含{}个县级单位", provinceName, virtualCityCode, orphans.size());

                        for (RegionApiResponse.RegionNode orphan : orphans) {
                            processDistrict(orphan, virtualCityCode, entities);
                        }
                    }
                }
            } else {
                // 没有 level=2 的节点（如直辖市、部分自治区），需要补全虚拟市级节点
                // 虚拟市节点：省级编码前2位 + "0100"，name 为 "市辖区"
                String virtualCityCode = provinceCode.substring(0, 2) + "0100";

                ConfRegion virtualCity = new ConfRegion();
                virtualCity.setCode(virtualCityCode);
                virtualCity.setPcode(provinceCode);
                virtualCity.setName("市辖区");
                virtualCity.setLevel(2);
                virtualCity.setFullPath(provinceEntity.getFullPath() + virtualCityCode + "/");
                entities.add(virtualCity);

                // 将所有 level=3 的子节点挂到这个虚拟市下面
                for (RegionApiResponse.RegionNode child : provinceChildren) {
                    if (child.getLevel() != null && child.getLevel() == 3) {
                        processDistrict(child, virtualCityCode, entities);
                    } else {
                        log.warn("[跳过] 省级({})直属非level=3节点: code={}, name={}, level={}", provinceName, child.getCode(), child.getName(), child.getLevel());
                    }
                }
            }
        }
    }

    /**
     * 处理市级节点（level=2）
     */
    private void processCity(RegionApiResponse.RegionNode cityNode, String parentCode, List<ConfRegion> entities) {
        // 行政区划编码只取前6位
        String cityCode = cityNode.getCode().substring(0, 6);

        // 保存市级节点
        ConfRegion cityEntity = buildEntity(cityNode, parentCode, null);
        // 计算 fullPath
        String fullPath = findParentFullPath(parentCode, entities) + cityCode + "/";
        cityEntity.setFullPath(fullPath);
        entities.add(cityEntity);

        // 处理区县级节点（level=3）
        if (cityNode.getChildren() != null) {
            for (RegionApiResponse.RegionNode childNode : cityNode.getChildren()) {
                if (childNode.getLevel() != null && childNode.getLevel() == 3) {
                    processDistrict(childNode, cityCode, entities);
                } else {
                    log.warn("[跳过] 市级({})下非level=3节点: code={}, name={}, level={}", cityNode.getName(), childNode.getCode(), childNode.getName(), childNode.getLevel());
                }
            }
        }
    }

    /**
     * 处理区县级节点（level=3）
     */
    private void processDistrict(RegionApiResponse.RegionNode districtNode, String parentCode, List<ConfRegion> entities) {
        // 行政区划编码只取前6位
        String districtCode = districtNode.getCode().substring(0, 6);

        ConfRegion entity = buildEntity(districtNode, parentCode, null);
        // 计算 fullPath
        String fullPath = findParentFullPath(parentCode, entities) + districtCode + "/";
        entity.setFullPath(fullPath);
        entities.add(entity);
    }

    /**
     * 构建 RegionEntity（不含 fullPath）
     */
    private ConfRegion buildEntity(RegionApiResponse.RegionNode node, String parentCode, String fullPath) {
        ConfRegion entity = new ConfRegion();
        // 行政区划编码只取前6位
        entity.setCode(node.getCode().substring(0, 6));
        entity.setPcode(parentCode);
        entity.setName(node.getName());
        entity.setLevel(node.getLevel());
        entity.setFullPath(fullPath);
        return entity;
    }

    /**
     * 从已存在的实体列表中查找父节点的 fullPath
     */
    private String findParentFullPath(String parentCode, List<ConfRegion> entities) {
        for (ConfRegion entity : entities) {
            if (parentCode.equals(entity.getCode())) {
                return entity.getFullPath();
            }
        }
        // 如果找不到（理论上不会发生），返回根路径
        return "/";
    }

    /**
     * 递归统计 API 返回的行政区划节点总数（含根节点）
     */
    private int countNodes(RegionApiResponse.RegionNode node) {
        if (node == null) {
            return 0;
        }
        int count = 1; // 当前节点
        if (node.getChildren() != null) {
            for (RegionApiResponse.RegionNode child : node.getChildren()) {
                count += countNodes(child);
            }
        }
        return count;
    }
}
