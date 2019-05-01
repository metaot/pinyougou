package cn.itcast.core.service.content;

import java.util.List;

import cn.itcast.core.pojo.ad.ContentQuery;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import entity.PageResult;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class ContentServiceImpl implements ContentService {

    @Resource
    private ContentDao contentDao;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public List<Content> findAll() {
        List<Content> list = contentDao.selectByExample(null);
        return list;
    }

    @Override
    public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Content> page = (Page<Content>) contentDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(Content content) {
        //清除缓存
        cleanRedis(content.getCategoryId());
        contentDao.insertSelective(content);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(Content content) {
        //更新之前清除缓存
        Long categoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
        cleanRedis(categoryId);
        contentDao.updateByPrimaryKeySelective(content);
        //如果修改了分类id 可能关联了别的分类id 清除缓存
        if (categoryId.longValue() != content.getCategoryId().longValue()) {
            cleanRedis(content.getCategoryId());
        }
    }

    @Override
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delAll(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                //清除缓存
                cleanRedis(contentDao.selectByPrimaryKey(id).getCategoryId());
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    /**
     * 首页轮播图展示
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        //从缓存中取出contentList
        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
        //判断是否有缓存
        if (contentList == null) {
            //防止缓存穿透
            /**
             * 当某一时刻同时进行了判断得到的都是空,都会请求数据库
             *  解决 加锁 并进行第二次判断
             */
            synchronized (this) {
                contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
                //判断是否有缓存
                if (contentList == null) {
                    // 设置查询条件
                    ContentQuery contentQuery = new ContentQuery();
                    contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).
                            andStatusEqualTo("1");    // 根据分类查询并且可用的广告
                    contentQuery.setOrderByClause("sort_order desc");    // 根据该字段排序
                    // 查询
                    contentList = contentDao.selectByExample(contentQuery);
                    redisTemplate.boundHashOps("content").put(categoryId, contentList);
                }
            }
        }
        return contentList;
    }

    /**
     * 清除缓存
     * @param categoryId
     */
    private void cleanRedis(Long categoryId) {
        redisTemplate.boundHashOps("content").delete(categoryId);
    }

}
