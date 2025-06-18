package com.example.backend.work.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}
