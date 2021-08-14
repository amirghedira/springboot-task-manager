package com.example.taskmanager.controller;


import com.example.taskmanager.model.Todo;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.types.ErrorResponse;
import com.example.taskmanager.types.UpdateUserPassword;
import com.example.taskmanager.types.UpdateUserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user){
        try {
            User createdUser =  userService.addUser(user);
            return ResponseEntity.ok().body(createdUser);

        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>>  getUsers (){
        List<User> users =   userService.getUsers();
        return ResponseEntity.ok().body(users);
    }
    @GetMapping("{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN,ROLE_USER')")
    public ResponseEntity<User> getUser (@PathVariable("userId") long userId){
        try {
            User user =  userService.getUserById(userId);
            return ResponseEntity.ok().body(user);

        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }
    }

    @DeleteMapping("{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> deleteUser (@PathVariable("userId") long userId){
        try {
            User user =  userService.deleteUser(userId);
            return ResponseEntity.ok().body(user);

        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }

    }
    @PatchMapping("{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN,ROLE_USER')")
    public ResponseEntity<User> updateUserInfo (@PathVariable("userId") long userId, @RequestBody JsonPatch patch){
        try {
            User user = userService.getUserById(userId);
            User userPatched = userService.applyPatchToUser(patch, user);
            userService.editUserInfo(userPatched);
            return ResponseEntity.ok(userPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return new ErrorResponse(500,e.toString()).build();
        } catch (NotFoundException e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }

    }

    @PatchMapping("{userId}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> updateUserRole (@PathVariable("userId") long userId, @RequestBody UpdateUserRole userRole){
        try {
            User user =  userService.editUserRole(userId,userRole.getRole());
            return ResponseEntity.ok().body(user);

        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }

    }

    @PatchMapping("{userId}/password")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN,ROLE_USER')")
    public ResponseEntity<User> updateUserPassword (@PathVariable("userId") long userId, @RequestBody UpdateUserPassword userPasswordToUpdate){
        try {
            User user =  userService.updateUserPassword(userId,userPasswordToUpdate.getOldPassword(),userPasswordToUpdate.getNewPassword());
            return ResponseEntity.ok().body(user);

        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }

    }
}
