package cn.codesensi.leaf.rbac.system.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 民政部行政区划 API 响应
 * 用于接收 <a href="https://dmfw.mca.gov.cn/9095/xzqh/getList">...</a> 的返回数据
 */
@Data
public class RegionApiResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 行政区划节点
     */
    private RegionNode data;

    /**
     * 行政区划节点
     */
    @Data
    public static class RegionNode implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 行政区划编码（12位或13位，只取前6位使用）
         */
        private String code;

        /**
         * 行政区划名称
         */
        private String name;

        /**
         * 层级: 1-省, 2-市, 3-县（区）
         */
        private Integer level;

        /**
         * 类型（如：省、市、区）
         */
        private String type;

        /**
         * 子节点列表
         */
        private List<RegionNode> children;
    }

}
