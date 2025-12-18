package com.zjsu.yyd.acgproductservice.model;

import com.zjsu.yyd.acgproductservice.model.AcgProduct;
import lombok.Data;

import java.util.List;

@Data
public class AcgProductPageDto {
    private long totalElements; // 总记录数
    private int totalPages;     // 总页数
    private int page;           // 当前页码
    private int size;           // 每页大小
    private List<AcgProduct> content; // 当前页数据
}
