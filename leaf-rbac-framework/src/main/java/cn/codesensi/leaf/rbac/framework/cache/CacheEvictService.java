package cn.codesensi.leaf.rbac.framework.cache;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 缓存清除服务
 */
@Service
public class CacheEvictService {

    /**
     * 清除用户权限缓存
     *
     * @param userId 用户ID
     */
    @CacheEvict(value = CacheConst.USER_PERM, key = "#userId")
    public void clearUserPermCache(Long userId) {
        // 空方法，仅用于清除用户权限缓存
    }

    /**
     * 清空行政区划缓存
     */
    @CacheEvict(value = CacheConst.REGION_PCODE, allEntries = true)
    public void clearRegionPcodeCache() {
        // 空方法，仅用于清除行政区划缓存
    }

    /**
     * 清除用户角色缓存
     *
     * @param userId 用户ID
     */
    @CacheEvict(value = CacheConst.USER_ROLE, key = "#userId")
    public void clearUserRoleCache(Long userId) {
        // 空方法，仅用于清除用户角色缓存
    }

    /**
     * 清除用户菜单缓存
     *
     * @param userId 用户ID
     */
    @CacheEvict(value = CacheConst.USER_MENU, key = "#userId")
    public void clearUserMenuCache(Long userId) {
        // 空方法，仅用于清除用户菜单缓存
    }

    /**
     * 清除用户信息缓存
     *
     * @param userId 用户ID
     */
    @CacheEvict(value = CacheConst.USER_INFO, key = "#userId")
    public void clearUserInfoCache(Long userId) {
        // 空方法，仅用于清除用户信息缓存
    }

    /**
     * 清空字典缓存
     */
    @CacheEvict(value = CacheConst.DICT, allEntries = true)
    public void clearDictCache() {
        // 空方法，仅用于清除字典缓存
    }
}
