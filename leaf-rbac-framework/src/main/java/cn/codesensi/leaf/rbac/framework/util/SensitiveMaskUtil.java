package cn.codesensi.leaf.rbac.framework.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * 敏感信息脱敏工具类 —— 在日志输出前对敏感字段进行掩码处理。
 * <p>
 * 提供两种脱敏能力：
 * </p>
 * <ul>
 *   <li><b>JSON 字符串脱敏</b>（{@link #maskJson(String, Collection)}）—
 *       解析 JSON 对象/数组，递归遍历所有字段，
 *       将命中敏感列表的文本字段替换为 {@code ***}；</li>
 *   <li><b>Query String 脱敏</b>（{@link #maskQueryString(String, Collection)}）—
 *       对 URL 查询字符串按 {@code &} 和 {@code =} 拆解参数对，
 *       将命中敏感列表的参数值替换为 {@code ***}。</li>
 * </ul>
 * <p>
 * <b>适用场景：</b> 操作日志脱敏密码、身份证号、手机号等敏感字段，
 * 请求日志脱敏 URL 查询参数中的敏感信息。
 * <p>
 * <b>不适用：</b> 非 JSON 格式文本或已 URL-encoded 的 Query String。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
public class SensitiveMaskUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String MASK = "***";

    /**
     * 对 JSON 字符串中指定的敏感字段进行脱敏
     */
    public static String maskJson(String json, Collection<String> sensitiveFields) {
        if (!StringUtils.hasText(json) || sensitiveFields == null || sensitiveFields.isEmpty()) {
            return json;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            maskNode(root, sensitiveFields);
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            log.debug("JSON 脱敏失败，原样返回: {}", e.getMessage());
            return json;
        }
    }

    /**
     * 对 JSON 节点中的敏感字段进行脱敏
     */
    private static void maskNode(JsonNode node, Collection<String> sensitiveFields) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode child = objectNode.get(fieldName);
                if (sensitiveFields.contains(fieldName) && child.isTextual()) {
                    // 对文本类型的敏感字段进行脱敏
                    objectNode.put(fieldName, MASK);
                } else {
                    // 递归处理嵌套对象
                    maskNode(child, sensitiveFields);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                maskNode(arrayItem, sensitiveFields);
            }
        }
        // 其他类型（数字、布尔等）不处理
    }

    /**
     * 对 Query String 中指定的敏感参数进行脱敏
     * 例如：username=admin&password=123456 → username=admin&password=***
     */
    public static String maskQueryString(String query, Collection<String> sensitiveFields) {
        if (!StringUtils.hasText(query) || sensitiveFields == null || sensitiveFields.isEmpty()) {
            return query;
        }
        StringBuilder result = new StringBuilder();
        String[] pairs = query.split("&");
        for (int i = 0; i < pairs.length; i++) {
            String[] kv = pairs[i].split("=", 2);
            String key = kv[0];
            String value = kv.length > 1 ? kv[1] : "";
            // value 为空时不进行脱敏
            if (!value.isBlank() && sensitiveFields.contains(key)) {
                value = MASK;
            }
            result.append(key).append("=").append(value);
            if (i < pairs.length - 1) {
                result.append("&");
            }
        }
        return result.toString();
    }
}
