package cn.itcast.core.controller.cart;

import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;

import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.cart.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wophy
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    // allowCredentials="true"，无需设置，默认为：true
    @Reference
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList.do")
    @CrossOrigin(origins = "http://localhost:9003", allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse
            response) {

        // 服务器端支持CORS
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:9003");
        // 携带cookie
        //response.setHeader("Access-Control-Allow-Credentials", "true");
        try {

            //1.定义一个空购物车
            List<Cart> cartList = null;
            boolean flag=false;
            //TODO 添加购物车
            //2.判断Cookie中是否有购物车
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if ("BUYER_CART".equals(cookie.getName())) {
                        String value = cookie.getValue();
                        cartList = JSON.parseArray(value, Cart.class);
                        //说明cookie中有
                        flag=true;
                        break;
                    }
                }
            }
            //2.1 cookie中是否取出购物车 没有则创建
            if (cartList == null) {
                cartList = new ArrayList<>();
            }
            //3.创建一个购物显示项
            Cart cart = new Cart();
            Item item = cartService.findOne(itemId);
            //3.1设置商家id
            cart.setSellerId(item.getSellerId());
            //3.2设置每个商家下的商品项
            ArrayList<OrderItem> orderItemList = new ArrayList<>();
            OrderItem orderItem = new OrderItem();
            //设置库存id
            orderItem.setItemId(itemId);
            //设置数量
            orderItem.setNum(num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);

            //5.将商品保存到购物车
            //6.判断商品是否为同一个商家
            int sellerIndexOf = cartList.indexOf(cart);
            if (sellerIndexOf != -1) {
                Cart oldCart = cartList.get(sellerIndexOf);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                //oldOrderItemList 里面是否包含 否已商品项
                int indexOf = oldOrderItemList.indexOf(orderItem);
                if (indexOf != -1) {
                    //说明存在
                    OrderItem oldOrderItem = oldOrderItemList.get(indexOf);
                    oldOrderItem.setNum(oldOrderItem.getNum() + num);

                } else {
                    //是同意商家 单不是同一个商品
                    oldOrderItemList.add(orderItem);
                }
                //不是同一个商家
            } else {
                cartList.add(cart);
            }

            //TODO 判断用户是否登录
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("username:"+username);
            if (!"anonymousUser".equals(username)){
                //已经登录:将购车保存到redis中
                cartService.mergeCartList(username,cartList);
                //本地有:将本地的购车清空
                if (flag){
                    Cookie cookie = new Cookie("BUYER_CART", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");    // 设置cookie的共享   9003/ 9103/
                    response.addCookie(cookie);
                }
            }else{

                //7. 设置到cookie中
                //设置过期时间
                Cookie cookie = new Cookie("BUYER_CART", JSON.toJSONString(cartList));
                cookie.setMaxAge(60 * 60);
                // 设置cookie的共享   9003/ 9103/
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }


    @RequestMapping("/findCartList.do")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        List<Cart> cartList =null;
        // 从cookie中取出基础数据 考虑用户添加购物车后 登录 直接从cookie中取出购物车
        //在判断是否登录 登录则整合  最后根据购物车里面基础数据 进行其他数据的封装
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("BUYER_CART".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    cartList = JSON.parseArray(value, Cart.class);
                    break;
                }
            }
        }
        // 已登录：从redis中获取
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(username)){
            // 场景：未登录的情况下，将上加入购物车
            // 用户去登录：登录成功后，在页面点击【我的购物车】，需要将本地的购物车同步到redis中
            if(cartList != null){
                // 同步
                cartService.mergeCartList(username, cartList);
                // 清空本地
                Cookie cookie = new Cookie("BUYER_CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");    // 设置cookie的共享   9003/ 9103/
                response.addCookie(cookie);
            }
            cartList = cartService.findCartListFromRedis(username);
        }

            if (cartList != null) {
                cartList = cartService.setAttrituteForCart(cartList);
            }

        return cartList;
    }

    /**
     * 单个订单项的收藏
     * @param itemId
     * @return
     */
    @RequestMapping("/addCollect.do")
    public Result addCollect(Long itemId,HttpServletResponse response){
        try {
            //获取登录用户的名字
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            boolean b = cartService.checkCollect(itemId, username);

            if(b) {
                 cartService.addCollect(itemId, username);
             }else {
                return new Result(false,"此商品已经收藏");
            }

            return new Result(true,"收藏成功");
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false,"收藏失败");
        }
    }

    /**
     * 订单项的批量收藏
     * @param selectIds
     * @return
     */
    @RequestMapping("/addCollects.do")
    public Result addCollects(Long[] selectIds,HttpServletResponse response){
        try {
            //获取登录用户的名字
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            for (Long selectId : selectIds) {
                boolean b = cartService.checkCollect(selectId, username);

                if(b){
                    cartService.addCollect(selectId,username);
                }else {
                    return new Result(false,"此商品已经收藏");
                }
            }

            return new Result(true,"收藏成功");
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false,"收藏失败");
        }
    }
}
