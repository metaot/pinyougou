package cn.itcast.core.service.collect;

import cn.itcast.core.pojo.order.OrderItem;

import java.util.List;

public interface CollectService {
    //显示收藏商品
    List<OrderItem> showCollect(String seller_id);
}
