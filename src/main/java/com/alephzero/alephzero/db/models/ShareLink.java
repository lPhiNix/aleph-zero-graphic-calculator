package com.alephzero.alephzero.db.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Deprecated
@Table(name = "share_link", schema = "mysqldb")
public class ShareLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "math_expression_id", nullable = false)
    private User mathExpression;

    @Column(name = "math_expression_user_id", nullable = false)
    private Integer mathExpressionUserId;
}