package cn.itcast.core.service.goods;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.vo.GoodsVo;
import entity.PageResult;
import org.bouncycastle.crypto.agreement.srp.SRP6Client;

import javax.print.DocFlavor;

public interface GoodsService {
    /**
     * 添加商品
     * @param goodsVo
     */
    public void add(GoodsVo goodsVo);

    /**
     * 商品的分页展示
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    public PageResult search(Integer page, Integer rows, Goods goods);

    /**
     * 修改商品的数据回显
     * @param id
     * @return
     */
    GoodsVo findOne(Long id);

    /**
     * 更新商品
     * @param goodsVo
     */
    void update(GoodsVo goodsVo);

    /**
     * 运营商审核商品列表展示
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult searchForManager(Integer page, Integer rows, Goods goods);

    /**
     * 商品审核 状态更新
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 删除商品
     * @param ids
     */
    void delete(Long[] ids);
}
