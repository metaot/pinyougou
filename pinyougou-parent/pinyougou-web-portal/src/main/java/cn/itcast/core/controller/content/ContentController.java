package cn.itcast.core.controller.content;

import cn.itcast.core.pojo.ad.Content;

import cn.itcast.core.service.content.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    /**
     * 首页轮播图展示
     * @param categoryId
     * @return
     */
    @RequestMapping("/findByCategoryId.do")
    public List<Content> findByCategoryId(Long categoryId){
        System.out.println(1);
        List<Content> contentList = contentService.findByCategoryId(categoryId);
        return contentList;
    }
}
