package com.example.backend.user;

import com.example.backend.common.EncryptUtils;
import com.example.backend.user.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    // 회원가입
    @PostMapping("/sign-up")
    public Map<String, Object> signUp(
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password) {

        // password 암호화 (md5 알고리즘)
        String hashedPassword = EncryptUtils.md5(password);

        // 사용자 정보 DB에 저장
        User user;

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("result", "성공");

        return result;
    }
}
