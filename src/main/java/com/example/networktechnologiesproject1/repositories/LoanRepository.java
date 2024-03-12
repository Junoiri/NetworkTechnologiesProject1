package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends CrudRepository<Loan, Integer> {
}
