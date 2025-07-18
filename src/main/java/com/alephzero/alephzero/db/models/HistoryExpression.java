package com.alephzero.alephzero.db.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "history_expression", schema = "mysqldb")
public class HistoryExpression {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_history_id", nullable = false)
    private UserHistory userHistory;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "math_expression_id", nullable = false)
    private MathExpression mathExpression;


    @ColumnDefault("0")
    @Column(name = "index_order")
    private Integer indexOrder;
}