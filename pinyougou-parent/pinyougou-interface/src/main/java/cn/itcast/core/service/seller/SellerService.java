package cn.itcast.core.service.seller;

import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;

import java.util.List;

public interface SellerService {
    /**
     * 注册 添加
     * @param seller
     */
    public void add(Seller seller);

    /**
     * 未审批的注册商家展示
     * @param page
     * @param rows
     * @param seller
     */
    PageResult search(Integer page, Integer rows, Seller seller);

    /**
     * 商检审核详情
     * @param sellerId
     * @return
     */
    Seller findOne(String sellerId);

    /**
     * 根据商家的名称id 跟新审核状态
     * @param sellerId
     * @param status
     */
    void updateStatus(String sellerId, String status);

    List<Seller> findAll();
}
