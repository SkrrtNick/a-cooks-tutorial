package scripts;

import org.jetbrains.annotations.NotNull;
import org.tribot.script.sdk.GameState;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.painting.Painting;
import org.tribot.script.sdk.painting.template.basic.BasicPaintTemplate;
import org.tribot.script.sdk.painting.template.basic.PaintLocation;
import org.tribot.script.sdk.painting.template.basic.PaintRows;
import org.tribot.script.sdk.painting.template.basic.PaintTextRow;
import org.tribot.script.sdk.script.ScriptConfig;
import org.tribot.script.sdk.script.TribotScript;
import org.tribot.script.sdk.script.TribotScriptManifest;
import scripts.data.Ingredient;
import scripts.data.Vars;

import java.awt.*;
import java.util.Arrays;

@TribotScriptManifest(name = "A Cooks Tutorial", author = "SkrrtNick", category = "Quest", description = "Completes Cooks Assistant")
public class ACooksTutorial implements TribotScript {

    @Override
    public void configure(@NotNull ScriptConfig config) {
        config.setRandomsAndLoginHandlerEnabled(true);
        config.setBreakHandlerEnabled(true);
    }

    @Override
    public void execute(final String args) {
        PaintTextRow runningPaintTemplate =
                PaintTextRow.builder()
                .background(new Color(120, 123, 128, 180)) // this is the bg colour for our rows
                .build();
        BasicPaintTemplate paint = BasicPaintTemplate.builder()
                .row(PaintRows.scriptName(runningPaintTemplate.toBuilder())) //this method will use the manifest to grab the script name
                .row(PaintRows.runtime(runningPaintTemplate.toBuilder())) //this method will automatically print the value of runtime
                .row(runningPaintTemplate.toBuilder().label("Status")
                        .value(() -> Vars.get().getStatus()).build()) // we can also display our own values
                .row(runningPaintTemplate.toBuilder().label("Current Ingredient")
                        .condition(()->Vars.get().getCurrentIngredient() != null) // we can assign conditions which will dictate whether or not a value is displayed
                        .value(() -> Vars.get().getCurrentIngredient()).build())
                .location(PaintLocation.BOTTOM_LEFT_VIEWPORT)
                .build();
        Painting.addPaint(i -> paint.render(i)); // this is the most important part of painting, actually doing the rendering
        while (Vars.get().isRunning()) {

            if (Vars.get().getCurrentIngredient() == null || Vars.get().getCurrentIngredient().hasIngredient()) {
                //we only want this code to execute if we do not have a currentIngredient set, or if we already ahve the currentIngredient
                Arrays.stream(Ingredient.values()) // this converts the array of Ingredients into a Stream so that we can operate on it
                        .filter(ingredient -> !ingredient.hasIngredient()) // lets only choose from ingredients that we don't have
                        .findAny() // we can choose any as the order in which we grab ingredients does not matter
                        .ifPresent(nextIngredient -> Vars.get().setCurrentIngredient(nextIngredient)); // lets assign the result, if any to currentIngredient
            }

            for (Task task : Vars.get().getTasks()) {
                if (task.validate()) {
                    task.execute();
                }
            }

            Waiting.waitUniform(20, 40); // we need this sleep here to prevent iterating too quickly
        }
    }

}
