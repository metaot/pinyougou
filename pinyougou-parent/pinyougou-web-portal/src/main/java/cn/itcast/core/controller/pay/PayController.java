package cn.itcast.core.controller.pay;


import cn.itcast.core.service.pay.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author wophy
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private PayService payService;

    @RequestMapping("/createNative.do")
    public Map<String, String> createNative() {
        //获取登录用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map resultMap = payService.createNative(userId);
        //System.out.println(resultMap.toString());
        return resultMap;

    }
    //    public Result pay/queryPayStatus.do?out_trade_no='+out_trade_no
    @RequestMapping("/queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no){
        int i = 0;
        while (true){

            Map map = payService.queryPayStatus(out_trade_no);
            if (map==null){
                return new Result(false,"支付失败");
            }

            if(map.get("trade_state").equals("SUCCESS")){
                //更新支付日志
                return new Result(true,"支付成功");
            }

            try {
                //每隔3s查询一次
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            i++;

            if(i>5){
                return new Result(false,"二维码超时");
            }
        }
    }

}
