package cn.itcast.core.controller.itemsearch;



import cn.itcast.core.service.search.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author wophy
 */
@RestController
@RequestMapping("/itemsearch")
public class ItemsearchController {
    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search.do")
    public Map<String, Object> search(@RequestBody Map<String,String> searchMap) {
        return itemSearchService.search(searchMap);
    }

}
