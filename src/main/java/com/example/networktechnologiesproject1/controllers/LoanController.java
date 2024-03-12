package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Loan;
import com.example.networktechnologiesproject1.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Loan addLoan(@RequestBody Loan loan) {
        return loanRepository.save(loan);
    }

    @GetMapping("/getAll")
    public Iterable<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Integer id) {
        return loanRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Integer id, @RequestBody Loan loanDetails) {
        return loanRepository.findById(id)
                .map(existingLoan -> {
                    existingLoan.setBookId(loanDetails.getBookId());
                    existingLoan.setUserId(loanDetails.getUserId());
                    existingLoan.setLoanDate(loanDetails.getLoanDate());
                    existingLoan.setDueDate(loanDetails.getDueDate());
                    existingLoan.setReturnDate(loanDetails.getReturnDate());
                    Loan updatedLoan = loanRepository.save(existingLoan);
                    return new ResponseEntity<>(updatedLoan, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Integer id) {
        return loanRepository.findById(id)
                .map(loan -> {
                    loanRepository.delete(loan);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
