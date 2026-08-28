package com.g14.pindan.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拼单实体
 */
@Data
public class Groupon {

    private Integer id;
    private Integer publisherId;
    private String title;
    private String categoryTag;
    private String description;

    /** 目标人数（含团长） */
    private Integer targetCount;

    /** 当前人数（含团长） */
    private Integer currentCount;

    private LocalDateTime deadline;

    /** 0招募中 1已成团 2已结束 3已取消 */
    private Integer status;

    private LocalDateTime createdAt;
}
