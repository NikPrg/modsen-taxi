package com.example.passengerservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PassengerRegistrationDto(

        @Pattern(regexp = "^\\+375 ?\\((17|29|33|44)\\) ?[0-9]{3}-[0-9]{2}-[0-9]{2}$",
                message = "Incorrect format")
        String phone,
        @NotBlank(message = "Name can not be blank")
        @Size(min = 2, max = 100, message = "The name field must contain no less than 2 and no more than 100 letters")
        String firstName,
        @NotBlank(message = "Surname can not be blank")
        @Size(min = 2, max = 100, message = "The surname field must contain no less than 2 and no more than 100 letters")
        String lastName,

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,30}$",
                message = """
                                
                        Incorrect password format
                        The correct password should contain:
                        [at least 1 uppercase letter, at least 1 lowercase letter, at least 1 number, at least 1 special character]
                        Minimum 8 characters and maximum 30 characters.
                               
                        """)
        String password
) {
}
