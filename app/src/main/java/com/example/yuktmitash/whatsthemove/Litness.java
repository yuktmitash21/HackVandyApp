package com.example.yuktmitash.whatsthemove;

public enum Litness {
    Litness("Litness"),
    WorseThanListeningToNickelback("Worse than listening to nickelback"),
    BetterThanNothing("Better than Nothing"),
    PrettyDecent("Pretty decent"),
    BestPartyOfTheMonth("Best Party of the month"),
    RAGER("Once in a lifetime experience");

    private String rating;

    Litness(String x) {
        rating = x;
    }

    String getRating() {
        return rating;
    }
}
