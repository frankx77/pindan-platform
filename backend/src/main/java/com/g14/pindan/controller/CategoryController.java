package com.g14.pindan.controller;

import com.g14.pindan.common.Result;
import com.g14.pindan.enums.CategoryEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类接口（枚举常量约束分类，避免联表查询）
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @GetMapping("/list")
    public Result<List<String>> list() {
        return Result.success(
                CategoryEnum.all().stream()
                        .map(CategoryEnum::getLabel)
                        .collect(Collectors.toList())
        );
    }
}
