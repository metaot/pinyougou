package cn.itcast.core.controller.order;


import cn.itcast.core.pojo.order.Order;

import cn.itcast.core.pojo.order.OrderValue;
import cn.itcast.core.service.order.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


/**
 * 订单模块
 * @author wophy
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    OrderService orderService;
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Order order){
        return orderService.search(page,rows,order);
    }
    @RequestMapping("/findOne.do")
    public Order findOneById(Long orderId){
        return orderService.findOneById(orderId);
    }



}
