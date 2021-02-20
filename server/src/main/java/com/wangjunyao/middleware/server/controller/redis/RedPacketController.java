package com.wangjunyao.middleware.server.controller.redis;

import com.wangjunyao.middleware.api.enums.StatusCode;
import com.wangjunyao.middleware.api.response.BaseResponse;
import com.wangjunyao.middleware.server.dto.RedPacketDto;
import com.wangjunyao.middleware.server.service.IRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 红包处理逻辑controller
 */
@RestController
@RequestMapping(value = "red/packet")
public class RedPacketController {

    private static final Logger log = LoggerFactory.getLogger(RedPacketController.class);

    @Autowired
    private IRedPacketService redPacketService;

    /**
     * 发红包业务逻辑
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = "hand/out", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse handOut(@Validated @RequestBody RedPacketDto dto, BindingResult result){
        //参数校验
        if (result.hasErrors()){
            return BaseResponse.build(StatusCode.INVALID_PARAMS);
        }
        try {
            //核心业务逻辑处理服务 - 最终返回红包全局唯一标识串
            String redId = redPacketService.handOut(dto);
            return BaseResponse.build(StatusCode.SUCCESS, redId);
        }catch (Exception e){
            log.error("发红包发生异常：dto={}", dto, e);
            return BaseResponse.build(StatusCode.FAIL);
        }
    }

    /**
     * 抢红包
     * @param userId
     * @param redId
     * @return
     */
    @RequestMapping(value = "rob", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse rob(@RequestParam Integer userId, @RequestParam String redId){
        try{
            //调用红包业务逻辑处理接口中的抢红包方法，最终返回抢到的红包金额
            //单位为元（不为null时表示抢到了，否则代表已经被抢完了）
            BigDecimal result = redPacketService.rob(userId, redId);
            if (result != null){
                return BaseResponse.build(StatusCode.SUCCESS, result);
            }else{
                //没有抢到红包，即已经被抢完了
                return BaseResponse.build(StatusCode.FAIL, "红包已经抢完");
            }
        }catch (Exception e){
            //处理过程如果发生异常，则打印异常信息，并返回给前端
            log.error("抢红包发生异常：userId={} redId={}", userId, redId, e);
            return BaseResponse.build(StatusCode.FAIL, e.getMessage());
        }
    }

}
