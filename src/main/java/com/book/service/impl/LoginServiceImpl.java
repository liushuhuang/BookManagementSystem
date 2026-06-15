package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.common.BusinessException;
import com.book.domain.dto.LoginDto;
import com.book.domain.entity.SysUser;
import com.book.service.LoginService;
import com.book.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private SysUserService sysUserService;

    @Override
    public String login(LoginDto  dto) {
        //先检验用户名存不存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",dto.getUsername());
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if(sysUser==null){
            throw new BusinessException("用户不存在或密码错误");
        }


        return "";
    }
}
