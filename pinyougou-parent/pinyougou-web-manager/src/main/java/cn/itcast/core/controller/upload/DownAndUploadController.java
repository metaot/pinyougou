package cn.itcast.core.controller.upload;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.brand.BrandService;
import cn.itcast.core.utils.xls.XlsxWriter;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.awt.print.Book;
import java.io.OutputStream;
import java.util.*;

/**
 * @author 15000
 */
@RestController
public class DownAndUploadController {
    @Reference
    private BrandService brandService;

    @RequestMapping(value = "/brand/downLoad.do" , method = RequestMethod.POST)
    public void exportStudentInfo(HttpServletResponse response,Integer page,Integer rows,@RequestBody Brand Brand) throws Exception {
        page =1;
        rows=40;
        response.setHeader("Content-Disposition", "attachement;filename=studentInfo.xls");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        OutputStream out = response.getOutputStream();
        //数据库读取数据
        List<Brand> list = brandService.search(page,rows,Brand).getRows();
        List<List<String>> data = new ArrayList<>();
        String[] array = {"品牌名称","品牌首字母","商品状态"};
        List<String> title =  Arrays.asList(array);
        data.add(0,title);
        for (Brand brand : list) {
            List<String> line = new ArrayList<>();
           // line.add(0,vo.getStudentName());
            line.add(0,brand.getName());
            line.add(1,brand.getFirstChar());
            line.add(2,brand.getStatus());
            data.add(line);}
        XlsxWriter.bidDataExport("品牌信息" , data ,  out);
        out.flush();
        out.close();
    }
    @RequestMapping(value = "/post/export/Export.do" , method = RequestMethod.POST)
    public ModelAndView ExportPostList(ModelMap model) {
        //到数据库查询出需要导出的数据
        List<Brand> brandList = brandService.findAll();
        //把查询结果放到ModelMap中，在导出工具类ExcelViewList.java中使用
        model.addAttribute("brandList ",brandList );
         // ExcelViewList excelViewList =new ExcelViewList();
        //调用了ExcelViewList并返回视图
        ExcelViewPostList excelViewPostList = new ExcelViewPostList();
        return  new ModelAndView(excelViewPostList,model);
    }

    @RequestMapping("/upload/{guideVideoType}/{fileType}")
    public Map<String, Object> upload(@RequestParam(value = "file") MultipartFile file, @PathVariable Long guideVideoType, @PathVariable String fileType) {
        Map returnData = new HashMap();
        if(file == null || file.isEmpty()){
            returnData.put("flag",false);
            return returnData;
        }

     //   String fileName = manifestService.ctrateGuideFile(file,guideVideoType,fileType);


        returnData.put("seccess",true);
      //  returnData.put("fileName",fileName);
        return returnData;
    }
}
