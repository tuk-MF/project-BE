package com.example.backend.work;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.work.Work;
import com.example.backend.work.WorkRepository;
import com.example.backend.work.dto.WorkRequest;
import com.example.backend.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.backend.s3.S3Uploader;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;


    public void createWork(WorkRequest dto, MultipartFile image) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        String imageUrl = null;
        try {
            imageUrl = s3Uploader.uploadFile(image);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }

        Work work = new Work();
        work.setUser(user);
        work.setPhone(dto.getPhone());
        work.setTitle(dto.getTitle());
        work.setHourlyWage(dto.getHourlyWage());
        work.setPayType(dto.getPayType());
        work.setCategory(dto.getCategory());
        work.setAddress(dto.getAddress());
        work.setDetailAddress(dto.getDetailAddress());
        work.setDescription(dto.getDescription());
        work.setImageUrl(imageUrl);

        workRepository.save(work);
    }
}
