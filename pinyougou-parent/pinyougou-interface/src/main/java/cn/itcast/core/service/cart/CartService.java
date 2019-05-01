package cn.itcast.core.service.cart;


import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;

import java.util.List;

/**
 * @author wophy
 */
public interface CartService {
    /**
     *
     * @param id
     * @return
     */
    public Item findOne(Long id);

    /**
     * 购物车数据回显
     * @param cartList cookie中取出
     */
    List<Cart> setAttrituteForCart(List<Cart> cartList);

    /**
     * 将购物车同步到redis中
     * @param username
     * @param cartList
     */
    void mergeCartList(String username, List<Cart> cartList);

    /**
     * 根据username 从redis取出购物车基础数据 进行封装 回显
     * @param username
     * @return
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     * 单个商品的收藏
     * @param itemId
     */
    void addCollect(Long itemId, String username);

    //查看商品是否被添加
    boolean checkCollect(Long itemId, String username);
}
