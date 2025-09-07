package org.practice.finalprojectsf.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_fk", nullable = false, foreignKey = @ForeignKey(name = "fk_operations_users"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "operation_type", nullable = false)
    private Integer operationType; // 1 = put, 2 = take

    // Для переводов: контрагент (владелец другой стороны операции)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterparty_user_fk", foreignKey = @ForeignKey(name = "fk_operations_counterparty"))
    private User counterpartyUser;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}


