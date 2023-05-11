package com.yandex.taskmanagerapp.models;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> epicsSubtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, "NEW");
    }

    @Override
    public String toString() {
        return "Epic{" + super.toString() + "}";
    }

    public ArrayList<Integer> getEpicsSubtasksId() {
        return epicsSubtasksId;
    }
}