package com.example.taskmanager.controller;

import com.example.taskmanager.model.Todo;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.TodoService;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.types.AddTodoBody;
import com.example.taskmanager.types.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("todo")
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    @Autowired
    public TodoController(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getTodos() {
        List<Todo> todos =  this.todoService.getTodos();
        Map<String, List<Todo>> todosBody = new HashMap<>();
        todosBody.put("todos",todos);
        return ResponseEntity.ok().body(todosBody);
    }


    @GetMapping("{todoId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity getTodo(@PathVariable("todoId") long todoId) {
        try{
            Todo todo =  this.todoService.getTodo(todoId);
            Map<String, Todo> todosBody = new HashMap<>();
            todosBody.put("todo",todo);
            return ResponseEntity.ok().body(todosBody);
        }catch(Exception e){

            return new ErrorResponse(404,e.getMessage()).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Todo> addTodo(@RequestBody AddTodoBody todoToAdd) {
        try {
            Todo todo = todoToAdd.getTodo();
            long userId = todoToAdd.getUserId();
            String writerUserName =  SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            Todo savedTodo =  this.todoService.addTodo(todo,userId,writerUserName);
            return ResponseEntity.ok().body(savedTodo);
        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }
    }

    @DeleteMapping("{todoId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Todo> deleteTodo(@PathVariable("todoId") long todoId) throws NotFoundException {
        try {
            Todo deleteTodo = this.todoService.deleteTodo(todoId);
            return ResponseEntity.ok().body(deleteTodo);
        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }
    }


    @PatchMapping("{todoId}/state/{todoState}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Todo> updateTodoState(@PathVariable("todoId") long todoId,@PathVariable("todoState") boolean todoState)  {
        try {
            Todo editedToto =  this.todoService.editTodoState(todoId, todoState);
            return ResponseEntity.ok().body(editedToto);

        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }
    }

    @PatchMapping("{todoId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Todo> updateTodo(@PathVariable long todoId, @RequestBody JsonPatch patch) {
        try {
            Todo Todo = todoService.getTodo(todoId);
            Todo todoPatched = todoService.applyPatchToTodo(patch, Todo);
            todoService.editTodo(todoPatched);
            return ResponseEntity.ok(todoPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return new ErrorResponse(500,e.toString()).build();
        } catch (NotFoundException e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }
    }

    @PatchMapping("{todoId}/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Todo> updateTodoUser(@PathVariable("todoId") long todoId, @PathVariable("userId") long userId) {
        try {
            Todo editedToto =  this.todoService.editTodoUser(todoId, userId);
            return ResponseEntity.ok().body(editedToto);

        } catch (Exception e) {
            return new ErrorResponse(404,e.getMessage()).build();
        }
    }

}

