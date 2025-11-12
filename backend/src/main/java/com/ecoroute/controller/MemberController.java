package com.ecoroute.controller;

import com.ecoroute.database.MemberDatabaseManager;
import com.ecoroute.model.Member;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")

public class MemberController {

    @GetMapping("/all")
    public ResponseEntity<List<Member>> getAllMembers() {
        try
        {
            List<Member> memberList = MemberDatabaseManager.getAll();
            if (memberList == null || memberList.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(memberList);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createMember(@RequestBody Member member) {
        try
        {
            if (member == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            MemberDatabaseManager.insert(member);
            return ResponseEntity.status(HttpStatus.CREATED).body("Member " + member.getFirstName() + " successfully created!");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating member: " + e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable int id) {
        try
        {
            Member member = MemberDatabaseManager.getAll().stream()
                .filter(m -> m.getMemberId() == id)
                .findFirst()
                .orElse(null);
            if (member == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(member);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editMember(@RequestBody Member member) {
        try
        {
            if (member == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (member.getMemberId() <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid member ID.");
            }
            MemberDatabaseManager.update(member);
            return ResponseEntity.ok("Member " + member.getFirstName() + " successfully updated!");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating member: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMember(@RequestParam int memberId) {
        try
        {
            if (memberId <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid member ID.");
            }
            MemberDatabaseManager.delete(memberId);
            return ResponseEntity.ok("Member with ID " + memberId + " successfully deleted!");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting member: " + e.getMessage());
        }
    }
}