package scripts.data;

import lombok.Data;
import scripts.TaskSet;
import scripts.tasks.EggTask;
import scripts.tasks.FlourTask;
import scripts.tasks.MilkTask;
import scripts.tasks.TurnInQuestTask;

@Data
public class Vars {

    private boolean isRunning = true;

    private String status = null;

    private Ingredient currentIngredient = null;

    private static Vars instance = new Vars();

    private TaskSet tasks = new TaskSet(new EggTask(), new FlourTask(), new MilkTask(), new TurnInQuestTask());

    public static Vars get() {
        return instance;
    }
}
