package cn.itcast.core.task;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
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
 * 分类 品牌 以及规格(选项) 载入redis
 * @author wophy
 *
 */

@Component
public class RedisTask2 {
    @Resource
    private ItemCatDao itemCatDao;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private BrandDao brandDao;
    @Resource
    private TypeTemplateDao typeTemplateDao;
    @Resource
    private SpecificationOptionDao specificationOptionDao;
    /**
     *
      */
    @Scheduled(cron = "00 56 19 28 02 *")
    public void redisItem(){
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList!=null&&itemCatList.size()>0){
            for (ItemCat itemCat : itemCatList) {
                Object o = redisTemplate.boundHashOps("itemCat").get(itemCat.getName());
                System.out.println(o.toString());
                // redisTemplate.boundHashOps("itemCatList").put(itemCat.getName(),itemCat.getTypeId());
            }
        }
    }

    /**
     *
     */
    @Scheduled(cron = "00 56 19 28 02 *")
    public void redisBrandAndSpec(){
        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if (typeTemplateList!=null&&typeTemplateList.size()>0){
            for (TypeTemplate typeTemplate : typeTemplateList) {
                String brandIds = typeTemplate.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                System.out.println("brandList"+brandList.toString());
                //redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);

                String specIds = typeTemplate.getSpecIds();
                List<Map> specList = JSON.parseArray(specIds, Map.class);
                for (Map map : specList) {
                    long id = Long.parseLong(map.get("id").toString());
                    SpecificationOptionQuery query = new SpecificationOptionQuery();
                    query.createCriteria().andSpecIdEqualTo(id);
                    List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                    map.put("options",options);
                }
                System.out.println("specList:      "+specList.toString());
               // redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
            }
        }
    }
}
