package scripts.data;

import org.tribot.script.sdk.types.Area;
import org.tribot.script.sdk.types.WorldTile;

public class Constants {
    public static final int EGG = 1944;
    public static final int BUCKET = 1925;
    public static final int BUCKET_OF_MILK = 1927;
    public static final int GRAIN = 1947;
    public static final int POT = 1931;
    public static final int POT_OF_FLOUR = 1933;

    public static final Area COW_PEN = Area.fromRadius(new WorldTile(3258, 3276, 0), 4);
    public static final Area CHICKEN_COOP = Area.fromRadius(new WorldTile(3230, 3298, 0), 2);
    public static final Area BUCKET_AREA = Area.fromRadius(new WorldTile(3228, 3291, 0), 2);
    public static final Area WHEAT_FARM = Area.fromRadius(new WorldTile(3158, 3299, 0), 4);
    public static final Area FLOUR_MILL_TOP = Area.fromRadius(new WorldTile(3166, 3307, 2), 2);
    public static final Area FLOUR_MILL_GROUND = Area.fromRadius(new WorldTile(3166, 3307, 0), 2);
    public static final WorldTile COOKS_KITCHEN = new WorldTile(3209, 3213, 0);

    public static final int FLOUR_BIN_SETTING = 695;

    public static final String[] COOK_DIALOGUE = {"What's wrong?", "Yes."};
}
