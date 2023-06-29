package com.yandex.taskmanagerapp.model;

public class Subtask extends Task{
    private final int idEpic;


    public Subtask(String name, String description, Statuses status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return super.toString() + idEpic;
    }
}
