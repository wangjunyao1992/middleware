package com.wangjunyao.middleware.server.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedPacketUtilTest {

    private static final Logger log = LoggerFactory.getLogger(RedPacketUtilTest.class);

    /**
     * 二倍均值法 测试
     */
    @Test
    public void divideRedPackageTest(){
        //总金额单位为分，总金额为1000分，即10元
        Integer amount = 1000;
        //总人数即红包总个数，在这里假设为10个
        Integer total = 10;
        //调用二倍均值法产生随机金额
        List<Integer> list = RedPackedUtil.divideRedPackage(amount, total);
        log.info("总金额={}分，总个数={}个", amount, total);
        //用于统计生成的随机金额之和是否等于总金额
        Integer sum = 0;
        //遍历输出每个随机金额
        for (Integer i : list){
            log.info("随机金额为：{}分，即{}元", i, new BigDecimal(i.toString()).divide(new BigDecimal(100)));
            sum += i;
        }
        log.info("所有随机金额叠加之和={}分", sum);
    }

}
