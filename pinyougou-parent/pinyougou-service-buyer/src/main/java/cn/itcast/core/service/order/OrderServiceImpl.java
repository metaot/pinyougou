package cn.itcast.core.service.order;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.*;
import cn.itcast.core.utils.uniquekey.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void add(Order order, String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
        Double payLogTotalFee = 0d;
        ArrayList<String> orderIdList = new ArrayList<>();
        //保存订单 根据商家分类 保存到订单表
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                double payment = 0;
                long orderId = idWorker.nextId();
                orderIdList.add(orderId + "");
                //封装主键
                order.setOrderId(orderId);
                //设置付款状态
                order.setStatus("1");
                //订单创建时间
                order.setCreateTime(new Date());
                //订单更新时间
                order.setUpdateTime(new Date());
                //下单用户 ()
                order.setUserId(username);
                //订单来源
                order.setSourceType("2");

                //设置金额


                //保存订单明细
                //保存每一个购物项
                List<OrderItem> orderItemList = cart.getOrderItemList();
                for (OrderItem orderItem : orderItemList) {
                    //设置商品的id
                    orderItem.setId(idWorker.nextId());
                    Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                    //设施商品id
                    orderItem.setGoodsId(item.getGoodsId());
                    //设置订单id
                    orderItem.setOrderId(orderId);
                    //设置图片地址
                    orderItem.setPicPath(item.getImage());
                    //设置单价
                    orderItem.setPrice(item.getPrice());
                    //设置数量

                    //设置购买总金额
                    double totalFee = item.getPrice().doubleValue() * orderItem.getNum();
                    payment += totalFee;
                    //支付总金额 用于支付页面显示  日志保存
                    payLogTotalFee += totalFee;
                    orderItem.setTotalFee(new BigDecimal(totalFee));
                    orderItem.setSellerId(item.getSellerId());  // 商家id
                    orderItemDao.insertSelective(orderItem);
                }
                // 设置该商家下的总金额
                order.setPayment(new BigDecimal(payment));
                orderDao.insertSelective(order);
            }
        }
        //保存支付日志
        PayLog payLog = new PayLog();
        //下单时间
        payLog.setCreateTime(new Date());
        //订单编号
        payLog.setOutTradeNo(idWorker.nextId() + "");
        //支付金额
        payLog.setTotalFee(payLogTotalFee.longValue());
        //userId
        payLog.setUserId(username);
        //交易状态 0 未付款
        payLog.setTradeState("0");
        //订单编号列表
        payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", ""));

        //支付类型 完成时间  交易号码  交易状态  在支付时更新
        payLogDao.insertSelective(payLog);
        //保存在redis中
        redisTemplate.boundHashOps("payLog").put(username, payLog);
        // 删除购物车
        redisTemplate.boundHashOps("BUYER_CART").delete(username);


    }

    /**
     * 订单查询
     *
     * @param page
     * @param rows
     * @param order
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Order order) {
        //设置分页
        PageHelper.startPage(page, rows);

        OrderQuery query = new OrderQuery();
        OrderQuery.Criteria criteria = query.createCriteria();
        //封装查询条件
        //根据订单id
        if (order.getOrderId() != null && !"".equals(order.getOrderId().toString().trim())) {
            criteria.andOrderIdEqualTo(order.getOrderId());
        }
        if (order.getBuyerNick() != null && !"".equals(order.getBuyerNick().trim())) {
            criteria.andBuyerNickEqualTo(order.getBuyerNick());
        }
        if (order.getStatus() != null && !"".equals(order.getStatus().trim())) {
            //  if (order.getStatus().equals(""))
            criteria.andStatusEqualTo(order.getStatus());
        }

        // query.setOrderByClause("orderId desc");
        Page<Order> p = (Page<Order>) orderDao.selectByExample(query);
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public Order findOneById(Long orderId) {
        return orderDao.selectByPrimaryKey(orderId);

    }


    /**
     * 订单统计
     *
     * @return
     */
    @Override
    public OrderCount conutOrder(OrderValue orderValue) throws ParseException {
        //TODO 订单的统计
        //设置分页

        String countTime = orderValue.getCountTime();
        String paymentType = orderValue.getPaymentType();
        String status = orderValue.getStatus();
        String endTime = orderValue.getEndTime();
        String startTime = orderValue.getStartTime();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        OrderQuery query = new OrderQuery();
        OrderQuery.Criteria criteria = query.createCriteria();
        //封装查询条件
        if (orderValue.getStatus() != null && !"".equals(orderValue.getStatus().trim())) {
            criteria.andStatusEqualTo(orderValue.getStatus());
        }
        //支付类型
        if (orderValue.getPaymentType() != null && !"".equals(orderValue.getPaymentType().trim())) {
            criteria.andPaymentTypeEqualTo(orderValue.getPaymentType());
        }
        //
        if (countTime != null && !"".equals(countTime.trim())) {
            //点击了具体时间
            if (countTime.equals("1day")) {
                Date date = dfDate(1);
                criteria.andCreateTimeGreaterThan(date);
            } else if (countTime.equals("7day")) {
                Date date = dfDate(7);
                criteria.andCreateTimeGreaterThan(date);
            } else if (countTime.equals("30day")) {
                Date date = dfDate(30);
                criteria.andCreateTimeGreaterThan(date);
            }
        } else if (startTime!=null&&endTime!=null&&!"".equals(startTime.trim())&&!"".equals(endTime.trim())){
            //没有设定前几天时间

            Date startDate = df.parse(startTime);
            long startDateTime = startDate.getTime();

            Date endDate = df.parse(endTime);
            long endDateTime = endDate.getTime();
            Long time = (startDateTime - endDateTime) / (24 * 60 * 60 * 1000);
            if (time > 1) {
                criteria.andCreateTimeBetween(startDate, endDate);
            }
        }
        query.setOrderByClause("create_time desc");
        List<Order> orders = orderDao.selectByExample(query);

        //总的统计数据
        OrderCount count = new OrderCount();

        //每天的统计数据
        List<OrderCountOfEveryDay> everyDayList = new ArrayList<>();
        double totleMoney = 0.0;
        //总条数
        Long totleRows = orders.size() + 0L;
        if (orders.size()>0) {
            for (Order order1 : orders) {
                //总金额
                totleMoney += order1.getPayment().doubleValue();
                count.setTotleMoney(totleMoney);
                //设置总条数
                count.setTotleRows(totleRows);
                Date date = order1.getCreateTime();
                String time = df.format(date);
                String[] strings = time.split(" ");
                //判断是否包含这一天
                OrderCountOfEveryDay orderCountOfEveryDay = new OrderCountOfEveryDay();
                orderCountOfEveryDay.setDaytime(strings[0]);
                //判断是否包含这个时间段
                int indexOf = everyDayList.indexOf(orderCountOfEveryDay);
                if (indexOf != -1) {
                    //包含 取出 累加
                    orderCountOfEveryDay = everyDayList.get(indexOf);
                    everyDayList.remove(indexOf);
                    orderCountOfEveryDay.setDaytime(strings[0]);
                    orderCountOfEveryDay.setTotleDayMoney(orderCountOfEveryDay.getTotleDayMoney() + order1.getPayment().doubleValue());

                    orderCountOfEveryDay.setDayRows(orderCountOfEveryDay.getDayRows() + 1L);

                    orderCountOfEveryDay.setAvgDayMoney(orderCountOfEveryDay.getTotleDayMoney()/orderCountOfEveryDay.getDayRows());
                } else {
                    //不包含
                    orderCountOfEveryDay.setDaytime((strings[0]));
                    orderCountOfEveryDay.setDayRows(1L);
                    orderCountOfEveryDay.setTotleDayMoney(order1.getPayment().doubleValue());
                    orderCountOfEveryDay.setAvgDayMoney(order1.getPayment().doubleValue());
                }
                everyDayList.add(orderCountOfEveryDay);
            }
        }
        count.setEveryDay(everyDayList);
        return count;
    }

    @Override
    public HashMap<String, List> findView(String year) {

        List<OrderView> viewList1 = orderDao.selectOrderForView("2017");
        List<OrderView> viewList2= orderDao.selectOrderForView("2018");
        List<OrderView> viewList3 = orderDao.selectOrderForView("2019");

        HashMap<String, List> map = new HashMap<>();
        map.put("2017",viewList1);
        map.put("2018",viewList2);
        map.put("2019",viewList3);
        return map;
    }


    public static Date dfDate(Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -day);//得到前N天
        Date date = calendar.getTime();
        // return calendar;
        return date;
    }
}
