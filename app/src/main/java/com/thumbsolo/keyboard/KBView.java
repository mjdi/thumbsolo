package com.thumbsolo.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import java.util.Hashtable;

public class KBView extends View
{
    static final int LH = 0;
    static final int RH = 1;

    int width, height;
    int hand;
    int orientation, navBarHeight, statusBarHeight;
    float pixelDensity;

    // Color constants
    private int alpha = 100; // default
    private int alphaStepSize = 10; // default
    private final int minimumAlphaLevel = 20;
    private final int maximumAlphaLevel = 200;

    Window W;
    KBGeometry G;
    OnTapListener onTapListener;
    Hashtable<Integer, KBTap> t = new Hashtable<>(); // hash of pointerID to recorded taps (finger input)
    private int[] numTimesModHeld = new int[]{0,0,0,0,0,0,0,0};
    private int numTimesHypHeld = 0;
                                // LH       RH
    private boolean[] revolving = {false, false}; // tells MotionMove that we need to only remove pId from t
    private boolean[] bkspNotArrows = {true, true}; // tells MotionMove whether revolving is bksp/enter or arrow keys
    private boolean[] keycodeNotHypecode = {true, true}; // tells KBView whether to use keycode (true) or hypcode (false)
    private boolean[] capsLockOn = {false, false}; // toggles capslock function (TBD) on an off per hand
    private boolean[] insertOn = {false, false}; // toggles insert function (TBD) on an off per hand

    private int getOppositeHand(int hand){
        return (hand == LH) ? RH : LH;
    }

    private int getNavBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                                                "dimen", "android");
        if (resourceId > 0) {
            int navBarHeight = resources.getDimensionPixelSize(resourceId);
            return navBarHeight;
        }
        return 0;
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                                                    "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private int getDisplayWidth() { return Resources.getSystem().getDisplayMetrics().widthPixels; }
    private int getDisplayHeight() { return Resources.getSystem().getDisplayMetrics().heightPixels;}
    public void setAlphaComponentOfGeometry(int alphaArg){
        int newPaneAlpha;
        if (alphaArg >= maximumAlphaLevel)
            newPaneAlpha = maximumAlphaLevel;
        else if (alphaArg <= minimumAlphaLevel)
            newPaneAlpha = minimumAlphaLevel;
        else
            newPaneAlpha = alphaArg;
        G.updateKBGeometryAndColorAlpha(newPaneAlpha);
        invalidate();
    }

    public void swapActiveHand(){
        if (hand == LH)
            hand = RH;
        else if (hand == RH)
            hand = LH;
    }

    public KBView(Context context, AttributeSet attributes, int styleDef) {
        super(context,attributes, styleDef);
    }

    public KBView(Context context, AttributeSet attributes) {
        super(context,attributes);
    }

    public KBView(Context context, int hand) {
        super(context);
        orientation = getResources().getConfiguration().orientation;
        navBarHeight = getNavBarHeight();
        statusBarHeight = getStatusBarHeight();
        pixelDensity = context.getResources().getDisplayMetrics().density;
        this.hand = hand;

        /*
        if (hand == LH)
            this.setLayoutParams(new RelativeLayout.LayoutParams(s, s));
        else
            this.setLayoutParams(new RelativeLayout.LayoutParams(s, s));

        */
    }

    void createKBGeometry(){
        G = new KBGeometry(hand, orientation, navBarHeight, statusBarHeight, pixelDensity, alpha);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        boolean consumed = false; // assume not consumed unless otherwise determined consumed;
        switch (me.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                consumed = motionEventGeneralActionDown(me); break; //pIdx==0
            case MotionEvent.ACTION_POINTER_DOWN:
                consumed = motionEventGeneralActionDown(me); break; //pIdx!=0
            case MotionEvent.ACTION_MOVE: motionEventActionMove(me); break;
            case MotionEvent.ACTION_UP: motionEventGeneralActionUp(me); break;
            case MotionEvent.ACTION_POINTER_UP: motionEventGeneralActionUp(me); break;
            //case MotionEvent.ACTION_OUTSIDE:
            //    int test = 0;
            //    break;
        }
        return consumed;
    }
    private boolean motionEventGeneralActionDown(MotionEvent m) {
        int pIdx = ((m.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pId = m.getPointerId(pIdx);
        t.put(pId, G.createTap((double)m.getX(pIdx), (double)m.getY(pIdx)));

        KBTap down = t.get(pId);
        if (down.sectDown.keycode == G.ST){
            if (down.hand == LH)
                numTimesModHeld[0] = 1;
            else
                numTimesModHeld[7] = 1;
            invalidate();
        }

        //invalidate();

        return true; // t.get(pId).sectDown.keycode != G.UNRECOGNIZED; // only true if recognized
    }
    private void motionEventActionMove(MotionEvent m) {
        long now = System.currentTimeMillis();

        int pId; // loop through unordered pointer indices (up to count)
        for (int pIdx = 0; pIdx < m.getPointerCount(); pIdx++) {
            pId = m.getPointerId(pIdx);

            if (t.containsKey(pId)) {
                KBTap down = t.get(pId);
                int H = down.hand;
                double xMove = (double) m.getX(pIdx);
                double yMove = (double) m.getY(pIdx);
                KBSector move = G.getSectNow(xMove, yMove, down);

                // Swipe gesture logic

                // Not-Revolving
                if (!revolving[H] && move.Y < down.sectDown.Y) { // Y -> Y - 1
                    if (down.sectDown.keycode == G.ST) {
                        if (down.hand == LH)
                            numTimesModHeld[0] = 0;
                        else
                            numTimesModHeld[7] = 0;
                        capsLockOn[LH] = !capsLockOn[LH]; // Shift down is CapsLock
                        capsLockOn[RH] = !capsLockOn[RH]; // Shift down is CapsLock
                        invalidate();
                    } else // shifted keys on swipe down
                        onTapListener.onTap(new KBEvent(keycodeNotHypecode[H] ? down.sectDown.keycode : down.sectDown.hypcode,
                                               1, false, false));
                    t.remove(pId);
                } else if ( !revolving[H] && move.Y > down.sectDown.Y) { // Y -> Y + 1
                    if (down.sectDown.keycode == G.ST) {
                        keycodeNotHypecode[LH] = !keycodeNotHypecode[LH]; // swap hyper state
                        keycodeNotHypecode[RH] = !keycodeNotHypecode[RH]; // swap hyper state
                        if (down.hand == LH) // get rid of shift
                            numTimesModHeld[0] = 0;
                        else
                            numTimesModHeld[7] = 0;
                        invalidate();
                    } else if (move.Y == 1) {
                        bkspNotArrows[LH] = !bkspNotArrows[LH]; // swap bkspNotArrows
                        bkspNotArrows[RH] = !bkspNotArrows[RH]; // swap bkspNotArrows
                    } else if (move.Y == 2)
                        onTapListener.onTap(new KBEvent(keycodeNotHypecode[H] ? down.sectDown.hypcode : down.sectDown.keycode,
                                                        ((move.X - (2 *down.sectDown.X)) == 0) ? 0 : 1, false, false));
                    else if (move.Y == 3)
                        if (!keycodeNotHypecode[H]) // only allow hyp code on keycode level
                        onTapListener.onTap(new KBEvent(keycodeNotHypecode[H] ? down.sectDown.hypcode : down.sectDown.keycode,
                                                        getMod(), false, false));
                    t.remove(pId);

                } // Revolving
                else if (move.X < down.sectDown.X) {  // X -> X - 1
                    if (down.sectDown.Y == 1 && move.Y == 1)
                        if (!bkspNotArrows[H])
                            onTapListener.onTap(new KBEvent( G.UP, getMod(), false, false));
                        else {
                            if (H == LH)
                                alpha = alpha - alphaStepSize; // TBD: make it per hand
                            else
                                alpha = alpha - alphaStepSize;
                            setAlphaComponentOfGeometry(alpha);
                        }
                    else if (down.sectDown.Y == 2 && move.Y == 2)
                        onTapListener.onTap(new KBEvent( bkspNotArrows[H] ? G.BE : G.LT, getMod(), false, false));
                    else if (down.sectDown.Y == 3 && move.Y == 3)
                        if (down.sectDown.X == 7 || down.sectDown.X == 0)
                            onTapListener.onTap(new KBEvent(G.ER, getMod(), false, false));
                        else if (down.sectDown.X == 1 || down.sectDown.X == 2)
                            if (H == LH)
                                numTimesModHeld[2] = 0; // LH Alt
                            else
                                numTimesModHeld[5] = 0; // RH Alt
                        else if (down.sectDown.X == 3 || down.sectDown.X == 4)
                            if (H == LH)
                                numTimesModHeld[3] = 0; // LH Meta
                            else
                                numTimesModHeld[4] = 0; // RH Meta
                        else if (down.sectDown.X == 5 || down.sectDown.X == 6)
                            if (H == LH)
                                numTimesModHeld[1] = 0; // LH Ctrl
                            else
                                numTimesModHeld[6] = 0; // RH Ctrl
                    revolving[H] = true;
                    if (move.Y == down.sectDown.Y) {
                        if (down.sectDown.keycode == G.ST){ // remove shift
                            if (down.hand == LH)
                                numTimesModHeld[0] = 0;
                            else
                                numTimesModHeld[7] = 0;
                            invalidate();
                        }
                        down.sectDown = move;
                    }
                } else if (move.X > down.sectDown.X) { // X -> X + 1
                    if (down.sectDown.Y == 1 && move.Y == 1)
                        if (!bkspNotArrows[H])
                            onTapListener.onTap(new KBEvent( G.DN, getMod(), false, false));
                        else {
                            if (H == LH)
                                alpha = alpha + alphaStepSize; // TBD: make it per hand
                            else
                                alpha = alpha + alphaStepSize;
                            setAlphaComponentOfGeometry(alpha);
                        }
                    else if (down.sectDown.Y == 2 && move.Y == 2)
                        onTapListener.onTap(new KBEvent( bkspNotArrows[H] ? G.DE : G.RT, getMod(), false, false));
                    else if (down.sectDown.Y == 3 && move.Y == 3)
                        if (down.sectDown.X == 7 || down.sectDown.X == 0)
                            onTapListener.onTap(new KBEvent(G.TB, getMod(), false, false));
                        else if (down.sectDown.X == 1 || down.sectDown.X == 2)
                            if (H == LH)
                                numTimesModHeld[2] = 1; // LH Alt
                            else
                                numTimesModHeld[5] = 1; // RH Alt
                        else if (down.sectDown.X == 3 || down.sectDown.X == 4)
                            if (H == LH)
                                numTimesModHeld[3] = 1; // LH Meta
                            else
                                numTimesModHeld[4] = 1; // RH Meta
                        else if (down.sectDown.X == 5 || down.sectDown.X == 6)
                            if (H == LH)
                                numTimesModHeld[1] = 1; // LH Ctrl
                            else
                                numTimesModHeld[6] = 1; // RH Ctrl
                    revolving[H] = true;
                    if (move.Y == down.sectDown.Y){
                        if (down.sectDown.keycode == G.ST){ // remove shift
                            if (down.hand == LH)
                                numTimesModHeld[0] = 0;
                            else
                                numTimesModHeld[7] = 0;
                            invalidate();
                        }
                        down.sectDown = move;
                    }
                }
            }

            /* // repeating code, TBD
            if ((next.sectMove.X == move.X) && (next.sectMove.Y == move.Y)) {
                if (!next.mod && !next.hyp) {
                    float total_elapsed = now - next.tDown;
                    if (total_elapsed > next.REPEAT_DELAY) {
                        if (!next.repeating) {
                            next.repeating = true;
                            handleKeyEvent(next, true);
                        } else {
                            float repeat_elapsed = total_elapsed - next.REPEAT_DELAY;
                            if (repeat_elapsed - next.REPEAT_RATE * (1 + next.timesRepeated) > 0) {
                                next.timesRepeated++;
                                handleKeyEvent(next, true);
                            }
                        }
                    }
                }
            }
            */

            //last.sectMove = move; // update sectMove for next callback
        }
        //invalidate();
    }

    private void motionEventGeneralActionUp(MotionEvent m) {
        int pIdx = ((m.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pId = m.getPointerId(pIdx);

        if (t.containsKey(pId) ) {
            KBTap down = t.get(pId);
            int H = down.hand;

            if (down.sectDown.keycode == G.ST){ // remove shift
                if (down.hand == LH)
                    numTimesModHeld[0] = 0;
                else
                    numTimesModHeld[7] = 0;
                invalidate();
            }

            if (!revolving[down.hand]) {
                down.tUp = System.currentTimeMillis();
                down.calcKeyPressedTimeNet();
                double xUp = m.getX(pIdx);
                double yUp = m.getY(pIdx);
                KBSector up = G.getSectNow(xUp, yUp, down);

                if ((up.X == down.sectDown.X) && (up.Y == down.sectDown.Y)) { // same key up-down
                    onTapListener.onTap(new KBEvent(keycodeNotHypecode[H]?  down.sectDown.keycode : down.sectDown.hypcode, getMod(), false, false));
                    t.remove(pId);
                }
            } else if (t.containsKey(pId) && revolving[down.hand]) {
                revolving[down.hand] = false;
                t.remove(pId);
            }
        }

        //handleKeyEvent(tap, false);

        // need to decrement numTimesModHeld/numTimesHypHeld arrays on release
        //if (down.mod) updateMod(down, false);
        //if (tap.hyp) updateHyp(tap, false); // handles the swipe from Hyper to Shift

        //t.remove(pId);
        //invalidate();
    }

    private void handleKeyEvent(KBTap tap, boolean held) {

        int code = getHypOrRegKeycode(tap.sectUp.keycode, tap.sectUp.hypcode);

        if (isHyp(tap.sectDown.keycode)) {
            updateHyp(tap, held);
            tap.hyp = true; // prevents held hyp repeatedly increasing numHypHeld
            tap.typed = true; // prevents repeating
        } else if (isMod(tap.sectDown.keycode)) {
            updateMod(tap, held);
            tap.mod = true; // prevents held mod repeatedly increasing numModHeld
            tap.typed = true; // prevents repeating
        } else if (code == G.UNRECOGNIZED) {
            onTapListener.onTap(new KBEvent(code, getMod(), true, false));
        } else if (code == G.INCREASE_ALPHA) {
            alpha = alpha + alphaStepSize;
            setAlphaComponentOfGeometry(alpha);
        } else if (code == G.DECREASE_ALPHA){
            alpha = alpha - alphaStepSize;
            setAlphaComponentOfGeometry(alpha);
        } else if (code == G.LANGUAGE_SWITCH)
            onTapListener.onTap(new KBEvent(code, getMod(), false, true));
        else
            onTapListener.onTap(new KBEvent(code, getMod(), false, false));
    }

    int getHypOrRegKeycode(int keycode, int hypcode){
        if (numTimesHypHeld > 0)
            return hypcode;
        else
            return keycode;
    }
    boolean isHyp(int keycode){
        return keycode == G.HR;
    }
    void updateHyp(KBTap tap, boolean held){
        if (tap.sectDown.keycode == G.HR)
            numTimesHypHeld = held ? numTimesHypHeld + 1: numTimesHypHeld - 1;
    }
    boolean isMod(int keycode){
        if (keycode == KeyEvent.KEYCODE_SHIFT_LEFT  || keycode == KeyEvent.KEYCODE_SHIFT_RIGHT  ||
            keycode == KeyEvent.KEYCODE_CTRL_LEFT   || keycode == KeyEvent.KEYCODE_CTRL_RIGHT   ||
            keycode == KeyEvent.KEYCODE_ALT_LEFT    || keycode == KeyEvent.KEYCODE_ALT_RIGHT    ||
            keycode == KeyEvent.KEYCODE_META_LEFT   || keycode == KeyEvent.KEYCODE_META_RIGHT   )
            return true;
        else
            return false;
    }
    private void updateMod(KBTap tap, boolean held){
        switch (tap.sectDown.keycode) {
            case KeyEvent.KEYCODE_SHIFT_LEFT:
                numTimesModHeld[0] = held ? numTimesModHeld[0] + 1: numTimesModHeld[0] - 1;
                break;
            case KeyEvent.KEYCODE_CTRL_LEFT:
                numTimesModHeld[1] = held ? numTimesModHeld[1] + 1: numTimesModHeld[1] - 1;
                break;
            case KeyEvent.KEYCODE_ALT_LEFT:
                numTimesModHeld[2] = held ? numTimesModHeld[2] + 1: numTimesModHeld[2] - 1;
                break;
            case KeyEvent.KEYCODE_META_LEFT:
                numTimesModHeld[3] = held ? numTimesModHeld[3] + 1: numTimesModHeld[3] - 1;
                break;
            case KeyEvent.KEYCODE_META_RIGHT:
                numTimesModHeld[4] = held ? numTimesModHeld[4] + 1: numTimesModHeld[4] - 1;
                break;
            case KeyEvent.KEYCODE_ALT_RIGHT:
                numTimesModHeld[5] = held ? numTimesModHeld[5] + 1: numTimesModHeld[5] - 1;
                break;
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                numTimesModHeld[6] = held ? numTimesModHeld[6] + 1: numTimesModHeld[6] - 1;
                break;
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
                numTimesModHeld[7] = held ? numTimesModHeld[7] + 1: numTimesModHeld[7] - 1;
                break;
        }
    }
    private int getMod(){
        int mod = 0;

        if (numTimesModHeld[0] > 0 || capsLockOn[LH])
            mod |= 1;
        if (numTimesModHeld[1] > 0)
            mod |= 2;
        if (numTimesModHeld[2] > 0)
            mod |= 4;
        if (numTimesModHeld[3] > 0)
            mod |= 8;
        if (numTimesModHeld[4] > 0)
            mod |= 16;
        if (numTimesModHeld[5] > 0)
            mod |= 32;
        if (numTimesModHeld[6] > 0)
            mod |= 64;
        if (numTimesModHeld[7] > 0 || capsLockOn[RH])
            mod |= 128;

        return mod;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (G != null)
            drawBitmaps(canvas, G.mCorners, G.center);
    }
    void drawBitmaps(Canvas c, DblPt[][] corners, FloatPt[] center) {
        //if (hand == RH) {
            hand = RH;

            if (keycodeNotHypecode[hand]) // opposite hand shift
                c.drawBitmap( (numTimesModHeld[0] == 1 || capsLockOn[hand]) ? G.RHkeyShiftBM : G.RHkeyBM,
                              (float) corners[hand][0].x, (float) corners[hand][0].y, null);
            else
                c.drawBitmap( (numTimesModHeld[0] == 1 || capsLockOn[hand]) ? G.RHhypShiftBM : G.RHhypBM,
                        (float) corners[hand][0].x, (float) corners[hand][0].y, null);

        //}
        //if (hand == LH) {
            hand = LH;
            c.save();
            c.rotate(90, center[hand].x, center[hand].y); // LH is rotated by 90 deg.

            if (keycodeNotHypecode[hand])
                c.drawBitmap( (numTimesModHeld [7] == 1) || capsLockOn[hand] ? G.RHkeyShiftBM : G.RHkeyBM,
                        (float) corners[hand][0].x, (float) corners[hand][0].y, null);
            else
                c.drawBitmap( (numTimesModHeld [7] == 1) || capsLockOn[hand] ? G.RHhypShiftBM : G.RHhypBM,
                        (float) corners[hand][0].x, (float) corners[hand][0].y, null);
            c.restore();
        //}
    }

    public interface OnTapListener {void onTap(KBEvent te);}
    public void setOnTapListener(OnTapListener onTapListenerArg) {
        onTapListener = onTapListenerArg;
    }
}
