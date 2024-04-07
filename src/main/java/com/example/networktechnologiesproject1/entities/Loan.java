package com.example.networktechnologiesproject1.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(description = "Entity representing a loan of a book to a user")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier for each loan", example = "101", required = true)
    private Integer loanId;

    @Schema(description = "Identifier for the book that is loaned", example = "1")
    private Integer bookId;

    @Schema(description = "Identifier for the user who has borrowed the book", example = "42")
    private Integer userId;

    @Schema(description = "The date when the book was borrowed", example = "2024-02-07T00:00:00Z")
    private Date loanDate;

    @Schema(description = "The due date by which the book should be returned", example = "2024-04-07T00:00:00Z")
    private Date dueDate;

    @Schema(description = "The actual date the book was returned", example = "2024-03-07T00:00:00Z", nullable = true)
    private Date returnDate;


    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}
