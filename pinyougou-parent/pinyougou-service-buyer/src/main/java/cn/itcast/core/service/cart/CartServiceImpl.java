package cn.itcast.core.service.cart;


import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.seller.Seller;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


/**
 * @author 15000
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 获取库存对象
     *
     * @param id
     * @return
     */
    @Override
    public Item findOne(Long id) {
        return itemDao.selectByPrimaryKey(id);
    }

    /**
     * 保存购物车数据回显
     *
     * @param cartList cookie中取出
     */
    @Override
    public List<Cart> setAttrituteForCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            // 填充商家的店铺名称
            Seller seller = sellerDao.selectByPrimaryKey(cart.getSellerId());
            cart.setSellerName(seller.getNickName());
            // 填充购物项的数据：图片、标题、单价、计算出小计
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                orderItem.setPicPath(item.getImage());  // 商品图片
                orderItem.setTitle(item.getTitle());    // 商品标题
                orderItem.setPrice(item.getPrice());    // 商品单价
                // 小计 = 单价 * 数量
                BigDecimal totalFee = new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum());
                orderItem.setTotalFee(totalFee);        // 商品小计
            }
        }
        return cartList;
    }

    /**
     * 将购物车同步到redis中
     *
     * @param username
     * @param
     */
    @Override
    public void mergeCartList(String username, List<Cart> newCartList) {
        //取出用户在redis中的购物车
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
        //2.将cookie中的购物车与redis中购物车合并
        oldCartList = mergeNewCartListToOldCartList(newCartList, oldCartList);
        //3.将合并后的购物车保存在redis中
        redisTemplate.boundHashOps("BUYER_CART").put(username, oldCartList);

    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        //从redis取出cartList
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);

        return cartList;
    }

    /**
     * 单个订单项的收藏
     * @param itemId
     * @param username
     */
    @Transactional
    @Override
    public void addCollect(Long itemId, String username)  {
            orderItemDao.updateIsCollectByItemId(username, itemId);
    }

    //查看商品是否被添加
    @Override
    public boolean checkCollect(Long itemId, String username) {
        long i=orderItemDao.checkCollect(itemId,username);

        if(i!=0){
            return false;
        }else {
            return true;
        }
    }
    /**
     * cookie 和redis的购物车合并
     *
     * @param newCartList
     * @param oldCartList
     * @return
     */
    private List<Cart> mergeNewCartListToOldCartList(List<Cart> newCartList, List<Cart> oldCartList) {

        if (newCartList != null) {
            if (oldCartList!=null){
                //老车新车都不为空 将新车里面的cart合并到老车中
                for (Cart newCart : newCartList) {
                    //判断是否为同一个商家
                    int sellerIndexOf = oldCartList.indexOf(newCart);
                    if (sellerIndexOf!=-1){
                        //是同一个商家的 继续判断是不是同一个商品
                            //取出老车中的购物项
                        List<OrderItem> oldOrderItemList = oldCartList.get(sellerIndexOf).getOrderItemList();
                            //取车新车中的购物项
                        List<OrderItem> newOrderItemList = newCart.getOrderItemList();
                        for (OrderItem newOrderItem : newOrderItemList) {
                            int orderItemindexof = oldOrderItemList.indexOf(newOrderItem);
                            if (orderItemindexof!=-1){
                                //旧的购物项中包含新的购物项  数量相加
                                OrderItem oldOrderItem = oldOrderItemList.get(orderItemindexof);
                                oldOrderItem.setNum(oldOrderItem.getNum()+newOrderItem.getNum());
                            }else{
                                //购物项中不包含新车的这个商品(SKU) 直接添加
                                oldOrderItemList.add(newOrderItem);
                            }
                        }
                    }else{
                        //不是同一个上商家的 直接添加到老车中
                        oldCartList.add(newCart);
                    }
                }
            }else{
                //新车不为空 老车为空 返回新车
                return newCartList;
            }
        } else {
            //新车为null 直接返回老车
            return oldCartList;
        }
        return oldCartList;
    }
}







