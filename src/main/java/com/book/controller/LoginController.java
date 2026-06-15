package com.book.controller;

import com.book.common.Result;
import com.book.domain.dto.LoginDto;
import com.book.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public Result<String> login(LoginDto dto) {
        return Result.success(loginService.login(dto));
    }
}
