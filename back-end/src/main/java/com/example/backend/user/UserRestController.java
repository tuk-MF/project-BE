package com.example.backend.user;

import com.example.backend.common.EncryptUtils;
import com.example.backend.common.JwtUtil;
import com.example.backend.enums.UserType;
import com.example.backend.user.bo.UserBO;
import com.example.backend.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
