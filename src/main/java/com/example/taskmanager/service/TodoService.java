package com.example.taskmanager.service;


import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import javassist.NotFoundException;
import com.example.taskmanager.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.taskmanager.repository.TodoRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    @Autowired
    public TodoService(TodoRepository todoRepository, UserRepository userRepository){
        this.todoRepository=todoRepository;
        this.userRepository = userRepository;
    }

    public List<Todo> getTodos(){
        return todoRepository.findAll();
    }

    public Todo addTodo(Todo newTodo,long userId,String writerUserName) throws NotFoundException{
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("todo not found"));
        newTodo.setUser(user);
        User writer = userRepository.findUserByUsername(writerUserName).orElseThrow(()-> new NotFoundException("writer not found"));
        newTodo.setWriter(writer);
        newTodo.setDate(LocalDate.now());
        return todoRepository.save(newTodo);

    }
    public Todo getTodo(long todoId) throws NotFoundException{
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException("Todo not found"));
    }
    public List<Todo> getUserTodos(long userId){
        return todoRepository.findUserTodos(userId);
    }
    public Todo deleteTodo(long todoId) throws NotFoundException{
        Todo todoToDelete = this.todoRepository.findById(todoId)
                .orElseThrow(()-> new NotFoundException("todo not found"));
        todoRepository.deleteById(todoId);
        return todoToDelete;
    }

    public Todo editTodoState(long todoId,boolean state) throws NotFoundException{
        Todo todoToEdit = this.todoRepository.findById(todoId)
                .orElseThrow(()-> new NotFoundException("todo not found"));
        todoToEdit.setState(state);
        return todoToEdit;
    }
    public Todo editTodo(Todo todoToUpdate) throws NotFoundException{
        Todo todoToEdit = this.todoRepository.findById(todoToUpdate.getId())
                .orElseThrow(()-> new NotFoundException("todo not found"));
        todoToEdit.setDescription(todoToUpdate.getDescription());
        todoToEdit.setTitle(todoToUpdate.getTitle());
        todoToEdit.setState(todoToUpdate.isState());
        todoToEdit.setUser(todoToUpdate.getUser());
        return todoToEdit;

    }
    public Todo applyPatchToTodo(JsonPatch patch, Todo targeTodo) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targeTodo, JsonNode.class));
        return objectMapper.treeToValue(patched, Todo.class);
    }

    public Todo editTodoUser(long todoId,long userId)  throws NotFoundException{
        Todo todoToEdit = this.todoRepository.findById(todoId)
                .orElseThrow(()-> new NotFoundException("todo not found"));
        User userToAdd = this.userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        todoToEdit.setUser(userToAdd);
        return todoToEdit;
    }

}
