package cn.itcast.core.controller.upload;


import cn.itcast.core.service.user.UserService;
import cn.itcast.core.utils.fdfs.FastDFSClient;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class uploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @Reference
    private UserService userService;

    @RequestMapping("/uploadFile.do")
    public Result upload(MultipartFile file) {
        //读取文件拓展名
        String originalFilename = file.getOriginalFilename();

      //  String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        String extName = FilenameUtils.getExtension(originalFilename);
       // System.out.println(extension);
        //创建FastDfs客户端
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/fdfs_client.conf");
            //3.执行上传
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //拼接返回的url 和ip地址,拼接成完整的url
            String url =FILE_SERVER_URL+path;
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            userService.updatePicSrc(userId,url);
            return  new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
