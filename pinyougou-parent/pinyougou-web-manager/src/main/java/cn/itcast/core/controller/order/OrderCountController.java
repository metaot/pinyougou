package cn.itcast.core.controller.order;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderCount;
import cn.itcast.core.pojo.order.OrderCountOfEveryDay;
import cn.itcast.core.pojo.order.OrderValue;
import cn.itcast.core.service.order.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import entity.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orderCount")
public class OrderCountController {



    @Reference
    OrderService orderService;
    @RequestMapping("/searchCount.do")
    public Map countOrder(@RequestBody OrderValue orderValue) throws ParseException {

        OrderCount orderCount = orderService.conutOrder(orderValue);

        List<OrderCountOfEveryDay> everyDayList = orderCount.getEveryDay();
        OrderCount count = new OrderCount();
        count.setTotleRows(orderCount.getTotleRows());
        count.setTotleMoney(orderCount.getTotleMoney());
        JSON.toJSONString(everyDayList, SerializerFeature.DisableCircularReferenceDetect);
        Map map = new HashMap(16);
        map.put("countP",count);
        map.put("everyDayList",everyDayList);

        return map;
    }
}
