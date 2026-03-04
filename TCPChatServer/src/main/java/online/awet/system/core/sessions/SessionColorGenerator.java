package online.awet.system.core.sessions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SessionColorGenerator {

    /** RGB triples in normalized form: r, g, b in [0, 1]. */
    private static final float[][] PALETTE = {
        { 0.90f, 0.22f, 0.27f },   // crimson
        { 0.96f, 0.64f, 0.38f },   // coral
        { 0.91f, 0.77f, 0.42f },   // goldenrod
        { 0.16f, 0.62f, 0.56f },   // teal
        { 0.27f, 0.48f, 0.62f },   // steel blue
        { 0.11f, 0.21f, 0.34f },   // navy
        { 0.61f, 0.36f, 0.90f },   // violet
        { 0.00f, 0.71f, 0.85f },   // cyan
        { 0.48f, 0.17f, 0.75f },   // purple
        { 1.00f, 0.00f, 0.43f },   // magenta
        { 0.23f, 0.53f, 1.00f },   // blue
        { 0.02f, 1.00f, 0.65f },   // spring green
        { 0.98f, 0.34f, 0.03f },   // orange red
        { 1.00f, 0.75f, 0.04f },   // gold
        { 0.51f, 0.22f, 0.93f },   // medium purple
        { 0.30f, 0.79f, 0.94f },   // sky blue
        { 0.18f, 0.77f, 0.71f },   // dark cyan
        { 1.00f, 0.62f, 0.11f },   // dark orange
        { 0.76f, 0.07f, 0.12f },   // dark red
        { 0.36f, 0.30f, 0.49f },   // slate
        { 0.97f, 0.15f, 0.52f },   // deep pink
        { 0.45f, 0.04f, 0.72f },   // indigo
        { 0.34f, 0.04f, 0.68f },   // deep violet
        { 0.25f, 0.43f, 0.79f },   // royal blue
        { 0.26f, 0.38f, 0.79f },   // medium slate blue
        { 0.28f, 0.58f, 0.94f },   // dodger blue
        { 0.32f, 0.72f, 0.94f },   // light sky blue
        { 0.32f, 0.72f, 0.58f },   // cadet blue
        { 0.25f, 0.57f, 0.42f },   // sea green
        { 0.58f, 0.84f, 0.70f },   // mint
        { 0.55f, 0.27f, 0.69f },   // plum
        { 0.89f, 0.47f, 0.36f },   // terracotta
        { 0.12f, 0.66f, 0.54f }    // jungle green
    };

    private static int counter = 0;
    private static final List<Map<String, Float>> colors = generateListOfColors();

    public static Map<String, Float> generateColor() {
        return colors.get(counter++ % colors.size());
    }

    public static List<Map<String, Float>> generateListOfColors() {
        List<Map<String, Float>> list = new ArrayList<>(PALETTE.length);
        for (float[] rgb : PALETTE) {
            list.add(Map.of("r", rgb[0], "g", rgb[1], "b", rgb[2]));
        }
        Collections.shuffle(list);
        return list;
    }
}
