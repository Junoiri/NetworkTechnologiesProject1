package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.Loan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends CrudRepository<Loan, Integer> {
    boolean existsByBookIdAndReturnDateIsNull(Integer bookId);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.userId = ?1")
    long countByUserId(Integer userId);

    List<Loan> findByUserId(Integer userId);
}