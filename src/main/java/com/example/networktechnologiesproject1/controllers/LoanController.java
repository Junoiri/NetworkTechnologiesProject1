package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Loan;
import com.example.networktechnologiesproject1.exceptions.BookNotAvailableException;
import com.example.networktechnologiesproject1.exceptions.LoanDateException;
import com.example.networktechnologiesproject1.exceptions.LoanNotFoundException;
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
    public ResponseEntity<Loan> addLoan(@RequestBody Loan loan) {
        validateLoan(loan);
        if (!isBookAvailableForLoan(loan.getBookId())) {
            throw new BookNotAvailableException(loan.getBookId());
        }
        Loan savedLoan = loanRepository.save(loan);
        return new ResponseEntity<>(savedLoan, HttpStatus.CREATED);
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
        validateLoan(loanDetails);
        return loanRepository.findById(id).map(existingLoan -> {
            updateLoanDetails(existingLoan, loanDetails);
            return new ResponseEntity<>(loanRepository.save(existingLoan), HttpStatus.OK);
        }).orElseThrow(() -> new LoanNotFoundException(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Integer id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
        loanRepository.delete(loan);
        return ResponseEntity.noContent().build();
    }
    private void validateLoan(Loan loan) {
        if (loan.getDueDate() != null && loan.getLoanDate() != null && loan.getDueDate().before(loan.getLoanDate())) {
            throw new LoanDateException("Due date cannot be before loan date.");
        }
    }

    private boolean isBookAvailableForLoan(Integer bookId) {
        return !loanRepository.existsByBookIdAndReturnDateIsNull(bookId);
    }

    private void updateLoanDetails(Loan existingLoan, Loan loanDetails) {
        if (loanDetails.getReturnDate() != null && loanDetails.getLoanDate() != null
                && loanDetails.getReturnDate().before(loanDetails.getLoanDate())) {
            throw new LoanDateException("Return date must be after loan date.");
        }
        existingLoan.setBookId(loanDetails.getBookId());
        existingLoan.setUserId(loanDetails.getUserId());
        existingLoan.setLoanDate(loanDetails.getLoanDate());
        existingLoan.setDueDate(loanDetails.getDueDate());
        existingLoan.setReturnDate(loanDetails.getReturnDate());
    }
}