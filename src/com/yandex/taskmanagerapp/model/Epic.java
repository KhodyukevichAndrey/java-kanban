package com.yandex.taskmanagerapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task{
    private final List<Integer> epicsSubtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Statuses.NEW);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public List<Integer> getEpicsSubtasksId() {
        return epicsSubtasksId;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(epicsSubtasksId, epic.epicsSubtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicsSubtasksId);
    }
}