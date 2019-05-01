package cn.itcast.core.controller.order;

import cn.itcast.core.pojo.order.OrderView;
import cn.itcast.core.pojo.order.TestlineData;
import cn.itcast.core.service.order.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author 15000
 */
@RestController
@RequestMapping("/orderView")
public class OrderViewController {

    @Reference
    private OrderService orderService;
    @RequestMapping("/searchView.do")
    public Object getView(){
        Integer[] data1 = {120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90};
        Integer[] data2 = {200, 232, 110, 144, 90, 300, 90,200,300,150,200,260};
        Integer[] data3 = {180, 120, 161, 84, 360, 230, 100,230,280,240,260,120};
        Integer[][] json = {data1,data2,data3};
       // return orderService.findView();
        return json;
    }

    @RequestMapping("/searchView2.do")
    public Map getView2(){
       return orderService.findView("2017");
    }

}
