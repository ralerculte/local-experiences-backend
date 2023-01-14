package com.cultegroup.findguide.data.model;

public enum Duration {
    ONE(60),
    ONE_AND_HALF(90),
    TWO(120),
    TWO_AND_HALF(150),
    THREE(180),
    THREE_AND_HALF(210),
    FOUR(240),
    FOUR_AND_HALF(270),
    FIVE(300),
    FIVE_AND_HALF(330),
    SIX(360),
    SIX_AND_HALF(390),
    SEVEN(420),
    SEVEN_AND_HALF(450),
    EIGHT(480),
    EIGHT_AND_HALF(510),
    NINE(540),
    NINE_AND_HALF(570),
    TEN(600),
    TEN_AND_HALF(630),
    ELEVEN(660),
    ELEVEN_AND_HALF(690),
    TWELVE(720),
    TWELVE_AND_HALF(750),
    THIRTEEN(780),
    THIRTEEN_AND_HALF(810),
    FOURTEEN(840),
    FOURTEEN_AND_HALF(870),
    FIFTEEN(900),
    FIFTEEN_AND_HALF(930),
    SIXTEEN(960);

    public final int minutes;

    Duration(int minutes) {
        this.minutes = minutes;
    }
}
