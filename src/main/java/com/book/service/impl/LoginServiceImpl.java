package com.book.service.impl;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.common.BusinessException;
import com.book.domain.dto.LoginDto;
import com.book.domain.entity.SysUser;
import com.book.service.LoginService;
import com.book.service.SysUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    private final SysUserService sysUserService;
    private final String tokenSecret;

    public LoginServiceImpl(SysUserService sysUserService,
                            @Value("${book.security.jwt-secret}") String tokenSecret) {
        this.sysUserService = sysUserService;
        this.tokenSecret = tokenSecret;
    }

    @Override
    public String login(LoginDto  dto) {
        //先检验用户名存不存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",dto.getUsername());
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if(sysUser==null){
            throw new BusinessException("用户不存在或密码错误");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(dto.getPassword(),sysUser.getPassword())){
            throw new BusinessException("用户不存在或密码错误");
        }
        //如果都检验成功了就可以生成jwt的token了
        Map<String,Object> map = new HashMap<>();
        map.put("uid",sysUser.getId());
        return JWTUtil.createToken(map,tokenSecret.getBytes(StandardCharsets.UTF_8));
    }


}
