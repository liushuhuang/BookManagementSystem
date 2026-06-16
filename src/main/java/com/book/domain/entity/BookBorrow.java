package com.book.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("book_borrow")
public class BookBorrow {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;

    private Long userId;

    private LocalDateTime borrowTime;

    private LocalDateTime dueTime;

    private LocalDateTime returnTime;

    private Integer status;

    private BigDecimal fineAmount;

    private Integer fineStatus;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
