package tbc.game;

/**
 * This enum defines the different card types and their value.
 */
public enum Card {
    Plagiarism(-50),
    Party(-10),
    Coffee(10),
    Energy(20),
    WLAN(40),
    Study(60),
    GoodLecturer(80),
    Cheat180(180);


    private final int value;

    Card(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }
}