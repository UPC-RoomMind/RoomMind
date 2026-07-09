package com.example.web.controller;

import com.example.web.tools.BaseContext;
import com.example.web.tools.dto.CurrentUserDto;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@RestController()
@RequestMapping("/File")
public class FileController {

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String uploadUrlPrefix;

    @Value("${app.upload.access-path:/uploads}")
    private String uploadAccessPath;

    @PostMapping("/BatchUpload")
    public ArrayList<FileResultDto> uploadFile(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {
        ArrayList<FileResultDto> fileResultDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new CustomException("文件不能为空");
            }
            if (file.getSize() <= 0) {
                throw new CustomException("上传的文件不能为空!请重新上传");
            }
        }

        CurrentUserDto currentUser = BaseContext.getCurrentUserDto();
        Integer userId = currentUser != null ? currentUser.getUserId() : 0;

        for (MultipartFile file : files) {
            String originFileName = file.getOriginalFilename();
            Long time = new Date().getTime();

            String dirPath = uploadPath + File.separator + userId + File.separator + time;

            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(dirPath + File.separator + originFileName)) {
                fileOutputStream.write(file.getBytes());
                fileOutputStream.flush();
            } catch (IOException e) {
                throw new CustomException("文件上传失败");
            }

            String url = uploadUrlPrefix + uploadAccessPath + "/" + userId + "/" + time + "/" + originFileName;
            fileResultDtos.add(new FileResultDto(url, originFileName));
        }

        return fileResultDtos;
    }
}
