package taskproject.models;

public class Subtask extends Task{
    private int idEpic;


    public Subtask(String name, String description, String status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" + super.toString() +
                "idEpic=" + idEpic +
                '}';
    }
}
