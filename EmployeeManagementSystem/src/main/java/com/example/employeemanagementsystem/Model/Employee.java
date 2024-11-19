package com.example.employeemanagementsystem.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Employee {
    @NotEmpty(message = "ID cannot be null.")
    @Size(min = 3,  message = "ID length must be more than 2 characters.")
    private String ID;
    @NotEmpty(message = "Name cannot be null.")
    @Size(min = 5,  message = "Name length must be more than 5 characters.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only characters (no numbers).")
    private String name;
    @Email(message = "Enter a valid email format.")
    private String email;
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with '05' and consist of exactly 10 digits.")
    private String phoneNumber;
    @NotNull(message = "Age cannot be null.")
    @Min(value = 26, message = "Age must be more than 25." )
    private int age;
    @NotEmpty(message = "Position cannot be null.")
    @Pattern(regexp = "^(?i)(supervisor|coordinator)$", message = "Position must be either 'supervisor' or 'coordinator' only.")
    private String position;
    private boolean onLeave = false;
    @JsonFormat(pattern="yyyy-MM-dd")
    @NotNull(message ="Hire date cannot be null.")
    @PastOrPresent(message = "Hire date should be a date in the present or the past.")
    private LocalDate hireDate;
    @NotNull(message = "Annual leave cannot be null.")
    @PositiveOrZero(message = "Annual leave must be a positive number or zero.")
    private int annualLeave;
}