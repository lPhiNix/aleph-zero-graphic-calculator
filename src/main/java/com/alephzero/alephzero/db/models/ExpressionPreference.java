package com.alephzero.alephzero.db.models;

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
}