package com.example.backend.user;

import com.example.backend.common.EncryptUtils;
import com.example.backend.common.JwtUtil;
import com.example.backend.enums.UserType;
import com.example.backend.user.bo.UserBO;
import com.example.backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserBO userBO;

    // 회원가입
    @PostMapping("/sign-up")
    public Map<String, Object> signUp(
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("type") UserType type,
            @RequestParam(value = "region", required = false) String region) {

        // password 암호화 (md5 알고리즘)
        String hashedPassword = EncryptUtils.md5(password);

        // 사용자 정보 DB에 저장
        User user = userBO.addUser(loginId, hashedPassword, name, age, phoneNumber, type, region);

        // 응답값
        Map<String, Object> result = new HashMap<>();
        if(user != null) {
            result.put("code", 200);
            result.put("result", "성공");
        } else {
            result.put("code", 500);
            result.put("error_message", "회원가입에 실패했습니다.");
        }

        return result;
    }

    // 로그인
    @PostMapping("/sign-in")
    public Map<String, Object> signIn(
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password) {

        // password 암호화
        String hashedPassword = EncryptUtils.md5(password);

        // DB 조회
        User user = userBO.getUserEntityByLoginIdPassword(loginId, hashedPassword);

        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            // 로그인 성공시 JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getId(), user.getLoginId());

            result.put("code", 200);
            result.put("result", "성공");
            result.put("token", token);
            result.put("userName", user.getName());
        } else {
            result.put("code", 403);
            result.put("error_message", "로그인 정보가 일치하지 않습니다.");
        }
        return result;
    }

    @GetMapping("/user/info")
    public Map<String, Object> userInfo(HttpServletRequest request) {
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

        result.put("code", 200);
        result.put("result", "성공");
        result.put("user", user);
        return result;
    }
}
