package com.yandex.taskmanagerapp.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task{
    private final int idEpic;


    public Subtask(String name, String description, Statuses status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(String name, String description, Statuses status, int duration, LocalDateTime startTime, int idEpic) {
        super(name, description, status, duration, startTime);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return super.toString() + "," + idEpic;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return idEpic == subtask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }
}
