package cn.itcast.core.controller.upload;


import cn.itcast.core.utils.fdfs.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
//
@RestController
@RequestMapping("/upload")
public class uploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile.do")
    public Result upload(MultipartFile file) {
        //读取原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取拓展名
        String extName = FilenameUtils.getExtension(originalFilename);
       // System.out.println(extension);
        try {
            //创建FastDfs客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/fdfs_client.conf");
            //3.执行上传
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //拼接返回的url 和ip地址,拼接成完整的url
            String url =FILE_SERVER_URL+path;
            return  new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
