package scripts.tasks;

import org.tribot.script.sdk.GameState;
import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.util.TribotRandom;
import org.tribot.script.sdk.walking.GlobalWalking;
import scripts.Priority;
import scripts.Task;
import scripts.data.Constants;
import scripts.data.Ingredient;
import scripts.data.Vars;

public class FlourTask implements Task {
    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        // We only want to execute this task when the current ingredient is equal to pot of flour AND we do not have an egg in our inventory
        return Vars.get().getCurrentIngredient().equals(Ingredient.POT_OF_FLOUR) && !Vars.get().getCurrentIngredient().hasIngredient();
    }

    @Override
    public void execute() {
        if (!Inventory.contains(Constants.POT)) { //This code will only execute if we do not have a bucket - we'll need one before we can milk the cow!
            int distance = TribotRandom.normal(3, 6, 4, 1);
            if (Constants.COOKS_KITCHEN.distance() > distance) {
                Vars.get().setStatus("Walking to Pot");
                if (GlobalWalking.walkTo(Constants.COOKS_KITCHEN) && Waiting.waitUntil(() -> Constants.COOKS_KITCHEN.distance() <= distance)) {
                    Waiting.waitNormal(600, 90);
                }
            }
            if (Constants.COOKS_KITCHEN.distance() <= distance) {
                Vars.get().setStatus("Taking Pot");
                if (takePot() && Waiting.waitUntil(TribotRandom.uniform(1800, 2400), () -> Inventory.contains(Constants.POT))) {
                    //this code will execute if the takePot method returns true AND waiting until the inventory contains an item that matches the pot item id
                    //note that I've added a uniform timeout to the Waiting.waitUntil method here
                    Waiting.waitNormal(600, 90);
                }
            }
            //lets return here, because assuming all the code has executed successfully the inventory will now contain a pot and this code will not execute again
            return;
        }
        if (isFlourBinEmpty()) {
            if (!Inventory.contains(Constants.GRAIN)) {
                if (!Constants.WHEAT_FARM.containsMyPlayer() && GlobalWalking.walkTo(Constants.WHEAT_FARM.getRandomTile()) && Waiting.waitUntil(Constants.WHEAT_FARM::containsMyPlayer)) {
                    //This code will execute if we are not initially in the wheat from
                    // then have successfully clicked on a random tile in the wheat farm
                    // and we have successfully waited until we are in the wheat farm
                    Waiting.waitNormal(600, 90); // let's sleep before the next action
                }
                if (Constants.WHEAT_FARM.containsMyPlayer()) {
                    Vars.get().setStatus("Picking Wheat");
                    if (interactObject("Wheat", "Pick") && Waiting.waitUntil(TribotRandom.uniform(3600, 4800), () -> Inventory.contains(Constants.GRAIN))) {
                        //this code will execute if the pickWheat method returns true AND waiting until the inventory contains an item that matches the grain item id
                        //note that I've added a uniform timeout to the Waiting.waitUntil method here
                        Waiting.waitNormal(600, 90);
                    }
                }
                //lets return here, because assuming all the code has executed successfully the inventory will now contain grain and this code will not execute again
                return;
            }
            if (!Constants.FLOUR_MILL_TOP.containsMyPlayer()) {
                Vars.get().setStatus("Walking to the Top of the Flour Mill");
                if (GlobalWalking.walkTo(Constants.FLOUR_MILL_TOP.getRandomTile()) && Waiting.waitUntil(Constants.FLOUR_MILL_TOP::containsMyPlayer)) {
                    Waiting.waitNormal(600, 90); // let's sleep before the next action
                }
            }
            if (Constants.FLOUR_MILL_TOP.containsMyPlayer()) {
                if (interactObject("Hopper", "Fill") && Waiting.waitUntil(() -> !MyPlayer.isMoving() && !Inventory.contains(Constants.GRAIN) && !MyPlayer.isAnimating())) {
                    //when these three conditions are met, we have completed filling the hopper
                    Waiting.waitNormal(600, 90);// let's sleep before the next action
                }
                if (interactObject("Hopper controls", "Operate") && Waiting.waitUntil(() -> !isFlourBinEmpty())) {
                    // after operating with the hopper, the setting that stores the value for whether or not there is flour in the bin changes from 0 to 1
                    Waiting.waitNormal(600, 90);// let's sleep before the next action
                }
            }
        }
        if(!isFlourBinEmpty()){
            if (!Constants.FLOUR_MILL_GROUND.containsMyPlayer()) {
                Vars.get().setStatus("Walking to the Ground of the Flour Mill");
                if (GlobalWalking.walkTo(Constants.FLOUR_MILL_GROUND.getRandomTile()) && Waiting.waitUntil(Constants.FLOUR_MILL_GROUND::containsMyPlayer)) {
                    Waiting.waitNormal(600, 90); // let's sleep before the next action
                }
            }
            Vars.get().setStatus("Emptying Bin");
            if (interactObject("Flour bin", "Empty") && Waiting.waitUntil(TribotRandom.uniform(1800, 2400), () -> Vars.get().getCurrentIngredient().hasIngredient())) {
                //this code will execute if the emptyBin method returns true AND waiting until the player has the curent ingredient succeeds
                //note that I've added a uniform timeout to the Waiting.waitUntil method here
                Waiting.waitNormal(600, 90);
            }
        }
    }

    private boolean interactObject(String name, String action) {
        return Query.gameObjects()
                .nameEquals(name)
                .findBestInteractable()
                .map(wheat -> wheat.interact(action))
                .orElse(false);
    }

    private boolean takePot() {
        return Query.groundItems() //the pot is a ground item, so let's query those
                .idEquals(Constants.POT) //We only want ground items that have an id matching a pot
                .findBestInteractable() // Let's make tribot decide which bucket to get
                .map(bucket -> bucket.interact("Take")) //if there is a bucket let's try to pick it up
                .orElse(false); // if there is not a bucket, or interacting with it fails, let's return false
    }

    private boolean isFlourBinEmpty() {
        // here we check if the flour bin has flour in it by checking the setting that stores the value, this value is gathered via the settings explorer
        return GameState.getSetting(Constants.FLOUR_BIN_SETTING) == 0;
    }
}
