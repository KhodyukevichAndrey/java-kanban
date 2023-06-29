package com.yandex.taskmanagerapp.model;

import java.util.ArrayList;
import java.util.List;

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
}