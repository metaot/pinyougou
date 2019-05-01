package cn.itcast.core.service.collect;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectServiceImpl implements CollectService {

    @Resource
    private UserDao userDao;

    @Resource
    private ItemDao itemDao;

    @Override
    public List<OrderItem> showCollect(String seller_id) {
        List orderItems=new ArrayList();

       List<Long> itemIds=userDao.selectItemId(seller_id);

       if(itemIds!=null&&itemIds.size()>0){
           for (Long itemId : itemIds) {
               Item item = itemDao.selectByPrimaryKey(itemId);

               orderItems.add(item);
           }
       }

        return orderItems;
    }
}
