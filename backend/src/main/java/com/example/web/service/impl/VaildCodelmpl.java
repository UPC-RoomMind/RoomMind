package com.example.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.entity.Seat;
import com.example.web.entity.VaildCode;
import com.example.web.mapper.SeatMapper;
import com.example.web.mapper.VaildCodeMapper;
import com.example.web.service.SeatService;
import com.example.web.service.VaildCodeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VaildCodelmpl extends ServiceImpl<VaildCodeMapper, VaildCode> implements VaildCodeSevice {

    @Autowired
    private VaildCodeMapper vaildCodeMapper; // 注入 Mapper 实例

    @Override
    public boolean save(VaildCode vaildCode) {
        return super.save(vaildCode);
    }

    @Override
    public String getCodeByEmail(String email) {
        return vaildCodeMapper.getCodeByEmail(email); // 用注入的对象调用方法
    }

    @Override
    public void saveCode(String email, String code) {
        VaildCode vaildCode = new VaildCode();
        vaildCode.setEmail(email);
        vaildCode.setCode(code);
        // 调用 MyBatis-Plus 的 save 方法，自动插入
        boolean res =  save(vaildCode);
    }
}

