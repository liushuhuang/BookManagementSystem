package com.book.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liushuhuang
 * @date 2026/6/15
 *
 */
@Data
public class LoginDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;


    /**
     * 密码
     */
    private String password;
}
