package com.placeholder.placeholder.db.models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "expression_preferences", schema = "mysqldb")
public class ExpressionPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "use_global_preferences")
    private Boolean useGlobalPreferences;

    @Column(name = "canvas_preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> canvasPreferences;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "math_expression_id", nullable = false)
    private User mathExpression;

    @Column(name = "math_expression_user_id", nullable = false)
    private Integer mathExpressionUserId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getUseGlobalPreferences() {
        return useGlobalPreferences;
    }

    public void setUseGlobalPreferences(Boolean useGlobalPreferences) {
        this.useGlobalPreferences = useGlobalPreferences;
    }

    public Map<String, Object> getCanvasPreferences() {
        return canvasPreferences;
    }

    public void setCanvasPreferences(Map<String, Object> canvasPreferences) {
        this.canvasPreferences = canvasPreferences;
    }

    public User getMathExpression() {
        return mathExpression;
    }

    public void setMathExpression(User mathExpression) {
        this.mathExpression = mathExpression;
    }

    public Integer getMathExpressionUserId() {
        return mathExpressionUserId;
    }

    public void setMathExpressionUserId(Integer mathExpressionUserId) {
        this.mathExpressionUserId = mathExpressionUserId;
    }

}