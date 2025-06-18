package com.example.backend.work;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.work.dto.WorkRequest;
import com.example.backend.s3.S3Uploader;
import com.example.backend.work.dto.WorkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final NaverGeocodingService geocodingService;


    public void createWork(WorkRequest request, MultipartFile image) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

        String imageUrl = null;
        try {
            imageUrl = image != null ? s3Uploader.uploadFile(image) : null;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }

        double[] coordinates = geocodingService.getCoordinatesFromAddress(request.getAddress());
        double latitude = coordinates[0];
        double longitude = coordinates[1];

        if (latitude == 0.0 && longitude == 0.0) {
            // 실제 주소 좌표가 아닌 fallback 좌표일 수 있으므로 로그 남김
            System.err.println("❗️위경도 변환 실패 또는 기본값 반환됨. 주소: " + request.getAddress());
            // 또는 아래처럼 예외를 던져 등록 자체를 막을 수도 있음
            // throw new RuntimeException("위경도 변환 실패: 주소 확인 필요");
        }

        Work work = new Work();
        work.setUser(user);
        work.setPhone(request.getPhone());
        work.setTitle(request.getTitle());
        work.setHourlyWage(request.getHourlyWage());
        work.setPayType(request.getPayType());
        work.setCategory(request.getCategory());
        work.setAddress(request.getAddress());
        work.setDetailAddress(request.getDetailAddress());
        work.setDescription(request.getDescription());
        work.setImageUrl(imageUrl);
        work.setLatitude(latitude);
        work.setLongitude(longitude);

        workRepository.save(work);
    }


    public List<WorkResponse> getAllWorks() {
        return workRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(work -> new WorkResponse(
                        work.getId(),
                        work.getTitle(),
                        work.getCategory(),
                        work.getAddress(),
                        work.getDetailAddress(),
                        work.getDescription(),
                        work.getPhone(),
                        work.getImageUrl(),
                        work.getLatitude(),
                        work.getLongitude()
                )).toList();
    }

    public WorkResponse getWorkById(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 일자리가 존재하지 않습니다."));

        return new WorkResponse(
                work.getId(),
                work.getTitle(),
                work.getCategory(),
                work.getAddress(),
                work.getDetailAddress(),
                work.getDescription(),
                work.getPhone(),
                work.getImageUrl(),
                work.getLatitude(),
                work.getLongitude()
        );
    }
}