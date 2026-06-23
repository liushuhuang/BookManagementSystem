package com.book.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.common.AuthContext;
import com.book.common.BusinessException;
import com.book.common.LoginUser;
import com.book.domain.entity.SysRole;
import com.book.domain.entity.SysUser;
import com.book.domain.entity.SysUserRole;
import com.book.mapper.SysRoleMapper;
import com.book.mapper.SysUserMapper;
import com.book.mapper.SysUserRoleMapper;
import com.book.service.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    public AuthInterceptor(TokenService tokenService,
                           SysUserMapper sysUserMapper,
                           SysUserRoleMapper sysUserRoleMapper,
                           SysRoleMapper sysRoleMapper) {
        this.tokenService = tokenService;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或登录凭证无效");
        }
        Long userId = tokenService.parseUserId(authorization.substring("Bearer ".length()));
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "未登录或登录凭证无效");
        }
        AuthContext.set(new LoginUser(user.getId(), user.getUsername(), user.getRealName(), user.getStatus(), loadRoles(userId)));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private Set<String> loadRoles(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return new HashSet<>();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
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
