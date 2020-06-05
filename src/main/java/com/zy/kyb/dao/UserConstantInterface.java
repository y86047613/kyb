package com.zy.kyb.dao;

/**
 * @program: kyb
 * @description:
 * @author: Mr.yuan zhang
 * @create: 2020-04-30 15:46
 **/

public interface UserConstantInterface {

    // 请求的网址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    // 你的appid
    public static final String WX_LOGIN_APPID = "wxae145c98fea73a09";
    // 你的密匙
    public static final String WX_LOGIN_SECRET = "0c733eba7d1f7f22ed0928d165986ae7";
    // 固定参数
    public static final String WX_LOGIN_GRANT_TYPE = "authorization_code";


}
