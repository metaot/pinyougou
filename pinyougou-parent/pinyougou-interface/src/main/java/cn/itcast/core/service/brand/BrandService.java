package cn.itcast.core.service.brand;

import cn.itcast.core.pojo.good.Brand;
import entity.PageResult;


import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有的品牌
     * @return
     */
    public List<Brand> findAll();
    public List<Brand> findAll1();

    /**
     * 分页查询品牌
     * @param pageNo
     * @param pageSize
     * @return
     */
    public PageResult findPage(Integer pageNo, Integer pageSize);

    /**
     * 根据品牌的条件查询
     * @param pageNo
     * @param pageSize
     * @param brand
     * @return
     */
    public PageResult search(Integer pageNo, Integer pageSize, Brand brand);

    public void add(Brand brand);

    /**
     * 根据id查询
     * @param id
     */
    public Brand findOne(Long id);

    /**
     * 更新
     * @param brand
     */
    public void update(Brand brand);

    public void delete(Long[] ids);

    /**
     * 新建模板的关联品牌展示
     * @return
     */
    List<Map> selectOptionList();
}
