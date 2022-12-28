package top.sheldon.reggie.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.sheldon.reggie.common.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传下载，会使用apache的两个组件
     * - commons-fileupload
     * - commons-io
     * spring web框架封装了这两个组件，简化了上传下载的实现，主需要在controller的方法中声明一个MultipartFile类型的参数即可接收上传的文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {

        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        // 获取后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 通过uuid生成新的文件名
        String fileName = UUID.randomUUID().toString() + suffix;
        // 判断当前文件夹是否存在，如果不存在则创建一个新的文件夹
        File dir = new File(basePath);
        if (!dir.exists()) {
            // 目录不存在，创建新的目录
            dir.mkdirs();
        }
        // file是一个临时文件，需要转存到指定的位置，否则请求结束后会直接删除
        file.transferTo(new File(basePath + fileName));
        return R.success(fileName);
    }

    /**
     * 下载
     * @param name
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            // 创建输入流，读取文件
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));

            // 使用response的输出流返回文件
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            // 关闭资源
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
