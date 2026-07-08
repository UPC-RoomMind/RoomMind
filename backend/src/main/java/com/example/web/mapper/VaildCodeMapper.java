package com.example.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.example.web.entity.*;

public interface VaildCodeMapper extends BaseMapper<VaildCode> {
    @Select("SELECT code FROM Vaildcode WHERE email = #{email}")
    String getCodeByEmail(@Param("email") String email);

}
