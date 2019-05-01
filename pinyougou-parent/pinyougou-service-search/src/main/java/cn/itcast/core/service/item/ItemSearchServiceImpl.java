package cn.itcast.core.service.item;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.service.search.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author wophy
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Resource
    private SolrTemplate solrTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ItemDao itemDao;

    /**
     * 前端检索+分页 +高亮 +分类,品牌,规格展示
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        //检索 + 分页
        // Map<String, Object> map = searchPage(searchMap);
        //检索 + 分页 + 高亮
        String keywords = searchMap.get("keywords");
        if (keywords!=null&&!"".equals(keywords)){
            keywords = keywords.replace(" ","");
            searchMap.put("keywords",keywords);
        }
        Map<String, Object> map = searchForHighLightPage(searchMap);

        resultMap.putAll(map);
        //根据item_category 检索  得到分类信息
        List<String> categoryList = searchForGroupPage(searchMap);
        resultMap.put("categoryList", categoryList);
        //品牌信息 规格信息展示
        if (categoryList != null && categoryList.size() > 0) {
            Map<String, Object> brandAndSpecMap = defaultSelectBrandAndSpecByCategoryByName(categoryList.get(0));
            resultMap.putAll(brandAndSpecMap);
        }
        return resultMap;


    }

    /**
     * 商品审核通过后添加到solr 索引库
     * @param id
     */
    @Override
    public void siShow(Long id) {

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

    /**
     * @param searchMap
     * @return
     */
    public Map<String, Object> searchForList(Map<String, String> searchMap) {
        Map<String, Object> map = new HashMap<>();
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //1.封装检索条件
        String keywords = searchMap.get("keywords");
        //检索的字段
        Criteria criteria = new Criteria("item_keywords");
        if (keywords != null && !"".equals(keywords.trim())) {
            //切词 根据词条检索
            criteria.is(keywords);
        }
        query.addCriteria(criteria);

        //2.封装分类,品牌,规格
        if (!"".equals(searchMap.get("category"))) {
            Criteria criteriaCategory = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteriaCategory);
            query.addFilterQuery(filterQuery);
        }
        if (!"".equals(searchMap.get("brand"))) {
            Criteria criteriaBrand = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteriaBrand);
            query.addFilterQuery(filterQuery);
        }
        //3.封装分页条件

        return map;
    }


    /**
     * 默认根据第一个分类的名称 加载品牌和规格(选项)的信息
     *
     * @param categoryName
     * @return
     */
    public Map<String, Object> defaultSelectBrandAndSpecByCategoryByName(String categoryName) {

        //从redis中出去分类名称为categoryName的分类对象的模板id
        Object typeId = redisTemplate.boundHashOps("itemCat").get(categoryName);
        //根据这个模板id 找到对应的规格及规格选项
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        Map<String, Object> map = new HashMap<>();
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }


    /**
     * 检索后根据检索信息加载分类的信息
     *
     * @param searchMap
     * @return
     */
    public List<String> searchForGroupPage(Map<String, String> searchMap) {
        //1.封装检索条件
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords.trim())) {
            criteria.is(keywords);
        }
        SimpleQuery simpleQuery = new SimpleQuery(criteria);
        //2.封装分组条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        simpleQuery.setGroupOptions(groupOptions);

        ArrayList<String> list = new ArrayList<>();
        //3.根据条件查询
        GroupPage<Item> page = solrTemplate.queryForGroupPage(simpleQuery, Item.class);
        //根据字段得到分组结果集
        GroupResult<Item> groupResult = page.getGroupResult("item_category");
        //分组结果集 取出所有的项
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            //遍历每一项得到对象的分组信息
            String groupValue = groupEntry.getGroupValue();
            list.add(groupValue);
        }


        return list;
    }

    /**
     * 检索 +分页 + 高亮
     *
     * @param searchMap
     * @return
     */
    public Map<String, Object> searchForHighLightPage(Map<String, String> searchMap) {
        //1.设置检索条件
        String keywords = searchMap.get("keywords");
        //检索的字段
        Criteria criteria = new Criteria("item_keywords");
        if (keywords != null && !"".equals(keywords.trim())) {
            //切词 根据词条检索
            criteria.is(keywords);
        }

        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);
        //2.设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        //起始页
        Integer startPage = (pageNo - 1) * pageSize;
        query.setOffset(startPage);
        query.setRows(pageSize);
        //3.设置高亮
        HighlightOptions options = new HighlightOptions();
        //指定显示高亮的字段(标题)
        options.addField("item_title");
        //设置前缀(显示效果)
        options.setSimplePrefix("<font color='red'>");
        //设置后缀(显示效果)
        options.setSimplePostfix("</font>");
        query.setHighlightOptions(options);

        //2.封装分类
        if (!"".equals(searchMap.get("category"))) {
            Criteria criteriaCategory = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteriaCategory);
            query.addFilterQuery(filterQuery);
        }
        //2.封装品牌
        if (!"".equals(searchMap.get("brand"))) {
            Criteria criteriaBrand = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteriaBrand);
            query.addFilterQuery(filterQuery);
        }
        //2.封装规格
        if (searchMap.get("spec") != null && !"".equals(searchMap.get("spec"))) {
            String spec = searchMap.get("spec");
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            for (String key : specMap.keySet()) {
                Criteria criteriaSpec = new Criteria("item_spec_" + key).is(specMap.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteriaSpec);
                query.addFilterQuery(filterQuery);
            }
        }

        //3.根据价格查询 'price','3000-*'
        String price = searchMap.get("price");
        if (price != null && !"".equals(price)) {
            String[] prices = price.split("-");
            Criteria cri = new Criteria("item_price");
            //判断是否包含  *
            if (price.contains("*")) {
                    cri.greaterThanEqual(prices[0]);
            }else{
                cri.between(prices[0],prices[1],true,true);
            }
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);
        }
        //4.排序,价格,销量,updateTime //'updatetime','DESC'  ng-click="sortSearch('price','ASC')"
        //'sort':'','sortField':''   $scope.sortSearch=function(sortField,sort)
        String sort = searchMap.get("sort");
        String sortField = searchMap.get("sortField");
        if (sort!=null&&!"".equals(sort)){
            //如果升序
            if ("ASC".equals(sort)){
                Sort s = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(s);
            }else{
                Sort s = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(s);
            }
        }


        //4.根据条件查询
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(query, Item.class);
        // String brand = searchMap.get("brand");
        //5.处理高亮的效果
        List<HighlightEntry<Item>> highlighted = page.getHighlighted();

        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                //得到item(对原有的item的title进行高亮处理)
                Item item = itemHighlightEntry.getEntity();
                List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
                if (highlights != null && highlights.size() > 0) {
                    //得到高亮的标题
                    String title = highlights.get(0).getSnipplets().get(0);
                    //设置item的标题为新的高亮的标题
                    item.setTitle(title);
                }
            }
        }



        Iterator<Item> iterator = page.iterator();

        //6.封装结果集
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", page.getTotalElements());
        map.put("totalPages", page.getTotalPages());
        //封装商品结果集
        map.put("rows", page.getContent());
        return map;
    }


    /**
     * 根据检索条件查询 并分页
     *
     * @param searchMap
     * @return
     */
    public Map<String, Object> searchPage(Map<String, String> searchMap) {
        //1.封装检索条件
        //指定检索的字段
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords.trim())) {
            //切词 根据词条检索
            criteria.is(keywords);
        }
        SimpleQuery simpleQuery = new SimpleQuery(criteria);
        //2.设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        //起始行
        Integer startPage = (pageNo - 1) * pageSize;
        //设置起始页
        simpleQuery.setOffset(startPage);
        //设置每页条数
        simpleQuery.setRows(pageSize);
        //3.根据条件查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(simpleQuery, Item.class);
        //4.封装结果
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", scoredPage.getTotalElements());
        map.put("totalPages", scoredPage.getTotalPages());
        //封装商品结果集
        map.put("rows", scoredPage.getContent());
        return map;
    }


}

