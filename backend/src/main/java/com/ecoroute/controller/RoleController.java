package com.ecoroute.controller;

import com.ecoroute.database.RoleDatabaseManager;
import com.ecoroute.model.Role;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")

public class RoleController {

    @GetMapping("/all")
    public ResponseEntity<List<Role>> getAllRoles() {
        try {
            List<Role> roles = RoleDatabaseManager.getAll();
            if (roles == null || roles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(roles);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createRole(@RequestBody Role role) {
        try 
        {
            if (role == null) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            RoleDatabaseManager.insert(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(role.getRoleName() + " successfully created!");
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating role: " + e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable int id) {
        try {
            Role role = RoleDatabaseManager.getAll().stream()
                .filter(r -> r.getRoleId() == id)
                .findFirst()
                .orElse(null);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(role);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<String> updateRole(@RequestBody Role role) {
        try 
        {
            if (role == null) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (role.getRoleId() <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid role ID.");
            }
            RoleDatabaseManager.update(role);
            return ResponseEntity.ok(role.getRoleName() + " successfully updated!");
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating role: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRole(@PathVariable int id) {
        try {
            if (id <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid role ID.");
            }
            RoleDatabaseManager.delete(id);
            return ResponseEntity.ok("Role with ID " + id + " successfully deleted!");
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting role: " + e.getMessage());
        }
    }
}

