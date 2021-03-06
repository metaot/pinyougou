package cn.itcast.core.controller.spec;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.spec.SpecService;
import cn.itcast.core.vo.SpecVo;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/specification")
public class SpecController {
    @Reference
    private SpecService specService;
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification){
        return specService.search(page, rows, specification);
    }

    @RequestMapping("/add.do")
    public Result add(@RequestBody SpecVo specVo){
        try {
            specService.add(specVo);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }
    @RequestMapping("/findOne.do")
    public SpecVo findOne(Long id){
        return specService.findOne(id);
    }

    /**
     * 更新
     * @param specVo
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody SpecVo specVo){
        try {
            specService.update(specVo);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            specService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 新建模板的规格展示
     * @return 所有的规格
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        return specService.selectOptionList();
    }


}
