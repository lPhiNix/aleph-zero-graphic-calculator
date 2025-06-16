package com.alephzero.alephzero.db.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "math_expression", schema = "mysqldb")
public class MathExpression {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "expression")
    private String expression;

    @Lob
    @Column(name = "points")
    private String points;

    @Lob
    @Column(name = "preferences", columnDefinition = "LONGTEXT")
    @JdbcTypeCode(SqlTypes.JSON)
    private MathExpressionPreferences preferences;


    @Lob
    @Column(name = "evaluation")
    private String evaluation;

    @Lob
    @Column(name = "calculation")
    private String calculation;

}