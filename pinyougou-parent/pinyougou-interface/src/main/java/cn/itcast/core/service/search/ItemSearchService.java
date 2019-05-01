package cn.itcast.core.service.search;

import java.util.Map;

/**
 *
 * @author wophy
 */
public interface ItemSearchService {
    /**
     * 主页搜索显示高亮
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map<String,String> searchMap);
    //public Map<String,Object> searchList(Map<String,String> searchMap);\

    public void siShow(Long id);
}
