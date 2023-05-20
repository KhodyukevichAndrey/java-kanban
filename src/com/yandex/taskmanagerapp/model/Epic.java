package com.yandex.taskmanagerapp.model;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> epicsSubtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Statuses.NEW);
    }

    @Override
    public String toString() {
        return "Epic{" + super.toString() + "}";
    }

    public ArrayList<Integer> getEpicsSubtasksId() {
        return epicsSubtasksId;
    }
}