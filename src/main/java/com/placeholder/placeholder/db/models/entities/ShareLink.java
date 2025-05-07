package com.placeholder.placeholder.db.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "share_link", schema = "mysqldb")
public class ShareLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "token", nullable = false)
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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