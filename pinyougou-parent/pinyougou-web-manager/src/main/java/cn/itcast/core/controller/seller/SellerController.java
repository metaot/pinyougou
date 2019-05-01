package cn.itcast.core.controller.seller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.seller.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 分页展示
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller) {
        System.out.println(sellerService.search(page,rows,seller).toString());
        return sellerService.search(page,rows,seller);

    }

    /**
     * 商家审核详情
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public Seller findOne(String id){

        return sellerService.findOne(id);
    }
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }
    }

    /**
     * 查询所有商家
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Seller> findAll() {
        return sellerService.findAll();

    }



}
