package cn.itcast.core.service.goods;

;
import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import entity.PageResult;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDao goodsDao;
    @Resource
    private GoodsDescDao goodsDescDao;
    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private BrandDao brandDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination topicPageAndSolrDestination;

    @Resource
    private  Destination  queueSolrDeleteDestination;

    /**
     * @param goodsVo
     */
    @Transactional
    @Override
    public void add(GoodsVo goodsVo) {
        //保存商品
        Goods goods = goodsVo.getGoods();
        //需要对审核状态进行设置
        goods.setAuditStatus("0"); //0:待审核
        goodsDao.insertSelective(goods);    //需要返回自增id
        //保存商品描述
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        //商品描述含有 商品的id 不能为null
        //  System.out.println(goods.getId());
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);
        saveItem(goodsVo);

    }

    /**
     * 商品添加的规格保存提取
     *
     * @param goodsVo
     */
    private void saveItem(GoodsVo goodsVo) {
        Goods goods = goodsVo.getGoods();
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        //保存商品对应的库存信息
        if ("1".equals(goods.getIsEnableSpec())) { //启用规格:一个商品可以对应多个库存
            List<Item> itemList = goodsVo.getItemList();
            if (itemList != null && itemList.size() > 0) {
                for (Item item : itemList) {
                    //设置库存商品的标题 = spu名称+spu副标题+规格选项名称
                    String title = goods.getGoodsName() + " " + goods.getCaption();
                    //规格选项在item表中 的spec
                    String spec = item.getSpec();
                    Map<String, String> map = JSON.parseObject(spec, Map.class);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);
                    setAttributeForItem(item, goods, goodsDesc);
                    //保存
                    itemDao.insertSelective(item);
                }
            } else {//不启用规格的情况下
                Item item = new Item();
                item.setTitle(goods.getGoodsName() + " " + goods.getCaption()); // 库存的商品标题
                item.setPrice(goods.getPrice());    // 商品价格
                item.setIsDefault("1");             // 默认的商品
                item.setSpec("{}");                 // 无规格
                item.setNum(9999);                  // 库存量
                setAttributeForItem(item, goods, goodsDesc);
                // 保存库存
                itemDao.insertSelective(item);

            }

        }

    }

    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        //设置分页
        PageHelper.startPage(page, rows);
        //设置查询条件(商家只能查询自己的商品信息)
      //  goods.getSellerId();
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goods.getSellerId() != null && !"".equals(goods.getSellerId().trim())) {
            criteria.andSellerIdEqualTo(goods.getSellerId().trim());
        }
        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus().trim())) {
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }
        if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName().trim())) {
            criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
        }
        //倒叙
        goodsQuery.setOrderByClause("id desc");
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
        //封装goods
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);
        //封装goodsDesc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goods.getId());
        goodsVo.setGoodsDesc(goodsDesc);
        //封装List<item>
        ItemQuery query = new ItemQuery();
        if (id != null && !"".equals(id.toString().trim())) {
            query.createCriteria().andGoodsIdEqualTo(id);
        }
        List<Item> items = itemDao.selectByExample(query);
        goodsVo.setItemList(items);
        return goodsVo;
    }

    /**
     * 更新商品
     *
     * @param goodsVo
     */
    @Override
    public void update(GoodsVo goodsVo) {
        //保存商品
        Goods goods = goodsVo.getGoods();
        //需要对审核状态进行设置
        goods.setAuditStatus("0"); //0:待审核
        goodsDao.updateByPrimaryKeySelective(goods);


        //保存商品描述
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        //商品描述含有 商品的id 不能为null
        //  System.out.println(goods.getId());
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);

        //SKU更新 先删除在插入
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(query);

        //插入就是add方法
        saveItem(goodsVo);
    }

    /**
     * 运营商系统商品的列表查询分页
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult searchForManager(Integer page, Integer rows, Goods goods) {
        //设置分页
        PageHelper.startPage(page, rows);
        //设置查询条件
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.setOrderByClause("id desc");
        //封装查询条件
        // 待审核的商品 且未删除的商品
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {
            criteria.andAuditStatusEqualTo("0");
        }
        criteria.andIsDeleteIsNull();
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 商品审核
     *
     * @param ids    多选的商品的id
     * @param
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String auditStatus) {
        if(ids != null && ids.length > 0){
            Goods goods = new Goods();
            goods.setAuditStatus(auditStatus);
            for (final Long id : ids) {
                goods.setId(id);
                // 商品审核
                goodsDao.updateByPrimaryKeySelective(goods);
                if("1".equals(auditStatus)){

                    // 将审核通过后的商品信息保存到索引库中（上架）
//                    isShow(id); // 上架
                    // 将所有的商品保存到索引库中：不是最终的做法。
//                    dataImportToSolr();
                    // 生成商品详情的静态页
//                    staticPageService.getHtml(id);
                    // 将消息（商品id）发送到mq中
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            // 将商品id封装成消息体：文本消息、map消息
                            TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                            return textMessage;
                        }
                    });
                }
            }
        }
    }



    /**
     * 商品审核通过更新索引库
     * @param id
     */
    private void updateSolr(Long id) {
        //根据id 查询库存
        ItemQuery itemQuery = new ItemQuery();
        //审核通过状态 && 默认首页展示 && 该商品的下的库存
        itemQuery.createCriteria().andStatusEqualTo("1").andIsDefaultEqualTo("1")
                .andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        //处理动态字段
        if (itemList!=null&&itemList.size()>0){
            for (Item item : itemList) {
                String spec = item.getSpec();
                Map map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }
    }

    private void dataImporToSolr() {
        //查询所有的sku
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andStatusEqualTo("1");
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        for (Item item : itemList) {
            String spec = item.getSpec();
            Map specMap = JSON.parseObject(spec, Map.class);
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    /**
     * 运营商商品删除
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            //设置商品是否删除状态码
            goods.setIsDelete("1");
            for (final Long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
        }
        /**
         * 批量删除,
         * 判断非空非null
         * 遍历 查询是否是一个父id(是否关联) 是的话不能删除
         * 不是的 删除
         * 返回前端
         *  当全部无法删除是 提示信息
         * 有删除时 刷新当前页面 未删除的显示勾选状态
         */
    }

    public void deleteSolr(Long[] ids){
        if (ids != null && ids.length > 0){
            for (Long id : ids) {
                SimpleQuery query = new SimpleQuery("item_goodsid:"+id);
                solrTemplate.delete(query);
                solrTemplate.commit();
            }

        }
    }


    private void setAttributeForItem(Item item, Goods goods, GoodsDesc goodsDesc) {
        //设置库存图片:一张图片 图片保存在goods_desc中
        String itemImages = goodsDesc.getItemImages();
        List<Map> images = JSON.parseArray(itemImages, Map.class);
        if (images != null && images.size() > 0) {
            String url = images.get(0).get("url").toString();
            item.setImage(url);
        }
        //设置三级分类
        item.setCategoryid(goods.getCategory3Id());
        item.setStatus("1");    // 库存状态：1，正常
        item.setCreateTime(new Date()); // 创建日期
        item.setUpdateTime(new Date()); // 更新日期
        item.setGoodsId(goods.getId()); // 商品id
        item.setSellerId(goods.getSellerId());  // 商家id
        item.setCategory(itemCatDao.selectByPrimaryKey(item.getCategoryid()).getName());//设置分类名称
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());    // 品牌名称
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());   // 商家店铺名称

    }
}

















