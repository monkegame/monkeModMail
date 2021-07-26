package online.monkegame.monkemodmail.utils;

import java.awt.*;

public class ColorGenerator {

    //generates a random color
    public Color randomColor() {
        int r = (int) (Math.random() * 257) - 1;
        int g = (int) (Math.random() * 257) - 1;
        int b = (int) (Math.random() * 257) - 1;

        return new Color(r, g ,b);
    }


}
