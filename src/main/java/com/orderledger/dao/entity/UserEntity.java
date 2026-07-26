package com.orderledger.dao.entity;

import com.orderledger.util.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_ledger_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Ad boş ola bilməz")
    @Size(min = 3, max = 50, message = "Username 3-50 simvol aralığında olmalıdır")
    String username;

    @NotBlank(message = "Şifrə boş ola bilməz.")
    @Size(min = 6, message = "Şifrə 6 simvolda böyük olmalıdır")
    String password;

    @NotBlank(message = "Email daxil edilməlidir")
    @Email(message = "Düzgün email formatı daxil edin")
    String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role boş ola bilməz.")
    Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<OrderEntity> orders;

    @CreationTimestamp
    LocalDateTime createdAt;

}