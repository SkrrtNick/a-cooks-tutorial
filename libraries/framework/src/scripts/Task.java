package scripts;

public interface Task {

    Priority priority();

    boolean validate();

    void execute();

}
