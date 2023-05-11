package com.yandex.taskmanagerapp.models;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private String status;


    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
