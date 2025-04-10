package com.admin.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_credential_manager")
public class CustomerCredentialManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "password_hash")
    private String password;
    @Enumerated(EnumType.STRING)  // Store as 'ADMIN', 'USER'
    @Column(name = "role", columnDefinition = "role_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Role role;

    public enum Role {
        ADMIN,
        USER;
    }

}
