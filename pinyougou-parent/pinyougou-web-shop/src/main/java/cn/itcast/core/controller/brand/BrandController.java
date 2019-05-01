package cn.itcast.core.controller.brand;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.brand.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 品牌管理模块
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有的品牌
     *
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    @RequestMapping("/findPage.do")
    public PageResult findPage(Integer pageNo, Integer pageSize) {

        System.out.println("branService:" + brandService.findPage(pageNo, pageSize));
        return brandService.findPage(pageNo, pageSize);
    }

    @RequestMapping("/search.do")
    public PageResult search(Integer pageNo, Integer pageSize, @RequestBody Brand brand) {
        return brandService.search(pageNo, pageSize, brand);
    }

    @RequestMapping("/add.do")
    public Result add(@RequestBody Brand brand) {
        try {
            brandService.add(brand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失敗");
        }
    }

    @RequestMapping("/findOne.do")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }
    @RequestMapping("/update.do")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失敗");
        }
    }
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失敗");
        }
    }
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        System.out.println(brandService.selectOptionList().toString());
        return brandService.selectOptionList();
    }
}
