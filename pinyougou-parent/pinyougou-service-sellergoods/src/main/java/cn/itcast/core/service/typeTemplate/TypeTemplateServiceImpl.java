package cn.itcast.core.service.typeTemplate;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;

import cn.itcast.core.pojo.template.TypeTemplateQuery;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //将模板信息同步到redis中
        List<TypeTemplate> list = typeTemplateDao.selectByExample(null);
        if (list.size()>0&&list!=null){
            for (TypeTemplate template : list) {
                String brandIds = template.getBrandIds();
                //brandIds :[{"id":16,"text":"TCL"},{"id":13,"text":"长虹"}]
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                //redis 中放入brandList (品牌信息)
               redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            }
        }





        //设置分页参数
        PageHelper.startPage(page, rows);
        //设置查询条件(模板名称)
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (typeTemplate.getName() != null && !"".equals(typeTemplate.getName().trim())) {

            typeTemplateQuery.createCriteria().andNameLike("%" + typeTemplate.getName() + "%");
        }
        // typeTemplateQuery.setOrderByClause("id desc");
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        //返回分页结果
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 添加模板
     *
     * @param typeTemplate
     */
    @Transactional
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 修改模板的数据回显 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findone(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 修改模板
     *
     * @param typeTemplate
     */
    @Transactional
    @Override
    public void update(TypeTemplate typeTemplate) {
        //先删除在插入
        typeTemplateDao.deleteByPrimaryKey(typeTemplate.getId());
                //插入
        typeTemplateDao.insertSelective(typeTemplate);
        // System.out.println(typeTemplate.toString());
        // typeTemplateDao.updateByExampleSelective();
    }

    /**
     * 删除模板
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            typeTemplateDao.deleteByPrimaryKeys(ids);
        }
    }

    @Override
    public List<Map> findBySpecList() {
       return typeTemplateDao.selectByPrimaryKeys();

    }

    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        //根据type的主键id查询 typeTemplate对象 包含(spec_id)
        //spec_id 在数据库中以字符json字符串的形式存在[{"id":"27","text":"网络"},{"id":32,"text":"机身内存"}]
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);

        //获取到 spec_id 在数据库中以字符json字符串的形式存在[{"id":"27","text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();

        //将[{id=27, text=网络}, {id=32, text=机身内存}, {id=28, text=手机屏幕尺寸}] 字符串转换为List<Map>形式
        List<Map> list = JSON.parseArray(specIds, Map.class);


        //System.out.println(list.toString());
        for (Map map : list) {
            //Object o = map.get(id);
            long specId = Long.parseLong(map.get("id").toString());
            //根据规格id 获取相应的规格属性
            SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
            specificationOptionQuery.createCriteria().andSpecIdEqualTo(specId);
            List<SpecificationOption> options = specificationOptionDao.selectByExample
                    (specificationOptionQuery);

            map.put("options",options);
        }
      //[{"id":"","text":"","options":[{},{}]}]
        System.out.println(list);

        return list;
    }

    @Override
    public List<Map> selectOptionList() {
        return typeTemplateDao.selectByPrimaryKeys();
    }
}
