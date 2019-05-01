package cn.itcast.core.task;


import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wophy
 */
@Component
public class RedisTask {

    @Resource
    private ItemCatDao itemCatDao;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private TypeTemplateDao typeTemplateDao;
    @Resource
    private SpecificationOptionDao specificationOptionDao;



    //将所有分类加载到redis
    @Scheduled(cron = "00 35 16 28 02 *")
    public void setItemCatToRedis() {
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList != null && itemCatList.size() > 0) {
            for (ItemCat itemCat : itemCatList) {
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
            }
        }

    }



    /**
     * 将品牌和规格加载到redis
     */
    @Scheduled(cron = "00 35 16 28 02 *")
    public void setBrandsAndSpecAndSpecOptionToRedis(){
        //将模板信息同步到redis中
        List<TypeTemplate> list = typeTemplateDao.selectByExample(null);
        if (list.size()>0&&list!=null){
            for (TypeTemplate template : list) {
                String brandIds = template.getBrandIds();
                //brandIds :[{"id":16,"text":"TCL"},{"id":13,"text":"长虹"}]
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                //redis 中放入brandList (品牌信息)
                System.out.println();
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            }
        }


    }
    public List<Map> findBySpecList(Long id) {
        //根据type的主键id查询 typeTemplate对象 包含(spec_id)
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //获取到 spec_id 在数据库中以字符json字符串的形式存在[{"id":"27","text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //将[{id=27, text=网络}, {id=32, text=机身内存}, {id=28, text=手机屏幕尺寸}] 字符串转换为List<Map>形式
        List<Map> list = JSON.parseArray(specIds, Map.class);

        for (Map map : list) {
            long specId = Long.parseLong(map.get("id").toString());

            //根据规格id 获取相应的规格属性
            SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
            specificationOptionQuery.createCriteria().andSpecIdEqualTo(specId);
            List<SpecificationOption> options = specificationOptionDao.selectByExample
                    (specificationOptionQuery);
            System.out.println();
            map.put("options",options);
        }
        //[{"id":"","text":"","options":[{},{}]}]
        return list;
    }

}
