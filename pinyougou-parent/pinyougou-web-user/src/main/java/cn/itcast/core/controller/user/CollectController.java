package cn.itcast.core.controller.user;

import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.collect.CollectService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Reference
    private CollectService collectService;

    /**
     * 显示收藏商品
     * @return
     */
    @RequestMapping("/showCollect.do")
    public List<OrderItem> showCollect(){
        String seller_id = SecurityContextHolder.getContext().getAuthentication().getName();

        return collectService.showCollect(seller_id);
    }
}
