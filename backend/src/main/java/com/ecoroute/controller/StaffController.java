package com.ecoroute.controller;

import com.ecoroute.database.StaffDatabaseManager;
import com.ecoroute.model.Staff;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")

public class StaffController {

    @GetMapping("/all")
    public ResponseEntity<List<Staff>> getAllStaff() {
        try
        {
            List<Staff> staffList = StaffDatabaseManager.getAll();
            if (staffList == null || staffList.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(staffList);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createStaff(@RequestBody Staff staff) {
        try
        {
            if (staff == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            StaffDatabaseManager.insert(staff);
            return ResponseEntity.status(HttpStatus.CREATED).body("Staff member" + staff.getFirstName() + " successfully created!");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating staff member: " + e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable int staffId) {
        try
        {
            Staff staff = StaffDatabaseManager.getAll().stream()
                .filter(s -> s.getStaffId() == staffId)
                .findFirst()
                .orElse(null);
            if (staff == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(staff);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editStaff(@RequestBody Staff staff) {
        try
        {
            if (staff == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (staff.getStaffId() <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid staff ID.");
            }
            StaffDatabaseManager.update(staff);
            return ResponseEntity.ok("Staff member " + staff.getFirstName() + " successfully updated!");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating staff member: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteStaff(@RequestParam int staffId) {
        try
        {
            if (staffId <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid staff ID.");
            }
            StaffDatabaseManager.delete(staffId);
            return ResponseEntity.ok("Staff member with ID " + staffId + " successfully deleted!");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting staff member: " + e.getMessage());
        }
    }   
}