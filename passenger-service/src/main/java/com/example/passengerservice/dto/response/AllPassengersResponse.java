package com.example.passengerservice.dto.response;

import com.example.passengerservice.model.projections.PassengerView;
import lombok.Builder;

import java.util.List;

@Builder
public record AllPassengersResponse(
        List<PassengerView> passengerViewList,
        int currentPageNumber,
        int totalPages,
        long totalElements
) {
}
