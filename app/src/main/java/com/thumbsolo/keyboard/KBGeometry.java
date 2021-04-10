package com.thumbsolo.keyboard;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.KeyEvent;
import androidx.core.graphics.ColorUtils;
import java.util.Hashtable;

class KBGeometry {

    static final int UNRECOGNIZED = -10;
    static final int LANGUAGE_SWITCH = -15;
    static final int HR = -20; // Hyper

    private static final int SE = KeyEvent.KEYCODE_SPACE;

    private static final int _A = KeyEvent.KEYCODE_A;
    static final int _B = KeyEvent.KEYCODE_B;
    private static final int _C = KeyEvent.KEYCODE_C;
    private static final int _D = KeyEvent.KEYCODE_D;
    private static final int _E = KeyEvent.KEYCODE_E;
    private static final int _F = KeyEvent.KEYCODE_F;
    private static final int _G = KeyEvent.KEYCODE_G;
    private static final int _H = KeyEvent.KEYCODE_H;
    private static final int _I = KeyEvent.KEYCODE_I;
    private static final int _J = KeyEvent.KEYCODE_J;
    private static final int _K = KeyEvent.KEYCODE_K;
    private static final int _L = KeyEvent.KEYCODE_L;
    private static final int _M = KeyEvent.KEYCODE_M;
    private static final int _N = KeyEvent.KEYCODE_N;
    private static final int _O = KeyEvent.KEYCODE_O;
    private static final int _P = KeyEvent.KEYCODE_P;
    private static final int _Q = KeyEvent.KEYCODE_Q;
    private static final int _R = KeyEvent.KEYCODE_R;
    private static final int _S = KeyEvent.KEYCODE_S;
    private static final int _T = KeyEvent.KEYCODE_T;
    private static final int _U = KeyEvent.KEYCODE_U;
    private static final int _V = KeyEvent.KEYCODE_V;
    private static final int _W = KeyEvent.KEYCODE_W;
    private static final int _X = KeyEvent.KEYCODE_X;
    private static final int _Y = KeyEvent.KEYCODE_Y;
    static final int _Z = KeyEvent.KEYCODE_Z;

    private static final int _0 = KeyEvent.KEYCODE_0;
    private static final int _1 = KeyEvent.KEYCODE_1;
    private static final int _2 = KeyEvent.KEYCODE_2;
    private static final int _3 = KeyEvent.KEYCODE_3;
    private static final int _4 = KeyEvent.KEYCODE_4;
    private static final int _5 = KeyEvent.KEYCODE_5;
    private static final int _6 = KeyEvent.KEYCODE_6;
    private static final int _7 = KeyEvent.KEYCODE_7;
    private static final int _8 = KeyEvent.KEYCODE_8;
    private static final int _9 = KeyEvent.KEYCODE_9;

    private static final int GE = KeyEvent.KEYCODE_GRAVE;
    private static final int MS = KeyEvent.KEYCODE_MINUS;
    private static final int ES = KeyEvent.KEYCODE_EQUALS;
    private static final int LB = KeyEvent.KEYCODE_LEFT_BRACKET;
    private static final int RB = KeyEvent.KEYCODE_RIGHT_BRACKET;
    private static final int BH = KeyEvent.KEYCODE_BACKSLASH;
    private static final int SN = KeyEvent.KEYCODE_SEMICOLON;
    private static final int AE = KeyEvent.KEYCODE_APOSTROPHE;
    private static final int CA = KeyEvent.KEYCODE_COMMA;
    private static final int PD = KeyEvent.KEYCODE_PERIOD;
    private static final int SH = KeyEvent.KEYCODE_SLASH;

    static final int CK = KeyEvent.KEYCODE_CAPS_LOCK;
    static final int FN = KeyEvent.KEYCODE_FUNCTION;
    static final int ST = KeyEvent.KEYCODE_SHIFT_LEFT;
    static final int CL = KeyEvent.KEYCODE_CTRL_LEFT;
    static final int AT = KeyEvent.KEYCODE_ALT_LEFT;
    static final int MA = KeyEvent.KEYCODE_META_LEFT;
    static final int BE = KeyEvent.KEYCODE_DEL;
    static final int DE = KeyEvent.KEYCODE_FORWARD_DEL;
    static final int IT = KeyEvent.KEYCODE_INSERT;
    static final int SQ = KeyEvent.KEYCODE_SYSRQ;
    static final int EE = KeyEvent.KEYCODE_ESCAPE;
    static final int ER = KeyEvent.KEYCODE_ENTER;
    static final int TB = KeyEvent.KEYCODE_TAB;

    static final int UP = KeyEvent.KEYCODE_DPAD_UP;
    static final int DN = KeyEvent.KEYCODE_DPAD_DOWN;
    static final int LT = KeyEvent.KEYCODE_DPAD_LEFT;
    static final int RT = KeyEvent.KEYCODE_DPAD_RIGHT;

    static final int HE = KeyEvent.KEYCODE_MOVE_HOME;
    static final int ED = KeyEvent.KEYCODE_MOVE_END;
    static final int PP = KeyEvent.KEYCODE_PAGE_UP;
    static final int PN = KeyEvent.KEYCODE_PAGE_DOWN;

    static final int F1 = KeyEvent.KEYCODE_F1;
    static final int F2 = KeyEvent.KEYCODE_F2;
    static final int F3 = KeyEvent.KEYCODE_F3;
    static final int F4 = KeyEvent.KEYCODE_F4;
    static final int F5 = KeyEvent.KEYCODE_F5;
    static final int F6 = KeyEvent.KEYCODE_F6;
    static final int F7 = KeyEvent.KEYCODE_F7;
    static final int F8 = KeyEvent.KEYCODE_F8;
    static final int F9 = KeyEvent.KEYCODE_F9;
    static final int F0 = KeyEvent.KEYCODE_F10;
    static final int FA = KeyEvent.KEYCODE_F11;
    static final int FB = KeyEvent.KEYCODE_F12;

    static final int LH = 0;
    static final int RH = 1;
    static final double PI = Math.PI; // shorthand
    static final double LH_OFFSET_ANGLE = 1.5 * PI;
    static final double RH_OFFSET_ANGLE = 1.5 * PI;

    private static final String KEY = "key";
    private static final String KEYSHIFT = "keyShift";
    private static final String HYP = "hyp";
    private static final String HYPSHIFT = "hypShift";

    Hashtable<Integer, Hashtable<Integer, KBSector>> sectorHT;

    int hand;
    private static final int NUM_HANDS = 2;
    DblPt[][] mCorners = new DblPt[NUM_HANDS][4]; // 4 * 2 (LH,RH) corners of KBGeometry

    double s; // side length of thumbsolo box
    float sf; // float side length
    int si; // int side length

    private static final int NUM_CIRCLES = 3;
    FloatPt[] center = new FloatPt[NUM_HANDS];
    float[] radii = new float[NUM_CIRCLES];

    // Color variables, alpha can be set programatically
    private int cGray = Color.GRAY;
    private final float STROKE_WIDTH = 1.5f; // X pixel density on device (see below)
    private final float FONTSIZE = 20f;
    private float pixelDensity;

    Bitmap RHkeyBM, RHkeyShiftBM, RHhypBM, RHhypShiftBM, RHLinesMaskBM;

    // init() Arguments
    private int width, height, alpha;
    private int getDisplayWidth() {
          return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    private int getDisplayHeight() { return Resources.getSystem().getDisplayMetrics().heightPixels;}

    KBGeometry(int hand, float pixelDensity, int alpha) {
        this.hand = hand;
        this.pixelDensity = pixelDensity;
        this.alpha = alpha;

        width = getDisplayWidth();
        height = getDisplayHeight();
        updateKBGeometryAndColorAlpha(alpha);
    }

    void updateKBGeometryAndColorAlpha(int alphaArg) {

        alpha = alphaArg; // update alpha
        cGray = ColorUtils.setAlphaComponent(cGray, alpha);

        sectorHT = getSectorHT();

        s = Math.min(height, width) / 2.0; // side length of solo
        sf = (float) s;
        si = (int) s;

        // LH is bottom left, RH is bottom right
        mCorners[LH][0] = new DblPt(0, 0);
        mCorners[LH][1] = new DblPt(s, 0);
        mCorners[LH][2] = new DblPt(0, s);
        mCorners[LH][3] = new DblPt(s, s);

        mCorners[RH][0] = new DblPt(width - s, 0);
        mCorners[RH][1] = new DblPt(width, 0);
        mCorners[RH][2] = new DblPt(width - s, s);
        mCorners[RH][3] = new DblPt(width, s);

        center[LH] = new FloatPt((float) ((mCorners[LH][0].x + mCorners[LH][3].x) / 2.0),
                            (float) ((mCorners[LH][0].y + mCorners[LH][3].y) / 2.0));
        center[RH] = new FloatPt((float) ((mCorners[RH][0].x + mCorners[RH][3].x) / 2.0),
                            (float) ((mCorners[RH][0].y + mCorners[RH][3].y) / 2.0));

        radii[0] = (float) (Math.sqrt((1.0 / 28.0)) * (s / 2.0));
        radii[1] = (float) (Math.sqrt((10.0 / 28.0)) * (s / 2.0));
        radii[2] = (float) (s / 2.0);

        // Create KBSectorBitmaps
        RHLinesMaskBM = makeSquareLineMaskBitmap(RH);
        RHkeyBM = makeSquareTextBitmap(KEY,RH, RHLinesMaskBM);
        RHkeyShiftBM = makeSquareTextBitmap(KEYSHIFT,RH, RHLinesMaskBM);
        RHhypBM = makeSquareTextBitmap(HYP,RH, RHLinesMaskBM);
        RHhypShiftBM = makeSquareTextBitmap(HYPSHIFT,RH, RHLinesMaskBM);
    }

    private double getOffsetAngle(int hand){
        if (hand == LH)
            return LH_OFFSET_ANGLE;
        else if (hand == RH)
            return RH_OFFSET_ANGLE;
        else
            return 0d; // shouldn't get here
    }

    private Bitmap makeSquareLineMaskBitmap(int hand){

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG); // solid black mask
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.BUTT);
        p.setStrokeWidth(STROKE_WIDTH * pixelDensity);
        p.setColor(Color.BLACK);

        Bitmap maskBM = Bitmap.createBitmap(si,si, Bitmap.Config.ALPHA_8);
        Canvas maskC = new Canvas(maskBM);
        maskC.drawRect(0f,0f,sf,sf,p); // outer rectangle
        FloatPt o = new FloatPt(sf/2,sf/2); // center of square
        for (float r : radii) maskC.drawCircle(o.x, o.y, r, p); // 3 circles
        for (int Y = 1; Y < NUM_CIRCLES; Y++) { // circle separators start on level 2
            double ts = (2.0 * PI) / sectorHT.get(Y).size(); // theta step
            for (int X = 0; X < sectorHT.get(Y).size(); X++)
                maskC.drawLine((float) (o.x + radii[Y-1] * Math.cos(getOffsetAngle(hand) + X * ts)),
                               (float) (o.y + radii[Y-1] * Math.sin(getOffsetAngle(hand) + X * ts)),
                               (float) (o.x + radii[Y] * Math.cos(getOffsetAngle(hand) + X * ts)),
                               (float) (o.y + radii[Y] * Math.sin(getOffsetAngle(hand) + X * ts)),
                        p);
        }
        float r = radii[2]; // corner separators
        maskC.drawLine((float)(o.x+r*Math.cos( .25*PI)),(float)(o.y+r*Math.sin( .25*PI)),sf,sf,p);
        maskC.drawLine((float)(o.x+r*Math.cos( .75*PI)),(float)(o.y+r*Math.sin( .75*PI)), 0,sf,p);
        maskC.drawLine((float)(o.x+r*Math.cos(1.25*PI)),(float)(o.y+r*Math.sin(1.25*PI)), 0, 0,p);
        maskC.drawLine((float)(o.x+r*Math.cos(1.75*PI)),(float)(o.y+r*Math.sin(1.75*PI)),sf, 0,p);

        return maskBM;
    }
    private Bitmap makeSquareTextBitmap(String textType, int hand, Bitmap linesMaskBM){

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        p.setColor(Color.BLACK);

        Bitmap maskBM = Bitmap.createBitmap(si,si, Bitmap.Config.ALPHA_8);
        Canvas maskC = new Canvas(maskBM);
        maskC.drawBitmap(linesMaskBM,0f,0f,p);
        for (int Y = 0; Y < sectorHT.size() ; Y++) {
            for (int X = 0; X < sectorHT.get(Y).size(); X++) {

                String text = "";

                if (textType.equals(KEY))
                    text = sectorHT.get(Y).get(X).key;
                else if (textType.equals(KEYSHIFT))
                    text = sectorHT.get(Y).get(X).keyShift;
                else if (textType.equals(HYP))
                    text = sectorHT.get(Y).get(X).hyp;
                else if (textType.equals(HYPSHIFT))
                    text = sectorHT.get(Y).get(X).hypShift;

                p.setTextSize(FONTSIZE * pixelDensity);
                float vertDisplacement = - ((p.descent() + p.ascent())/2);
                p.setTextAlign(Paint.Align.CENTER);

                FloatPt txtCtr = getTextCenterRH(Y, X);
                Path textPath = new Path();

                int pathHalfLength = 100;
                textPath.moveTo(txtCtr.x - pathHalfLength, txtCtr.y);
                textPath.lineTo(txtCtr.x + pathHalfLength, txtCtr.y);

                maskC.drawTextOnPath(text, textPath, 0, vertDisplacement, p);
            }
        }

        Bitmap squareBM=Bitmap.createBitmap(si,si,Bitmap.Config.ARGB_8888);
        Canvas squareC = new Canvas(squareBM);
        squareC.drawColor(cGray,PorterDuff.Mode.ADD); // gray background

        Paint knockout = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        knockout.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        squareC.drawBitmap(maskBM, 0.f, 0.f, knockout); // knocks out maskBM

        Paint lines = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        lines.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        lines.setColor(ColorUtils.setAlphaComponent(cGray,alpha/2));

        squareC.drawBitmap(linesMaskBM, 0f, 0f, lines); // re-adds linesMaskBM

        return squareBM;
    }

    private FloatPt getTextCenterRH(int Y, int X){

        if (Y == 0){
            return new FloatPt(sf/2,sf/2); // center of square

        } else if (Y == 1 || Y == 2) {
            FloatPt o = new FloatPt(sf/2,sf/2);
            double off_ang = RH_OFFSET_ANGLE;
            double th_step = (2.0 * PI) / sectorHT.get(Y).size();
            return new FloatPt(
                (float)(o.x + ((radii[Y-1]+radii[Y]) / 2) * Math.cos(off_ang + (X+0.5) * th_step)),
                (float)(o.y + ((radii[Y-1]+radii[Y]) / 2) * Math.sin(off_ang + (X+0.5) * th_step)));

        }  else if (Y == 3){

            FloatPt o = new FloatPt(sf/2,sf/2); // center of square

            double[] diagonalAngles = new double[]{ // x,y
                                            RH_OFFSET_ANGLE + 0.25 * PI, RH_OFFSET_ANGLE + 0.25 * PI,
                                            RH_OFFSET_ANGLE + 0.75 * PI, RH_OFFSET_ANGLE + 0.75 * PI,
                                            RH_OFFSET_ANGLE + 1.25 * PI, RH_OFFSET_ANGLE + 1.25 * PI,
                                            RH_OFFSET_ANGLE + 1.75 * PI, RH_OFFSET_ANGLE + 1.75 * PI};

            FloatPt opp = new FloatPt(
                    (float) (o.x + radii[NUM_CIRCLES - 1] *
                                    Math.cos(fitThetaDegWithinUnitRotation(diagonalAngles[X]))),
                    (float) (o.y + radii[NUM_CIRCLES - 1] *
                                    Math.sin(fitThetaDegWithinUnitRotation(diagonalAngles[X]))));

            int[] x_offset_direction_per_X = new int[]{ 0, 1, 1, 0, 0, -1, -1, 0};
            int[] y_offset_direction_per_X = new int[]{-1, 0, 0, 1, 1,  0,  0, -1};


            FloatPt[] c = new FloatPt[4]; // swapped indices, since clock wise from bottom right
            c[0] = new FloatPt(0f,0f);
            c[1] = new FloatPt(sf,0f);
            c[3] = new FloatPt(0f,sf);
            c[2] = new FloatPt(sf,sf);
            int[] c_index_per_X = new int[]{ 0, 1, 1, 2, 2, 3, 3, 0};

            return new FloatPt(
            opp.x + x_offset_direction_per_X[X] * Math.abs(c[c_index_per_X[X]].x - opp.x)/2,
            opp.y + y_offset_direction_per_X[X] * Math.abs(c[c_index_per_X[X]].y - opp.y)/2);

        } else
            return new FloatPt(-1.0f,-1.0f);
    }

    KBTap createTap(double x, double y) {

        String handString;
        DblPt origin;
        double offsetAngle;

        if (isInsideSquare(x, y, mCorners[LH])) {
        //if (hand == LH) {
            hand = LH;
            handString = "LH";
            origin = new DblPt((double) center[LH].x, (double) center[LH].y);
            offsetAngle = LH_OFFSET_ANGLE;
        } else if (isInsideSquare(x, y, mCorners[RH])) {
        //else if (hand == RH) {
            hand = RH;
            handString = "RH";
            origin = new DblPt((double) center[RH].x, (double) center[RH].y);
            offsetAngle = RH_OFFSET_ANGLE;
        } else // outside both ThumbSolo regions
            return new KBTap("", -1, null, 0,
                             new KBSector(-1, -1, UNRECOGNIZED, "", "",
                                                        UNRECOGNIZED, "", ""),
                             System.currentTimeMillis() );

        double rx = x - origin.x;
        double ry = y - origin.y;
        double r = Math.sqrt(Math.pow(rx, 2) + Math.pow(ry, 2));

        int row = determineRow(r);

        return new KBTap(handString, hand, origin, offsetAngle, sectorHT.get(row).get(
                            determineCol(row, sectorHT.get(row).size(), Math.toDegrees(
                                        (((ry >= 0) ? 1 : -1) * Math.acos(rx / r)) - offsetAngle)))
                         , System.currentTimeMillis());
    }
    KBSector getSectNow(double x, double y, KBTap t){

        if (isInsideSquare(x, y, mCorners[t.hand])) {

            double rx = x - t.origin.x;
            double ry = y - t.origin.y;
            double r = Math.sqrt(Math.pow(rx, 2) + Math.pow(ry, 2));
            int row = determineRow(r);

            return sectorHT.get(row).get(
                    determineCol(row, sectorHT.get(row).size(), Math.toDegrees(
                            (((ry >= 0) ? 1 : -1) * Math.acos(rx / r)) - t.offsetAngle)));
        } else // outside both ThumbSolo regions
            return new KBSector(-1, -1, UNRECOGNIZED, "", "", UNRECOGNIZED, "", "");
    }

    private boolean isInsideSquare(double x, double y, DblPt[] corners) {
        return (x >= corners[0].x && x <= corners[3].x && y >= corners[0].y && y <= corners[3].y);
    }
    private int determineRow(double r){
        if (r <= radii[0])
            return 0;
        else if (r <= radii[1])
            return 1;
        else if (r <= radii[2])
            return 2;
        else
            return 3; // to handle outside of main circle
    }
    private int determineCol(int row, int cols, double thetaDeg){
        int col = UNRECOGNIZED;

        if (row == 3) {
            double fitThetaDeg = fitThetaDegWithinUnitRotation(thetaDeg);
            for (int i = 0; i < sectorHT.get(3).size(); i++) {
                if (fitThetaDeg <= fitThetaDegWithinUnitRotation(45.0 + i * 45.0)) {
                    col = i ; // clockwise
                    break;
                }
            }
        } else {
            col = (int) Math.floor(fitThetaDegWithinUnitRotation(thetaDeg) / (360.0 / cols));
            if (col == cols) col = cols - 1; // avoid out of bounds in hashtable
        }
        return col;
    }
    private double fitThetaDegWithinUnitRotation(double theta) {

        // modulus f'n needs positive angle representation
        if (theta < 0.0) {
            while (theta < 0.0)
                theta += 360.0; // wrap around to between 0 and 360 if < 360 degrees
            return theta;
        } else if (theta > 360.0) {
            while (theta > 360.0)
                theta -= 360.0; // wrap around to between 0 and 360 if > 360 degrees
            return theta;
        } else
            return theta;
    }

    private Hashtable<Integer, Hashtable<Integer, KBSector>> getSectorHT(){

        Hashtable<Integer, Hashtable<Integer, KBSector>> s = new Hashtable<>();
        Hashtable<Integer, KBSector> r0 = new Hashtable<>();
        r0.put(0, new KBSector(0, 0, SE, "␣", "␣", SE, "␣", "␣"));
        s.put(0, r0);
        Hashtable<Integer, KBSector> r1 = new Hashtable<>();
        r1.put(0, new KBSector(0, 1, _A, "a", "A", _1, "1", "!"));
        r1.put(1, new KBSector(1, 1, _E, "e", "E", _2, "2", "@"));
        r1.put(2, new KBSector(2, 1, _I, "i", "I", _3, "3", "#"));
        r1.put(3, new KBSector(3, 1, _N, "n", "N", _4, "4", "$"));
        r1.put(4, new KBSector(4, 1, _O, "o", "O", _5, "5", "%"));
        r1.put(5, new KBSector(5, 1, _R, "r", "R", _6, "6", "^"));
        r1.put(6, new KBSector(6, 1, _S, "s", "S", _7, "7", "&"));
        r1.put(7, new KBSector(7, 1, _T, "t", "T", _8, "8", "*"));
        r1.put(8, new KBSector(8, 1, ST, "⇧", "⇧", HR, "⇧", "⇧"));
        s.put(1, r1);
        Hashtable<Integer, KBSector> r2 = new Hashtable<>();
        r2.put(0, new KBSector(0, 2, _B, "b", "B", _0, "0", ")"));
        r2.put(1, new KBSector(1, 2, _C, "c", "C", RB, "]", "}"));
        r2.put(2, new KBSector(2, 2, _D, "d", "D", BH, "\\", "|"));
        r2.put(3, new KBSector(3, 2, _F, "f", "F", F1, "F1", "F1"));
        r2.put(4, new KBSector(4, 2, _G, "g", "G", F2, "F2", "F2"));
        r2.put(5, new KBSector(5, 2, _H, "h", "H", F3, "F3", "F3"));
        r2.put(6, new KBSector(6, 2, _J, "j", "J", F4, "F4", "F4"));
        r2.put(7, new KBSector(7, 2, _K, "k", "K", F5, "F5", "F5"));
        r2.put(8, new KBSector(8, 2, _L, "l", "L", F6, "F6", "F6"));
        r2.put(9, new KBSector(9, 2, _M, "m", "M", F7, "F7", "F7"));
        r2.put(10, new KBSector(10, 2, _P, "p", "P", F8, "F8", "F8"));
        r2.put(11, new KBSector(11, 2, _Q, "q", "Q", F9, "F9", "F9"));
        r2.put(12, new KBSector(12, 2, _U, "u", "U", F0, "F10", "F10"));
        r2.put(13, new KBSector(13, 2, _V, "v", "V", FA, "F11", "F11"));
        r2.put(14, new KBSector(14, 2, _W, "w", "W", FB, "F12", "F12"));
        r2.put(15, new KBSector(15, 2, _X, "x", "X", SH, "/", "?"));
        r2.put(16, new KBSector(16, 2, _Y, "y", "Y", LB, "[", "{"));
        r2.put(17, new KBSector(17, 2, _Z, "z", "Z", _9, "9", "("));
        s.put(2, r2);
        Hashtable<Integer, KBSector> r3 = new Hashtable<>();
        r3.put(0, new KBSector(0, 3, MS, "-", "_", MS, "-", "_"));
        r3.put(1, new KBSector(1, 3, ES, "=", "+", ES, "=", "+"));
        r3.put(2, new KBSector(2, 3, AE, "'", "\"", AE, "'", "\""));
        r3.put(3, new KBSector(3, 3, SN, ";", ":", SN, ";", ":"));
        r3.put(4, new KBSector(4, 3, PD, ".", ">", PD, ".", ">"));
        r3.put(5, new KBSector(5, 3, CA, ",", "<", CA, ",", "<"));
        r3.put(6, new KBSector(6, 3, _9, "9", "(", GE, "`", "~"));
        r3.put(7, new KBSector(7, 3, _0, "0", ")", EE, "␛", "␛"));
        s.put(3, r3);
        return s;
    }
}