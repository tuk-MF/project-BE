package com.example.backend.work.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkRequest {
    private String phone;
    private String title;
    private Integer hourlyWage;
    private String payType;
    private String category;
    private String address;
    private String detailAddress;
    private String description;
}