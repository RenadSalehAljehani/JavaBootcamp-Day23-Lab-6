package com.example.employeemanagementsystem.Controller;

import com.example.employeemanagementsystem.Model.Employee;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employeeManagementSystem")
public class EmployeeController {

    // 1. Creat list of employees
    ArrayList<Employee> employees = new ArrayList<>();

    // 2. Get all employees
    @GetMapping("/getEmployees")
    public ResponseEntity getEmployees() {
        return ResponseEntity.status(200).body(employees);
    }

    // 3. Add a new employee
    @PostMapping("/addEmployee")
    public ResponseEntity addEmployee(@RequestBody @Valid Employee employee, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        employees.add(employee);
        return ResponseEntity.status(200).body("New Employee Added.");
    }

    // 4. Update an employee
    @PutMapping("/updateEmployee/{ID}")
    public ResponseEntity updateEmployee(@PathVariable String ID, @RequestBody @Valid Employee employee, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        // Handel invalid parameter input
        if (ID == null) {
            return ResponseEntity.status(400).body("ID Can't Be Empty.");
        }

        // Verify that the employee exists
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getID().equalsIgnoreCase(ID)) {
                employees.set(i, employee);
                return ResponseEntity.status(200).body("Employee with ID (" + ID + ") Has Been Updated.");
            }
        }
        return ResponseEntity.status(400).body("Employee with ID (" + ID + ") is Not Exists.");
    }

    // 5. Delete an employee
    @DeleteMapping("/deleteEmployee/{ID}")
    public ResponseEntity deleteEmployee(@PathVariable String ID) {
        // Handel invalid parameter input
        if (ID == null) {
            return ResponseEntity.status(400).body("ID Can't Be Empty.");
        }

        // Verify that the employee exists
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getID().equalsIgnoreCase(ID)) {
                employees.remove(i);
                return ResponseEntity.status(200).body("Employee with ID (" + ID + ") Has Been Deleted.");
            }
        }
        return ResponseEntity.status(400).body("Employee with ID (" + ID + ") is Not Exists.");
    }

    // 6. Search for employee by Position
    @GetMapping("/searchEmployee/{position}")
    public ResponseEntity searchEmployee(@PathVariable String position) {
        // Handel invalid parameter input
        if (position == null) {
            return ResponseEntity.status(400).body("Position Can't Be Empty.");
        }
        if (!position.equalsIgnoreCase("supervisor") && !position.equalsIgnoreCase("coordinator")) {
            return ResponseEntity.status(400).body("Invalid Input. Position Must Be Either 'Supervisor' or 'Coordinator'.");
        }

        // Create new list to store employees with same position
        ArrayList<Employee> samePositionEmployees = new ArrayList<>();

        // Loop through all employees
        for (Employee employee : employees) {
            if (employee.getPosition().equalsIgnoreCase(position)) {
                samePositionEmployees.add(employee);
            }
        }
        // No employees in the specified position
        if(samePositionEmployees.isEmpty()){
            return ResponseEntity.status(200).body("There Are No Employees in Position " + position + ".");
        }
        return ResponseEntity.status(200).body(samePositionEmployees);
    }

    // 7. Get Employees by Age Range
    @GetMapping("/getEmployeesByAgeRange/{minAge}/{maxAge}")
    public ResponseEntity getEmployeesByAgeRange(@PathVariable int minAge, @PathVariable int maxAge) {
        // Validate minAge and maxAge range
        if (minAge < 26) {
            return ResponseEntity.status(400).body("Invalid Minimum Age. The Lower Acceptable Age is 26.");
        }
        if (maxAge < minAge) {
            return ResponseEntity.status(400).body("Invalid Age Range. Maximum Age Must Be Greater Than or Equal to Minimum Age.");
        }

        // Create new list to store employees in the age range
        ArrayList<Employee> ageRangeEmployees = new ArrayList<>();

        // Loop through all employees
        for (Employee employee : employees) {
            if (employee.getAge() >= minAge && employee.getAge() <= maxAge) {
                ageRangeEmployees.add(employee);
            }
        }
        // No employees the specified age range
        if(ageRangeEmployees.isEmpty()){
            return ResponseEntity.status(200).body("There Are No Employees Between the Ages of " + minAge + " and " + maxAge + ".");
        }
        return ResponseEntity.status(200).body(ageRangeEmployees);
    }

    // 8. Apply for annual leave
    @PutMapping("/applyAnnualLeave/{ID}")
    public ResponseEntity applyAnnualLeave(@PathVariable String ID) {
        for (int i = 0; i < employees.size(); i++) {
            // Check if the current employee matches the provided ID
            if (employees.get(i).getID().equalsIgnoreCase(ID)) {
                // Check the conditions for applying leave
                if (!employees.get(i).isOnLeave() && employees.get(i).getAnnualLeave() >= 1) {
                    employees.get(i).setOnLeave(true);
                    int decAnnualLeave = employees.get(i).getAnnualLeave() - 1;
                    employees.get(i).setAnnualLeave(decAnnualLeave);
                    return ResponseEntity.status(200).body("Employee with ID (" + ID + ") Has Been Applied for Annual Leave.");
                }
                // If the employee is already on leave
                else if (employees.get(i).isOnLeave()) {
                    return ResponseEntity.status(400).body("Employee with ID (" + ID + ") is Already on Leave.");
                }
                // If the employee has no remaining days of annual leave
                else if (employees.get(i).getAnnualLeave() == 0) {
                    return ResponseEntity.status(400).body("Employee with ID (" + ID + ") Has No Remaining Days of Annual Leave.");
                }
            }
        }
        // If no matching employee ID was found
        return ResponseEntity.status(400).body("Employee with ID (" + ID + ") is not Available in the System.");
    }

    // 9. Get employees with no annual Leave
    @GetMapping("/getConsumedAnnulLeaveEmployees")
    public ResponseEntity getConsumedAnnulLeaveEmployees() {
        // Create new list to store employees who consumed their annul leave days
        ArrayList<Employee> consumedAnnulLeaveEmployees = new ArrayList<>();

        // Loop through all employees
        for (Employee employee : employees) {
            if (employee.getAnnualLeave() == 0) {
                consumedAnnulLeaveEmployees.add(employee);
            }
        }
        // No employees with no annual Leave
        if(consumedAnnulLeaveEmployees.isEmpty()){
            return ResponseEntity.status(200).body("There Are No Employees With No Annual Leave.");
        }
        return ResponseEntity.status(200).body(consumedAnnulLeaveEmployees);
    }

    // 10. Promote employee
    @PutMapping("/promoteEmployee/{supervisorID}/{employeeID}")
    public ResponseEntity promoteEmployee(@PathVariable String supervisorID, @PathVariable String employeeID) {
        // Check that the IDs are different
        if(supervisorID.equalsIgnoreCase(employeeID)){
            return ResponseEntity.status(400).body("Enter Two Different ID's.");
        }
        // First, Validate supervisor existence and position
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getID().equalsIgnoreCase(supervisorID)) {
                if (!employees.get(i).getPosition().equalsIgnoreCase("Supervisor")) {
                    return ResponseEntity.status(400).body("The Requester is Not a Supervisor.");
                }
                // Then, validate and promote the employee
                for (int j = 0; j < employees.size(); j++) {
                    if (employees.get(j).getID().equalsIgnoreCase(employeeID)) {
                        if (employees.get(j).getPosition().equalsIgnoreCase("Coordinator") && !employees.get(j).isOnLeave() && employees.get(j).getAge() >= 30) {
                            employees.get(j).setPosition("Supervisor");
                            return ResponseEntity.status(200).body("Employee With ID (" + employeeID + ") Has Been Promoted to a Supervisor.");
                        }
                        // If the employee is already a supervisor
                        else if (employees.get(j).getPosition().equalsIgnoreCase("Supervisor")) {
                            return ResponseEntity.status(400).body("Employee with ID (" + employeeID + ") is Already a Supervisor.");
                        }
                        // If the employee is on leave
                        else if (employees.get(j).isOnLeave()) {
                            return ResponseEntity.status(400).body("Employee with ID (" + employeeID + ") is Currently on Leave.");
                        }
                        // If the employee age is less than 30
                        else if (employees.get(j).getAge() < 30) {
                            return ResponseEntity.status(400).body("Employee with ID (" + employeeID + ") Age is Less than 30.");
                        }
                    }
                }
                // If no matching employee ID is found
                return ResponseEntity.status(400).body("Employee with ID (" + employeeID + ") is not Available in the System.");
            }
        }
        // If no matching supervisor ID is found
        return ResponseEntity.status(400).body("Supervisor with ID (" + supervisorID + ") is not Available in the System.");
    }
}