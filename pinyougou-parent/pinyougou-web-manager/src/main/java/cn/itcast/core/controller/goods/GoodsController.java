package cn.itcast.core.controller.goods;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.goods.GoodsService;
import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;
    @RequestMapping("/add.do")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {

            // System.out.println(goodsVo.toString());
            //保存商品信息  sellerId 商家的id(名称) 商家信息在登录信息里面
            //得到登录信息id
            String selleerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //设置商品的id
            goodsVo.getGoods().setSellerId(selleerId);
            goodsService.add(goodsVo);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }
    }
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        //设置商家id
        goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());

        return goodsService.search(page,rows,goods);
    }
    @RequestMapping("/findOne.do")
    public GoodsVo findOne(Long id){
        return goodsService.findOne(id);
    }
    @RequestMapping("/update.do")
    public Result update(@RequestBody GoodsVo goodsVo){
        System.out.println("未查询的前台数据sellerID:"+goodsVo.getGoods().getSellerId());
        GoodsVo goodsVo1 = goodsService.findOne(goodsVo.getGoods().getId());
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sellerId.equals(goodsVo1.getGoods().getSellerId())){
            return new Result(false,"非法操作");
        }
        //设置商品的id
        goodsVo.getGoods().setSellerId(sellerId);
        try {
            goodsService.update(goodsVo);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }

    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            goodsService.delete(ids);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");

        }
    }
}