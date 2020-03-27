package tbc.game;

public enum Card {
    Plagiate(-50),
    Party(-10),
    Coffee(10),
    RedBull(20),
    WLAN(40),
    Study(60),
    GoodLecturer(80);

    private int value;

    Card(int val) { value = val;}

}
