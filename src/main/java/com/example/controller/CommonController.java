package com.example.controller;


import com.example.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;


/**
 * 公共接口 文件上传与下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {


    @Value("${my-config.file.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        checkDir();
        String originalFilename = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        int i = originalFilename.lastIndexOf(".");  // 获取文件名中最后一个. 的下标用来截取后缀名。
        String suffix = originalFilename.substring(i);  // 截取后缀名
        // 转存到指定位置
        file.transferTo(new File(basePath + uuid + suffix));
        return Result.success(uuid.toString() + suffix);
    }


    /**
     * 文件下载
     * @return
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());

//        response.setContentType("image/png");
        try (FileInputStream fileInputStream = new FileInputStream(basePath + name);
             BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
             ) {
            int data = -1;
            while ((data = inputStream.read()) != -1) {
                bufferedOutputStream.write(data);
            }
        }
        /**
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            httpHeaders.setContentLength(contentLengthOfStream);
            return new ResponseEntity(inputStreamResource, httpHeaders, HttpStatus.OK);
         */

    }

    public void checkDir() {
        File file = new File(basePath);
        if (!file.exists())
            file.mkdir();
    }
}
