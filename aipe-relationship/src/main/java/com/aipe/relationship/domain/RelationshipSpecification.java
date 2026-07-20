package com.aipe.relationship.domain;

/**
 * Relationship 规格校验
 *
 * <p>封装 Relationship 级业务校验规则（Domain Law-005：Aggregate Root Enforces Consistency）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class RelationshipSpecification {

    private RelationshipSpecification() {
        // 工具类，禁止实例化
    }

    /**
     * 校验 Relationship 是否可以创建
     *
     * @param relationship 待校验
     * @throws IllegalArgumentException 校验失败
     */
    public static void validateForCreate(Relationship relationship) {
        if (relationship == null) {
            throw new IllegalArgumentException("Relationship cannot be null");
        }
        relationship.validate();
    }

    /**
     * 校验 Relationship 是否构成循环依赖（A→B→C→A）
     *
     * <p>简单检测：source == target 已在 validate() 中拒绝；
     * 完整循环检测需要图遍历，由 ApplicationService 在创建时调用 GraphTraversal 完成。
     *
     * @param relationship 待校验
     * @return 是否合法（此处仅做基础校验）
     */
    public static boolean isValidBasic(Relationship relationship) {
        if (relationship == null) {
            return false;
        }
        try {
            relationship.validate();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
