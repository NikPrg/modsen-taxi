package com.example.ridesservice.dto.response.ride;

import com.example.ridesservice.model.projection.RideView;
import lombok.Builder;

import java.util.List;
@Builder
public record AllRidesResponse(
        List<RideView> rideViewList,
        int currentPageNumber,
        int totalPages,
        long totalElements
) {
}
