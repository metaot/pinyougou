package cn.itcast.core.service.staticpage;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StaticPageServiceImpl  implements StaticPageService,ServletContextAware{

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    //springmvc支持freemarker
    //注入FreeMarkerConfigurer:获取需要的Configuration并且指定模板的位置

    private Configuration configuration;
    //属性注入 (配置文件中为这个类注入)
    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }


    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }



    @Override
    public void getHtml(Long id) {
       //1.创建configuration并指定模板的位置
        //在配置文件中指定模板的位置(同springMvv指定视图一样)
        try {
            //2.获取该位置下需要的模板
            Template template = configuration.getTemplate("item.ftl");
            //3.准备静态页面数据
            Map<String, Object> dataModel = getDataModel(id);
            //指定file:生成静态页的位置  可以直接访问  发布到项目的真实路径下
            String pathname = "/"+id+".html";
            String path = servletContext.getRealPath(pathname);

            /**
             *
             * protected String getResourceLocation(String path) {
             if (!path.startsWith("/")) {
             path = "/" + path;
             }
             return this.resourceBasePath + path;
             }
             *
             */
            File file = new File(path);
            //4.模板 + 数据
            template.process(dataModel,new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void delHtml(Long id) {
        //指定file:生成静态页的位置 可以直接访问  发布到项目的真实路径下
        String pathName="/"+id+".html";

        String path=servletContext.getRealPath(pathName);
        //用静态页的真实路径生成file对象
        File file = new File(path);

        file.delete();
    }

    private Map<String,Object> getDataModel(Long id) {
        HashMap<String, Object> map = new HashMap<>();
        //商品副标题
        Goods goods = goodsDao.selectByPrimaryKey(id);
        map.put("goods",goods);
        // 商品图片、介绍等
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        map.put("goodsDesc", goodsDesc);
        // 商品库存
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id).
                andStatusEqualTo("1").andNumGreaterThan(0);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        map.put("itemList", itemList);
        // 商品分类
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
        map.put("itemCat1", itemCat1);
        map.put("itemCat2", itemCat2);
        map.put("itemCat3", itemCat3);
        return map;
    }
}
