package com.example.networktechnologiesproject1.services;

import com.example.networktechnologiesproject1.entities.Loan;
import com.example.networktechnologiesproject1.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan saveLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    public Optional<Loan> findById(Integer loanId) {
        return loanRepository.findById(loanId);
    }

    public Iterable<Loan> findAllLoans() {
        return loanRepository.findAll();
    }

    public void deleteLoan(Integer loanId) {
        loanRepository.deleteById(loanId);
    }
}
