package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 * unknown-未知
 * insert-新增
 * update-更新
 * query-查询
 * delete-删除
 */
@Getter
public enum OperateType implements BaseEnum<String> {

    UNKNOWN("unknown", "未知"),
    INSERT("insert", "新增"),
    UPDATE("update", "更新"),
    QUERY("query", "查询"),
    DELETE("delete", "删除"),
    ;

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;

    /**
     * 枚举构造函数
     *
     * @param code 编码
     * @param desc 说明
     */
    OperateType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
