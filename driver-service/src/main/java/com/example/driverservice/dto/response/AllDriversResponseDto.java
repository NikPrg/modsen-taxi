package com.example.driverservice.dto.response;

import com.example.driverservice.model.projections.DriverView;
import lombok.Builder;

import java.util.List;
@Builder
public record AllDriversResponseDto(
        List<DriverView> driverViewList,
        int currentPageNumber,
        int totalPages,
        long totalElements
) {
}