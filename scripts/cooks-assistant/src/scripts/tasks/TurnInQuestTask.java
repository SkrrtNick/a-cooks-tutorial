package scripts.tasks;

import org.tribot.script.sdk.ChatScreen;
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

import java.util.Arrays;

public class TurnInQuestTask implements Task {
    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        //we only want to execute this code when we have all the ingredients
        return Arrays.stream(Ingredient.values()).allMatch(Ingredient::hasIngredient);
    }

    @Override
    public void execute() {
        int distance = TribotRandom.normal(4, 1);
        if (Constants.COOKS_KITCHEN.distance() > distance) {
            Vars.get().setStatus("Walking to Cook");
            if (GlobalWalking.walkTo(Constants.COOKS_KITCHEN) && Waiting.waitUntil(() -> Constants.COOKS_KITCHEN.distance() <= distance)) {
                Waiting.waitNormal(600, 90);
            }
        }
        if(Constants.COOKS_KITCHEN.distance() <= distance){
            Vars.get().setStatus("Talking to Cook");
            if(talkToCook() && Waiting.waitUntil(()-> ChatScreen.isOpen())){
                Waiting.waitNormal(600, 90);
                if(ChatScreen.handle(Constants.COOK_DIALOGUE) && Waiting.waitUntil(()->Arrays.stream(Ingredient.values()).noneMatch(Ingredient::hasIngredient))){
                    Log.info("Successfully handed in quest");
                    //YAY! This code will only execute if we successfully handle the Cook's chatscreen AND our ingredients are taken from our inventory
                }
            }
        }
    }

    private boolean talkToCook(){
        return Query.npcs()//The cook is an npc, so let's query those
                .nameEquals("Cook") //We only want npcs named cook
                .findBestInteractable() // Let's make tribot decide which egg to get
                .map(cook -> cook.interact("Talk-to")) //if there is a cook let's try to Talk-to him
                .orElse(false); // if there is not a cook, or Talking to him fails, let's return false
    }
}
