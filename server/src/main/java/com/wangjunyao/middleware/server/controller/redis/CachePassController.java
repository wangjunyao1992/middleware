package com.wangjunyao.middleware.server.controller.redis;

import com.wangjunyao.middleware.server.service.redis.CachePassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存 穿透 controller
 */
@RestController
public class CachePassController {

    private static final Logger log = LoggerFactory.getLogger(CachePassController.class);

    private static final String prefix = "cache/pass";

    /**
     * 缓存穿透处理服务类
     */
    @Autowired
    private CachePassService cachePassService;

    /**
     * 获取热销商品信息
     * @param itemCode
     * @return
     */
    @RequestMapping(value = prefix + "/item/info", method = RequestMethod.GET)
    public Map<String, Object> getItem(@RequestParam String itemCode){
        //定义接口返回的格式，主要包括code、msg和data
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "成功");
        try {
            //调用缓存穿透处理服务类得到返回结果，并将其添加进结果Map中
            resultMap.put("data", cachePassService.getItemInfo(itemCode));
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("code", -1);
            resultMap.put("msg", "失败" + e.getMessage());
        }
        return resultMap;
    }

}
