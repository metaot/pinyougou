package cn.itcast.core.controller.typeTmeplate;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.typeTemplate.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.search(page,rows,typeTemplate);
    }
    @RequestMapping("/add.do")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        System.out.println(typeTemplate.toString());
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }
    }

    /**
     * 修改模板的数据回显 根据id查询
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public TypeTemplate findOne(Long id){
        System.out.println(typeTemplateService.findone(id).toString());
        return typeTemplateService.findone(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        System.out.println(typeTemplate.toString());
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }
    }
    @RequestMapping("/findBySpecList.do")
    public List<Map> findBySpecList(){
       return typeTemplateService.findBySpecList();
    }
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        System.out.println(typeTemplateService.selectOptionList());
        return typeTemplateService.selectOptionList();
    }
}
