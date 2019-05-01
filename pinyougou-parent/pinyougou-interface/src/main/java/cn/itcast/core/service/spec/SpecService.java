package cn.itcast.core.service.spec;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.vo.SpecVo;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface SpecService {
    /**
     * 条件查询
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    public PageResult search(Integer page, Integer rows, Specification specification);

    /**
     * 添加
     * @param specVo
     */
    public void add(SpecVo specVo);

    /**
     * 根据id查询 数据回显
     * @param id
     * @return
     */
    public SpecVo findOne(Long id);

    /**
     * 更新
     * @param specVo
     */
    public void update(SpecVo specVo);

    /**
     * 删除
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 新建模板是规格展示
     * @return
     */
    List<Map> selectOptionList();
}
