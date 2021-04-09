package com.thumbsolo.keyboard;

class KBSector {
    int X, Y, keycode, hypcode;
    String key, keyShift, hyp, hypShift;

    KBSector (int XArg, int YArg, int keycodeArg, String keyArg, String keyShiftArg, int hypcodeArg, String hypArg, String hypShiftArg) {
        X = XArg;
        Y = YArg;
        keycode = keycodeArg;
        key = keyArg;
        keyShift = keyShiftArg;
        hypcode = hypcodeArg;
        hyp = hypArg;
        hypShift = hypShiftArg;
    }
}
