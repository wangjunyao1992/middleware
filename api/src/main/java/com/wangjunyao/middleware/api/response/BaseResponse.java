package com.wangjunyao.middleware.api.response;

import com.wangjunyao.middleware.api.enums.StatusCode;
import lombok.Data;

/**
 * 响应信息类
 */
@Data
public class BaseResponse<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 描述信息
     */
    private String msg;

    /**
     * 响应数据 - 采用泛型表示可以接受通用的数据类型
     */
    private T data;

    public static BaseResponse build(StatusCode statusCode){
        BaseResponse response = new BaseResponse();
        response.code = statusCode.getCode();
        response.msg = statusCode.getMsg();
        return response;
    }

    public static <T> BaseResponse build(StatusCode statusCode, T data){
        BaseResponse response = build(statusCode);
        response.data = data;
        return response;
    }

}
