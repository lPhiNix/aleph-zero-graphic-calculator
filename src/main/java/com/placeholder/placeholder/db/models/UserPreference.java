package com.placeholder.placeholder.db.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_preferences", schema = "mysqldb")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_settings", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> userPreferences;

    @Column(name = "canvas_preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> canvasPreferences;
}