package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 操作类型
 * 0-未知
 * 1-新增
 * 2-更新
 * 3-查询
 * 4-删除
 */
@Getter
public enum OperateType {
    UNKNOWN(0, "未知"),
    INSERT(1, "新增"),
    UPDATE(2, "更新"),
    QUERY(3, "查询"),
    DELETE(4, "删除"),
    ;

    /**
     * 操作类型编码
     */
    private final Integer code;

    /**
     * 操作类型说明
     */
    private final String msg;

    OperateType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
