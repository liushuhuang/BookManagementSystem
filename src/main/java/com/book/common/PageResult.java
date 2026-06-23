package com.book.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private Integer pageNo;
    private Integer pageSize;
    private Long total;
    private List<T> items;
}
