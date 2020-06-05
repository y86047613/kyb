package com.zy.kyb.dto;

import lombok.Data;

@Data
public class WxLoginResultDTO {
    private String openid;
    private String session_key;
    private String errcode;
    private String errmsg;
}
