package com.example.backend.work.dto;

import com.example.backend.work.Work;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkResponse {

    private Long id;
    private String title;
    private String category;
    private String address;
    private String detailAddress;
    private String description;
    private String phone;
    private String imageUrl;
    private Double latitude;
    private Double longitude;

    public static WorkResponse from(Work work) {
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
