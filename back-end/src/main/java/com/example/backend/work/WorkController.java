package com.example.backend.work;

import com.example.backend.ApplicationHistory.ApplicationHistoryBO;
import com.example.backend.common.JwtUtil;
import com.example.backend.user.bo.UserBO;
import com.example.backend.user.entity.User;
import com.example.backend.work.dto.WorkRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private static final Logger log = LoggerFactory.getLogger(WorkController.class);

    private final WorkService workService;
    private final JwtUtil jwtUtil;

    @Autowired
    private UserBO userBO;

    @Autowired
    private ApplicationHistoryBO applicationHistoryBO;

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

    // ✅ 내가 등록한 일자리 목록 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyWorks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        return ResponseEntity.ok(workService.getWorksByUserId(userId));
    }

    // ✅ 내가 등록한 일자리 삭제
    @DeleteMapping("/{workId}")
    public ResponseEntity<?> deleteMyWork(@PathVariable Long workId,
                                          @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        workService.deleteWorkByUser(userId, workId);
        return ResponseEntity.ok("일자리 삭제 완료");
    }

    @PostMapping("/apply")
    public Map<String, Object> applyWork(
            @RequestParam("workId") Long workId,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            result.put("code", 401);
            result.put("error_message", "인증 토큰이 없습니다.");
            return result;
        }

        // 토큰에서 사용자 id 추출
        String token = authorizationHeader.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        if(userId == null) {
            result.put("code", 401);
            result.put("error_message", "유효하지 않은 토큰입니다.");
            return result;
        }

        // DB에서 사용자 정보 조회
        User user = userBO.getUserEntityById(userId);
        if(user == null) {
            result.put("code", 401);
            result.put("error_message", "사용자 정보를 찾을 수 없습니다.");
            return result;
        }

        boolean isApplied = applicationHistoryBO.hasApplied(userId, workId);
        if(isApplied) {
            result.put("code", 400);
            result.put("error_message", "이미 지원한 공고입니다.");
            return result;
        }

        try {
            applicationHistoryBO.apply(userId, workId);
            result.put("code", 200);
            result.put("result", "지원이 완료되었습니다.");
        } catch(Exception e) {
            result.put("code", 500);
            result.put("error_message", "지원 처리 중 오류가 발생했습니다.");
        }

        return result;
    }
}
