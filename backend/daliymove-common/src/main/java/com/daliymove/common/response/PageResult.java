package com.daliymove.common.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果封装类
 * - 封装分页查询的结果数据
 * - 提供静态方法用于快速构建分页结果
 */
@Data
public class PageResult<T> implements Serializable {

    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;

    /**
     * 从 IPage 构建分页结果（不转换类型）
     * @param page 分页数据
     * @return 分页结果
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        result.setPages(page.getPages());
        return result;
    }

    /**
     * 从 IPage 构建分页结果（带类型转换）
     * @param page 分页数据
     * @param converter 类型转换函数
     * @return 分页结果
     */
    public static <S, T> PageResult<T> of(IPage<S> page, Function<S, T> converter) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords().stream()
            .map(converter)
            .collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        result.setPages(page.getPages());
        return result;
    }
}