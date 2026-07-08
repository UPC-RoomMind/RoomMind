package com.example.web.controller;

import com.example.web.tools.dto.FileResultDto;
import com.example.web.tools.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * 文件上传接口
 */
@RestController()
@RequestMapping("/File")
public class FileController {

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String uploadUrlPrefix;

    /**
     * 批量文件上传
     * @param files 上传的文件
     * @return
     */
    @PostMapping("/BatchUpload")
    public ArrayList<FileResultDto> uploadFile(@RequestParam("file") MultipartFile[] files,HttpServletRequest request) {
        ArrayList<FileResultDto> fileResultDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new CustomException("文件不能为空");
            }
            if (file.getSize() <= 0) {
                throw new CustomException("上传的文件不能为空!请重新上传");
            }
        }

        for (MultipartFile file : files) {
            String originFileName = file.getOriginalFilename();
            Long time = new Date().getTime();

            String dirPath = uploadPath + File.separator + time;

            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(dirPath + File.separator + originFileName);
                fileOutputStream.write(file.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

                String url = uploadUrlPrefix + "/" + time + "/" + originFileName;
                fileResultDtos.add(new FileResultDto(url, originFileName));

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        return fileResultDtos;
    }
}
