package com.example.backend.ApplicationHistory.bo;

import com.example.backend.ApplicationHistory.entity.ApplicationHistory;
import com.example.backend.ApplicationHistory.repository.ApplicationHistoryRepository;
import com.example.backend.user.bo.UserBO;
import com.example.backend.user.entity.User;
import com.example.backend.work.Work;
import com.example.backend.work.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationHistoryBO {

    @Autowired
    private ApplicationHistoryRepository applicationHistoryRepository;

    @Autowired
    private UserBO userBO;

    @Autowired
    private WorkService workService;

    public Boolean hasApplied(Long userId, Long workId) {
        return applicationHistoryRepository.existsByUserIdAndWorkId(userId, workId);
    }

    public void apply(Long userId, Long workId) {
        User user = userBO.getUserEntityById(userId);
        Work work = workService.getWorkEntityById(workId);

        ApplicationHistory applicationHistory = ApplicationHistory.builder()
                .user(user)
                .work(work)
                .appliedAt(LocalDateTime.now())
                .build();

        applicationHistoryRepository.save(applicationHistory);
    }

    public List<ApplicationHistory> getApplicationHistoryByWorkId(Long workId) {
        return applicationHistoryRepository.findByWorkId(workId);
    }

    public List<ApplicationHistory> getApplicationHistoryByUserId(Long userId) {
        return applicationHistoryRepository.findByUserId(userId);
    }
}
