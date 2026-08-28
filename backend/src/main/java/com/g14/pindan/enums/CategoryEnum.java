package com.g14.pindan.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 拼单分类枚举
 * 数据库 category_tag 存 label；前端下拉展示 label
 */
public enum CategoryEnum {

    FRUIT("水果"),
    MILK_TEA("奶茶"),
    MOVIE("电影票"),
    CARPOOL("拼车"),
    OTHER("其他");

    private final String label;

    CategoryEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 校验 label 是否合法
     */
    public static boolean isValid(String label) {
        return Arrays.stream(values()).anyMatch(c -> c.label.equals(label));
    }

    /**
     * 全部分类（含 code + label，供前端下拉）
     */
    public static List<CategoryEnum> all() {
        return Arrays.asList(values());
    }
}
