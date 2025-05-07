package com.placeholder.placeholder.db.models.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, Object> getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(Map<String, Object> userPreferences) {
        this.userPreferences = userPreferences;
    }

    public Map<String, Object> getCanvasPreferences() {
        return canvasPreferences;
    }

    public void setCanvasPreferences(Map<String, Object> canvasPreferences) {
        this.canvasPreferences = canvasPreferences;
    }

}