//테스트 용 api

package com.example.backend.s3.controller;

import com.example.backend.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3TestController {

    private final S3Uploader s3Uploader;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("파일 업로드 요청됨: {}", file.getOriginalFilename());
            String url = s3Uploader.uploadFile(file);
            log.info("파일 업로드 성공: {}", url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.internalServerError().body("업로드 실패: " + e.getMessage());
        }
    }
}