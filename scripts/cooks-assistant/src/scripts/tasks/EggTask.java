package scripts.tasks;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.util.TribotRandom;
import org.tribot.script.sdk.walking.GlobalWalking;
import scripts.Priority;
import scripts.Task;
import scripts.data.Constants;
import scripts.data.Ingredient;
import scripts.data.Vars;

public class EggTask implements Task {
    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        // We only want to execute this task when the current ingredient is equal to egg AND we do not have an egg in our inventory
        return Vars.get().getCurrentIngredient().equals(Ingredient.EGG) && !Vars.get().getCurrentIngredient().hasIngredient();
    }

    @Override
    public void execute() {
        if (!Constants.CHICKEN_COOP.containsMyPlayer()) {
            Vars.get().setStatus("Walking to Egg"); //we need to walk to the egg, so why don't we set the status to that
            if (GlobalWalking.walkTo(Constants.CHICKEN_COOP.getRandomTile()) && Waiting.waitUntil(Constants.CHICKEN_COOP::containsMyPlayer)) {
                //This code will execute if we have successfully clicked on a random tile in the chicken coop
                // and we have successfully waited until we are in the chicken coop
                Waiting.waitNormal(600, 90); // let's sleep before the next action
            } else {
                //this code will execute if we have either failed to walk to the random tile in the chicken coop,
                // or the waiting condition has timed out before we make it to the coop
                return; //lets return early as we don't want to execute anything underneath this
            }
        }
        Vars.get().setStatus("Taking Egg");
        if (takeEgg() && Waiting.waitUntil(TribotRandom.uniform(1800, 2400), () -> Vars.get().getCurrentIngredient().hasIngredient())) {
            //this code will execute if the take egg method returns true AND waiting until the player has the curent ingredient succeeds
            //note that I've added a uniform timeout to the Waiting.waitUntil method here
            Waiting.waitNormal(600, 90);
        }
    }

    private boolean takeEgg() {
        return Query.groundItems() //An egg is a ground item, so let's query those
                .idEquals(Constants.EGG) //We only want ground items that have an id matching an eggs
                .findBestInteractable() // Let's make tribot decide which egg to get
                .map(egg -> egg.interact("Take")) //if there is an egg let's try to pick it up
                .orElse(false); // if there is not an egg, or taking it fails, let's return false
    }
}
