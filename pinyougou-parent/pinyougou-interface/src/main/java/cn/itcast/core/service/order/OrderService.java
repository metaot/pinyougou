package cn.itcast.core.service.order;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderCount;
import cn.itcast.core.pojo.order.OrderValue;
import entity.PageResult;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

/**
 * @author wophy
 */
public interface OrderService {
    void add(Order order,String username);

    /**
     * 订单查询
     * @param page
     * @param rows
     * @param order
     * @return
     */
    public PageResult search(Integer page, Integer rows, Order order);

    Order findOneById(Long orderId);




    public OrderCount conutOrder(OrderValue orderValue) throws ParseException;

    public HashMap<String, List> findView(String year);

}
