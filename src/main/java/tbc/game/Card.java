package tbc.game;

/**
 * This enum defines the different card types and their values. The cheat cards seem to be never
 * used, because they are not put into a deck, but only used then someone cheats.
 */
public enum Card {
    Plagiarism(-50),
    Party(-10),
    Coffee(10),
    Energy(20),
    WLAN(40),
    Study(60),
    GoodLecturer(80),
    Cheat10(10),
    Cheat20(20),
    Cheat30(30),
    Cheat40(40),
    Cheat50(50),
    Cheat60(60),
    Cheat70(70),
    Cheat80(80),
    Cheat90(90),
    Cheat100(100),
    Cheat110(110),
    Cheat120(120),
    Cheat130(130),
    Cheat140(140),
    Cheat150(150),
    Cheat160(160),
    Cheat170(170),
    Cheat180(180),
    Cheat190(190);

    private final int value;

    Card(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }
}