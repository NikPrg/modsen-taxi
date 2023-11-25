package com.example.driverservice.dto.response;

import com.example.driverservice.model.projections.CarView;
import lombok.Builder;

import java.util.List;

@Builder
public record AllCarsResponseDto(
        List<CarView> carViewList,
        int currentPageNumber,
        int totalPages,
        long totalElements
) {
}