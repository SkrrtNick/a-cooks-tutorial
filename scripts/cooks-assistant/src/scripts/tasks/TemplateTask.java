package scripts.tasks;

import scripts.Priority;
import scripts.Task;

public class TemplateTask implements Task {
    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void execute() {

    }
}
