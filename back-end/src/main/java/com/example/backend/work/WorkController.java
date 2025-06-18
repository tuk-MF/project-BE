package com.example.backend.work;

import com.example.backend.work.dto.WorkRequest;
import com.example.backend.work.dto.WorkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createWork(@RequestPart("data") String data,
                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            WorkRequest request = mapper.readValue(data, WorkRequest.class);
            workService.createWork(request, image);
            return ResponseEntity.ok("일자리 등록 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("등록 실패: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<WorkResponse>> getAllWorks() {
        return ResponseEntity.ok(workService.getAllWorks());
    }

    @GetMapping("/{workId}")
    public ResponseEntity<WorkResponse> getWork(@PathVariable Long workId) {
        return ResponseEntity.ok(workService.getWorkById(workId));
    }
}