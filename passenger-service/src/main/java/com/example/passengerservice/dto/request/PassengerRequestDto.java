package com.example.passengerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PassengerRequestDto(
        @NotBlank(message = "Name can not be blank")
        @Size(min = 2, max = 100, message = "The name field must contain no less than 2 and no more than 100 letters")
        String firstName,

        @NotBlank(message = "Surname can not be blank")
        @Size(min = 2, max = 100, message = "The surname field must contain no less than 2 and no more than 100 letters")
        String lastName,

        @Pattern(regexp = "^\\+375 ?\\((17|29|33|44)\\) ?[0-9]{3}-[0-9]{2}-[0-9]{2}$",
                message = "Incorrect format")
        String phone,

        @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'+=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
        message = "Incorrect email format")
        String email
) {
}
