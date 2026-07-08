package com.example.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.entity.VaildCode;

public interface VaildCodeSevice extends IService<VaildCode> {
    String getCodeByEmail(String email);
    void saveCode(String email, String code);
}
