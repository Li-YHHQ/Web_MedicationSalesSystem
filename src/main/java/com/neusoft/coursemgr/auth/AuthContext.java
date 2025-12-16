package com.neusoft.coursemgr.auth;

public final class AuthContext {

    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    public static AuthUser get() {
        return HOLDER.get();
    }

    public static AuthUser require() {
        AuthUser u = HOLDER.get();
        if (u == null) {
            throw new IllegalStateException("No authenticated user in context");
        }
        return u;
    }

    public static void clear() {
        HOLDER.remove();
    }

    public record AuthUser(Long userId, String role) {
    }
}
