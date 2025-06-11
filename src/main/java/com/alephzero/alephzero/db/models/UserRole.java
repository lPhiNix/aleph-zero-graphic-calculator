package com.alephzero.alephzero.db.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_role", schema = "mysqldb")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "description", length = 50)
    private String description;

    @Override
    public String toString() {
        return String.format("ROLE_%s", name);
    }
}