package com.book.common;

public final class AuthContext {

    private static final ThreadLocal<LoginUser> CURRENT = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(LoginUser loginUser) {
        CURRENT.set(loginUser);
    }

    public static LoginUser get() {
        return CURRENT.get();
    }

    public static LoginUser require() {
        LoginUser loginUser = CURRENT.get();
        if (loginUser == null) {
            throw new BusinessException(401, "未登录或登录凭证无效");
        }
        return loginUser;
    }

    public static LoginUser requireManager() {
        LoginUser loginUser = require();
        if (!loginUser.hasAnyRole("admin", "librarian")) {
            throw new BusinessException(403, "无权限访问");
        }
        return loginUser;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
