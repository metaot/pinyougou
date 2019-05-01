package cn.itcast.core.service.staticpage;

public interface StaticPageService {

    /**
     * 生成商品详情静态页
     * @param id
     */
    public void getHtml(Long id);

    /**
     * 删除商品详情静态页
     * @param id
     */
    public void delHtml(Long id);
}
