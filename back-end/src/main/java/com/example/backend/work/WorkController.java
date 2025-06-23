package com.example.backend.work;

import com.example.backend.ApplicationHistory.bo.ApplicationHistoryBO;
import com.example.backend.ApplicationHistory.entity.ApplicationHistory;
import com.example.backend.common.JwtUtil;
import com.example.backend.user.bo.UserBO;
import com.example.backend.user.entity.User;
import com.example.backend.work.dto.WorkRequest;
import com.example.backend.work.dto.WorkResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserBO userBO;

    @Autowired
    private ApplicationHistoryBO applicationHistoryBO;

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

    // 일자리 지원하기
    @PostMapping("/apply")
    public Map<String, Object> applyWork(@RequestParam("workId") Long workId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            result.put("code", 401);
            result.put("error_message", "인증 토큰이 없습니다.");
            return result;
        }

        // 토큰에서 사용자 id 추출
        String token = authorizationHeader.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            result.put("code", 401);
            result.put("error_message", "유효하지 않은 토큰입니다.");
            return result;
        }

        // DB에서 사용자 정보 조회
        User user = userBO.getUserEntityById(userId);
        if (user == null) {
            result.put("code", 401);
            result.put("error_message", "사용자 정보를 찾을 수 없습니다.");
            return result;
        }

        boolean isApplied = applicationHistoryBO.hasApplied(userId, workId);
        if (isApplied) {
            result.put("code", 400);
            result.put("error_message", "이미 지원한 공고입니다.");
            return result;
        }

        try {
            applicationHistoryBO.apply(userId, workId);
            result.put("code", 200);
            result.put("result", "지원이 완료되었습니다.");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("error_message", "지원 처리 중 오류가 발생했습니다.");
        }

        return result;
    }

    // 특정 일자리에 지원한 사용자 목록
    @GetMapping("/applies")
    public ResponseEntity<List<User>> getUsersByWorkId(
            @RequestParam("workId") Long workId) {
        // workId로 지원 내역 가져오기
        List<ApplicationHistory> applicationHistories = applicationHistoryBO.getApplicationHistoryByWorkId(workId);

        // 지원한 사용자 목록 추출
        List<User> users = new ArrayList<>();
        for (ApplicationHistory ah : applicationHistories) {
            users.add(ah.getUser());
        }

        return ResponseEntity.ok(users);
    }
}