package com.thumbsolo.keyboard;

class KBTap {
    String handString;
    int hand;
    DblPt origin;
    double offsetAngle;
    KBSector sectDown, sectMove, sectUp;
    long tDown, tUp, tNet;
    final int REPEAT_DELAY = 1000; // ms
    final int REPEAT_RATE = 50; // ms
    int timesRepeated;
    boolean repeating, typed, mod, hyp;

    KBTap(String handStringArg, int handArg, DblPt originArg, double offsetAngleArg, KBSector sectDownArg, long tDownArg) {
        handString = handStringArg;
        hand = handArg;
        origin = originArg;
        offsetAngle = offsetAngleArg;
        sectDown = sectDownArg;
        tDown = tDownArg;

        // initializations
        tUp = tDownArg; // tNet == 0
        timesRepeated = 0;
        repeating = false;
        typed = false;
        mod = false;
        hyp = false;
    }

    void calcKeyPressedTimeNet() {
        tNet = tUp - tDown;
    }
}