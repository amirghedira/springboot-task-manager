package com.example.taskmanager.types;

import com.example.taskmanager.model.Todo;

public class AddTodoBody {
    private long userId;
    private Todo todo;

    public AddTodoBody() {

    }
    public AddTodoBody(long userId, Todo todo) {
        this.userId = userId;
        this.todo = todo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }


}
