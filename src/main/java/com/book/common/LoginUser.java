package com.book.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long id;
    private String username;
    private String realName;
    private Integer status;
    private Set<String> roles = new HashSet<>();

    public boolean hasAnyRole(String... roleCodes) {
        if (roles == null) {
            return false;
        }
        for (String roleCode : roleCodes) {
            if (roles.contains(roleCode)) {
                return true;
            }
        }
        return false;
    }
}
