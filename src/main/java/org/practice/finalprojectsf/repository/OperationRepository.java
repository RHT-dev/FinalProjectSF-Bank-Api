package org.practice.finalprojectsf.repository;

import org.practice.finalprojectsf.entity.Operation;
import org.practice.finalprojectsf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByUserOrderByCreatedAtDesc(User user);

    @Query("select o from Operation o where o.user = :user and (:from is null or o.createdAt >= :from) and (:to is null or o.createdAt <= :to) order by o.createdAt desc")
    List<Operation> findByUserAndCreatedAtBetweenOptional(
            @Param("user") User user,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}


