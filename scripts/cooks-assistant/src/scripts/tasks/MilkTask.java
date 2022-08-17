package scripts.tasks;

import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.util.TribotRandom;
import org.tribot.script.sdk.walking.GlobalWalking;
import scripts.Priority;
import scripts.Task;
import scripts.data.Constants;
import scripts.data.Ingredient;
import scripts.data.Vars;

public class MilkTask implements Task {
    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        // We only want to execute this task when the current ingredient is equal to bucket of milk AND we do not have a bucket of milk in our inventory
        return Vars.get().getCurrentIngredient().equals(Ingredient.BUCKET_OF_MILK) && !Vars.get().getCurrentIngredient().hasIngredient();
    }

    @Override
    public void execute() {
        if (!Inventory.contains(Constants.BUCKET)) { //This code will only execute if we do not have a bucket - we'll need one before we can milk the cow!
            if (!Constants.BUCKET_AREA.containsMyPlayer()) {
                Vars.get().setStatus("Walking to bucket");
                if (GlobalWalking.walkTo(Constants.BUCKET_AREA.getRandomTile()) && Waiting.waitUntil(Constants.BUCKET_AREA::containsMyPlayer)) {
                    Waiting.waitNormal(600, 90);
                }
            }
            if (Constants.BUCKET_AREA.containsMyPlayer()) {
                Vars.get().setStatus("Taking Bucket");
                if (takeBucket() && Waiting.waitUntil(TribotRandom.uniform(1800, 2400), () -> Inventory.contains(Constants.BUCKET))) {
                    //this code will execute if the take bucket method returns true AND waiting until the inventory contains an item that matches the bucket item id
                    //note that I've added a uniform timeout to the Waiting.waitUntil method here
                    Waiting.waitNormal(600, 90);
                }
            }
            //lets return here, because assuming all the code has executed successfully the inventory will now contain a bucket and this code will not execute again
            return;
        }
        if (dairyCowDistance() > TribotRandom.normal(4, 6, 5, 1)) {
            // lets only trigger this code if a dairy cow is not nearby
            Vars.get().setStatus("Walking to cow pen");
            if (GlobalWalking.walkTo(Constants.COW_PEN.getRandomTile()) && Waiting.waitUntil(Constants.COW_PEN::containsMyPlayer)) {
                Waiting.waitNormal(600, 90);
            }
        }
        if (Constants.COW_PEN.containsMyPlayer()) {
            Vars.get().setStatus("Milking Cow");
            if (milkCow() && Waiting.waitUntil(TribotRandom.uniform(2400, 4800), () -> Vars.get().getCurrentIngredient().hasIngredient())) {
                //this code will execute if the milkCow method returns true AND waiting until the player has the curent ingredient succeeds
                //note that I've added a uniform timeout to the Waiting.waitUntil method here
                Waiting.waitNormal(600, 90);
            }
        }
    }

    private boolean takeBucket() {
        return Query.groundItems() //Like the egg a bucket is a ground item, so let's query those
                .idEquals(Constants.BUCKET) //We only want ground items that have an id matching an a bucket
                .findBestInteractable() // Let's make tribot decide which bucket to get
                .map(bucket -> bucket.interact("Take")) //if there is a bucket let's try to pick it up
                .orElse(false); // if there is not a bucket, or interacting with it fails, let's return false
    }

    private boolean milkCow() {
        return Query.gameObjects() //dairy cows are game objects so that's what we'll query
                .nameEquals("Dairy cow") //We only want game objects that have a name matching Dairy cow
                .findBestInteractable() // Let's make tribot decide which dairy cow to get to get
                .map(cow -> cow.interact("Milk")) //if there is a dairy cow let's try to milk it
                .orElse(false); // if there is not a cow, or interacting with it fails, let's return false
    }

    private int dairyCowDistance() {
        return Query.gameObjects() //dairy cows are game objects so that's what we'll query
                .nameEquals("Dairy cow") //We only want game objects that have a name matching Dairy cow
                .findClosest() // we only care about the closest in this case
                .map(cow -> cow.distance()) // let's grab the distance of the closest cow
                .orElse(100); // if there is no nearby cow, let's assume it is 100 tiles away
    }
}
