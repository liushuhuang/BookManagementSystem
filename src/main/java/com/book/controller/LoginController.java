package com.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.common.AuthContext;
import com.book.common.BusinessException;
import com.book.common.LoginUser;
import com.book.common.Result;
import com.book.domain.dto.LoginRequest;
import com.book.domain.dto.LoginResponse;
import com.book.domain.dto.UserInfoVo;
import com.book.domain.entity.SysRole;
import com.book.domain.entity.SysUser;
import com.book.domain.entity.SysUserRole;
import com.book.mapper.SysRoleMapper;
import com.book.mapper.SysUserRoleMapper;
import com.book.service.SysUserService;
import com.book.service.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final SysUserService sysUserService;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginController(SysUserService sysUserService,
                           SysUserRoleMapper sysUserRoleMapper,
                           SysRoleMapper sysRoleMapper,
                           TokenService tokenService) {
        this.sysUserService = sysUserService;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        SysUser user = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername())
        );
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户不存在或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(403, "用户已禁用");
        }
        Set<String> roles = loadRoles(user.getId());
        LoginResponse response = new LoginResponse(
                tokenService.createToken(user),
                "Bearer",
                new UserInfoVo(user.getId(), user.getUsername(), user.getRealName(), roles)
        );
        return Result.success(response);
    }

    @GetMapping("/me")
    public Result<UserInfoVo> me() {
        LoginUser user = AuthContext.require();
        return Result.success(new UserInfoVo(user.getId(), user.getUsername(), user.getRealName(), user.getRoles()));
    }

    private Set<String> loadRoles(Long userId) {
        List<Long> roleIds = sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
                )
                .stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return sysRoleMapper.selectList(
                        new LambdaQueryWrapper<SysRole>()
                                .in(SysRole::getId, roleIds)
                                .eq(SysRole::getStatus, 1)
                )
                .stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toSet());
    }
}
