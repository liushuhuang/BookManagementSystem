package com.book.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo {
    private Long id;
    private String username;
    private String realName;
    private Set<String> roles;
}
