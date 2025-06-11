package com.alephzero.alephzero.db.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "mysqldb")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @Lazy
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Column(name = "email", nullable = false, length = 200)
    private String email;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;

    @Size(max = 36)
    @NotNull
    @Column(name = "public_id", nullable = false, length = 36)
    private String publicId;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

}