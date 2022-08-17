package scripts.data;

import lombok.AllArgsConstructor;
import org.tribot.script.sdk.Inventory;

@AllArgsConstructor
public enum Ingredient {
    EGG(Constants.EGG),
    BUCKET_OF_MILK(Constants.BUCKET_OF_MILK),
    POT_OF_FLOUR(Constants.POT_OF_FLOUR),
    ;
    private int itemID;

    public boolean hasIngredient() {
        return Inventory.contains(this.itemID);
    }
}
