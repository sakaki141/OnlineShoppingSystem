package com.example.onlineshoppingsystem.controller;

import com.example.onlineshoppingsystem.exception.Result;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    protected <T> Result<T> success() {
        return Result.success(null);
    }

    protected <T> Result<T> error(String message) {
        return Result.error(message);
    }
}
