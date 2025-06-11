package com.example.backend.user.bo;

import com.example.backend.enums.UserType;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBO {

    @Autowired
    private UserRepository userRepository;

    public User addUser(String loginId, String hashedPassword, String name, Integer age, String phoneNumber, UserType type, String region) {

        return userRepository.save(User.builder()
                .loginId(loginId)
                .password(hashedPassword)
                .name(name)
                .age(age)
                .phoneNumber(phoneNumber)
                .type(type)
                .region(region)
                .build());
    }

    public User getUserEntityByLoginIdPassword(String loginId, String password) {
        return userRepository.findByLoginIdAndPassword(loginId, password);
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
