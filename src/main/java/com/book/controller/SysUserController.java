package com.book.controller;

import com.book.common.BusinessException;
import com.book.common.Result;
import com.book.domain.entity.SysUser;
import com.book.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }



    @PostMapping("add")
    public Result<Integer> save(@RequestBody @Validated SysUser sysUser) {
        //先给密码加一下密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        if (!sysUserService.save(sysUser)){
            throw new BusinessException("用户新增错误");
        }
        return Result.success();
    }
}
