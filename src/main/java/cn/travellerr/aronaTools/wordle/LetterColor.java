package cn.travellerr.aronaTools.wordle;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@AllArgsConstructor
@Getter
public enum LetterColor {

    // #79B851
    RIGHT(new Color(121, 184, 81)),

    // #F3C237
    WRONG(new Color(243, 194, 55)),

    // #A4AEC4
    NONE(new Color(164, 174, 196));

    private final Color color;

    public static LetterColor getLetterColor(char feedbackChar) {
        return switch (feedbackChar) {
            case '+' -> LetterColor.RIGHT;
            case '-' -> LetterColor.WRONG;
            case '.' -> LetterColor.NONE;
            default -> null;
        };
    }
}
