package cn.codesensi.leaf.rbac.framework.util;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.InvalidConfigException;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.xdb.XdbException;

import java.io.IOException;
import java.io.InputStream;

/**
 * ip2region工具类
 */

@Slf4j
public class Ip2regionUtil {

    private static Ip2Region ip2Region;

    /*
      加载到内存中
     */
    static {
        try {
            InputStream v4Is = ResourceUtil.getStream("ip2region_v4.xdb");
            Config v4Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(15)
                    .setXdbInputStream(v4Is)
                    .asV4();
            InputStream v6Is = ResourceUtil.getStream("ip2region_v6.xdb");
            Config v6Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(15)
                    .setXdbInputStream(v6Is)
                    .asV6();
            ip2Region = Ip2Region.create(v4Config, v6Config);
        } catch (IOException | XdbException | InvalidConfigException ignored) {
        }
    }

    /**
     * 查询IP对应的地区
     */
    public static String search(String ip) {
        try {
            String search = ip2Region.search(ip);
            if (search.contains("Reserved")) {
                return "内网";
            }
            // 去掉 |0 及 0|
            search = search.replace("|0", "").replace("0|", "");
            // 去掉最后一个 | 及后边的内容
            search = search.substring(0, search.lastIndexOf("|"));
            return search;
        } catch (Exception ignored) {
        }
        return "未知";
    }

}
