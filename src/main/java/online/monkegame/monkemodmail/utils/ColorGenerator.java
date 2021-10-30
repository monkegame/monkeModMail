package online.monkegame.monkemodmail.utils;

import java.awt.Color;

public class ColorGenerator {
    //generates a random color
    public Color randomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g ,b);
    }
}