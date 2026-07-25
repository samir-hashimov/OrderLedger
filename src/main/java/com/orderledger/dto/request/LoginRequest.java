package com.orderledger.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class LoginRequest {

    @NotBlank(message = "Email daxil edilməlidir")
    @Email(message = "Düzgün email formatı daxil edin")
    String email;

    @NotBlank(message = "Şifrə boş ola bilməz.")
    @Size(min = 6, message = "Şifrə 6 simvolda böyük olmalıdır")
    String password;
}
