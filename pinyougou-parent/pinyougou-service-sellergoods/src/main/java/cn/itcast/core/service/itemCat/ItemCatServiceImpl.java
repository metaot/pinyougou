package cn.itcast.core.service.itemCat;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;

import com.alibaba.dubbo.config.annotation.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Resource
    private ItemCatDao itemCatDao;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        //查询分类是将所有分类加载到redis
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList!=null&&itemCatList.size()>0){
            for (ItemCat itemCat : itemCatList) {
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
        }





        ItemCatQuery query = new ItemCatQuery();
        if (parentId!=null&&!"".equals(parentId)){
            query.createCriteria().andParentIdEqualTo(parentId);
        }
        return itemCatDao.selectByExample(query);

    }
    @Transactional
    @Override
    public void add(ItemCat itemCat) {
        if (itemCat!=null&&!"".equals(itemCat)){
            itemCatDao.insertSelective(itemCat);

        }


    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    /**
     * 删除分类
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            //删除分类前确定该分类下没有没类信息

            for (Long id : ids) {
           /* ItemCat itemCat = itemCatDao.selectByPrimaryKey(id);
            Long parentId = itemCat.getParentId();*/
                //判断这个id是不是parent_id 不是可以删除 是不可以删除
                ItemCatQuery query = new ItemCatQuery();
                query.createCriteria().andParentIdEqualTo(id);
                List<ItemCat> list = itemCatDao.selectByExample(query);
                if (list.size() == 0) {
                    itemCatDao.deleteByPrimaryKey(id);
                }else{
                    System.out.println(list.get(list.size()+2).getName());
                }

            }
        }
    }
}
