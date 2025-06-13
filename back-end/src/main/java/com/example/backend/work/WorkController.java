package com.example.backend.work;

import com.example.backend.work.dto.WorkRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/works")
public class WorkController {

    private final WorkService workService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createWork(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WorkRequest workRequest = objectMapper.readValue(data, WorkRequest.class);
            workService.createWork(workRequest, image);
            return ResponseEntity.ok("일자리 등록 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("JSON 파싱 실패: " + e.getMessage());
        }
    }

}
