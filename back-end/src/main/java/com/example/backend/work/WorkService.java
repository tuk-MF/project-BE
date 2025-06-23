package com.example.backend.work;

import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.work.dto.WorkRequest;
import com.example.backend.work.dto.WorkResponse;
import com.example.backend.s3.S3Uploader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService {


    private final WorkRepository workRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final NaverGeocodingService geocodingService;

    private static final Logger log = LoggerFactory.getLogger(WorkService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void createWork(WorkRequest request, MultipartFile image, Long userId) {
        log.info("✅ 전달받은 userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

        log.info("✅ 조회된 user: {}", user);
        log.info("✅ user.getId(): {}", user.getId());

        if (!entityManager.contains(user)) {
            log.info("🔁 user is not managed. merging...");
            user = entityManager.merge(user);
        }

        log.info("✅ user is managed: {}", entityManager.contains(user));
        log.info("✅ user.getId() after merge: {}", user.getId());

        // 여기서 null이면 DB에 user가 제대로 저장 안 되어 있는 것
        if (user.getId() == null) {
            throw new IllegalStateException("user.getId()가 null입니다!");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Uploader.uploadFile(image);
            } catch (IOException e) {
                log.error("이미지 업로드 실패", e);
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }

        double[] coordinates = geocodingService.getCoordinatesFromAddress(request.getAddress());

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
        work.setLatitude(coordinates[0]);
        work.setLongitude(coordinates[1]);

        workRepository.save(work);
        log.info("✅ Work 저장 완료. 저장된 ID: {}", work.getId());
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
    public WorkResponse getWorkById(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new RuntimeException("해당 일자리를 찾을 수 없습니다."));
        return WorkResponse.from(work);
    }

    public void updateWork(Long workId, WorkRequest request, Long userId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new RuntimeException("일자리를 찾을 수 없습니다."));

        if (!work.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        work.updateFromRequest(request); // Work 엔티티에 이 메서드 추가 필요
        workRepository.save(work);
    }
    public List<WorkResponse> getWorksByUserId(Long userId) {
        List<Work> works = workRepository.findByUserId(userId);
        return works.stream()
                .map(WorkResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteWorkByUser(Long userId, Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new RuntimeException("일자리를 찾을 수 없습니다."));

        if (!work.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인이 등록한 일자리만 삭제할 수 있습니다.");
        }

        workRepository.delete(work);
    }

    public Work getWorkEntityById(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 일자리가 존재하지 않습니다."));

        return work;
    }
}
