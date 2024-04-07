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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * Controller for managing loans of books to users.
 */
@RestController
@RequestMapping("/loan")
@Tag(name = "Loan Management", description = "Endpoints for managing the lending and returning of books. Allows for the creation, retrieval, update, and deletion of loan records.")
public class LoanController {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @PostMapping("/add")
    @Operation(summary = "Create a loan", description = "Registers a new loan of a book to a user. Ensures the book is available for loan and that the loan dates are valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan successfully created", content = @Content(schema = @Schema(implementation = Loan.class))),
            @ApiResponse(responseCode = "400", description = "Invalid loan details or book not available for loan")
    })
    public ResponseEntity<Loan> addLoan(@RequestBody(description = "Loan object containing book ID, user ID, loan date, and due date") Loan loan) {
        validateLoan(loan);
        if (!isBookAvailableForLoan(loan.getBookId())) {
            throw new BookNotAvailableException(loan.getBookId());
        }
        Loan savedLoan = loanRepository.save(loan);
        return new ResponseEntity<>(savedLoan, HttpStatus.CREATED);
    }
    @GetMapping("/getAll")
    @Operation(summary = "Get all loans", description = "Retrieves a list of all loan records, including information about the book, the user, loan date, due date, and return date.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all loans", content = @Content(schema = @Schema(implementation = Loan.class)))
    public Iterable<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID", description = "Retrieves detailed information about a specific loan using its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loan details", content = @Content(schema = @Schema(implementation = Loan.class))),
            @ApiResponse(responseCode = "404", description = "Loan not found with the provided ID")
    })
    public ResponseEntity<Loan> getLoanById(@PathVariable @Parameter(description = "Unique identifier of the loan to retrieve") Integer id) {
        return loanRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update a loan", description = "Updates the details of an existing loan. Validates the new loan data, including the logic around dates and book availability.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the loan details", content = @Content(schema = @Schema(implementation = Loan.class))),
            @ApiResponse(responseCode = "400", description = "Invalid loan details supplied for update"),
            @ApiResponse(responseCode = "404", description = "Loan not found with the provided ID")
    })
    public ResponseEntity<Loan> updateLoan(@PathVariable @Parameter(description = "Unique identifier of the loan to update") Integer id,
                                           @RequestBody(description = "Updated Loan object with potentially new book ID, user ID, loan date, due date, and return date") Loan loanDetails) {
        validateLoan(loanDetails);
        return loanRepository.findById(id).map(existingLoan -> {
            updateLoanDetails(existingLoan, loanDetails);
            return new ResponseEntity<>(loanRepository.save(existingLoan), HttpStatus.OK);
        }).orElseThrow(() -> new LoanNotFoundException(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a loan", description = "Removes a loan record from the system. This could be used when a loan is cancelled or a book is returned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the loan record"),
            @ApiResponse(responseCode = "404", description = "Loan not found with the provided ID")
    })
    public ResponseEntity<Void> deleteLoan(@PathVariable @Parameter(description = "Unique identifier of the loan to delete") Integer id) {
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