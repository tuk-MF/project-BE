package com.example.backend.work;

import com.example.backend.common.JwtUtil;
import com.example.backend.work.dto.WorkRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private static final Logger log = LoggerFactory.getLogger(WorkController.class);

    private final WorkService workService;
    private final JwtUtil jwtUtil;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createWork(@RequestPart("data") WorkRequest request,
                                        @RequestPart(value = "image", required = false) MultipartFile image,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("🔐 Authorization Header: {}", authHeader);
            String token = authHeader.replace("Bearer ", "");
            log.info("🔐 token: {}", token);

            Long userId = jwtUtil.getUserIdFromToken(token);
            log.info("🔐 userId from token: {}", userId);

            if (userId == null) {
                return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
            }

            // ✅ ObjectMapper 제거
            workService.createWork(request, image, userId);
            return ResponseEntity.ok("일자리 등록 완료");

        } catch (Exception e) {
            log.error("❌ 등록 실패", e);
            return ResponseEntity.badRequest().body("등록 실패: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllWorks() {
        return ResponseEntity.ok(workService.getAllWorks());
    }
    @GetMapping("/{workId}")
    public ResponseEntity<?> getWorkById(@PathVariable Long workId) {
        return ResponseEntity.ok(workService.getWorkById(workId));
    }
    @PatchMapping("/{workId}")
    public ResponseEntity<?> updateWork(@PathVariable Long workId,
                                        @RequestBody WorkRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        workService.updateWork(workId, request, userId);
        return ResponseEntity.ok("일자리 수정 완료");
    }


}
