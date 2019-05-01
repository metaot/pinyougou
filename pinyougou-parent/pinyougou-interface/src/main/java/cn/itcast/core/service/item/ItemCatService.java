package cn.itcast.core.service.item;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    /**
     * 根据父id查询分类信息
     * @param parentId
     * @return
     */
     List<ItemCat> findByParentId(Long parentId);

    /**
     * 添加分类
     * @param itemCat
     */
    void add(ItemCat itemCat);

    /**
     *
     * @param id
     * @return
     */
    ItemCat findOne(Long id);

    /**
     * 查询所有 商品展示是展示分类信息
     * @return
     */
    public List<ItemCat> findAll();

    /**
     * 删除分类
     * @param ids
     */
    void delete(Long[] ids);
}
