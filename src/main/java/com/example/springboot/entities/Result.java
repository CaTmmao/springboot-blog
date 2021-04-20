package com.example.springboot.entities;

public class Result {
    boolean isLogin;
    String status;
    User data;
    String msg;

    public static Result fail(String msg) {
        return new Result(false, "fail", msg);
    }

    public static Result success(String msg) {
        return new Result(true, "ok", msg);
    }

    public static Result success(String msg, User user) {
        return new Result(true, "ok", msg, user);
    }

    public Result(boolean isLogin, String status) {
        this.isLogin = isLogin;
        this.status = status;
    }

    public Result(boolean isLogin, String status, String msg) {
        this(isLogin, status);
        this.msg = msg;
    }

    public Result(boolean isLogin, String status, String msg, User data) {
        this(isLogin, status, msg);
        this.data = data;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public String getStatus() {
        return status;
    }

    public User getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }
}
