package cn.itcast.core.service.typeTemplate;

import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    /**
     * 分页展示
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    /**
     * 添加模板
     * @param typeTemplate
     */
    void add(TypeTemplate typeTemplate);

    /**
     * 修改模板的数据回显 根据id查询
     * @param id
     * @return
     */
    TypeTemplate findone(Long id);

    /**
     * 修改模板
     * @param typeTemplate
     */
    void update(TypeTemplate typeTemplate);

    /**
     * 删除模板
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 查询所有模板
     * @return
     */
    List<Map> findBySpecList();

    /**
     * 根据id查询模板
     * @param id
     * @return
     */
    TypeTemplate findOne(Long id);

    List<Map> findBySpecList(Long id);

    /**
     * 增加分类是模板的回显
     * @return
     */
    List<Map> selectOptionList();
}
