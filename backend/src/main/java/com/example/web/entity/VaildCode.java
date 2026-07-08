package com.example.web.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("vaildcode")
public class VaildCode {

    @TableId(type = IdType.AUTO)
    private Integer Id;


    @JsonProperty("email")
    @TableField(value = "email", updateStrategy = FieldStrategy.IGNORED)
    private String email;

    @JsonProperty("code")
    @TableField(value = "code", updateStrategy = FieldStrategy.IGNORED)
    private String code;
}
