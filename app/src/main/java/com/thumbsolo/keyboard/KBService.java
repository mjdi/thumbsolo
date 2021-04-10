/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thumbsolo.keyboard;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import static android.view.Gravity.NO_GRAVITY;

/**
 Example of writing an input method for a soft keyboard.  This code is
 focused on simplicity over completeness, so it should in no way be considered
 to be a complete soft keyboard implementation.  Its purpose is to provide
 a basic example for how you would get started writing an input method, to
 be fleshed out as appropriate.
 */
public class KBService extends InputMethodService implements KBView.OnTapListener
{
    static final int LH = 0;
    static final int RH = 1;
    static final int PORT = Configuration.ORIENTATION_PORTRAIT;
    static final int LAND = Configuration.ORIENTATION_LANDSCAPE;

    private InputMethodManager mInputMethodManager;
    Vibrator vibrator;
    private SharedPreferences sharedPref;
    private KBView V;
    private int hand;
    private int s;

    private int getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    private int getDisplayHeight(){ return Resources.getSystem().getDisplayMetrics().heightPixels;}
    private int getNavBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     Main initialization of the input method component.  Be sure to call
     to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        s = (int) (Math.min(getDisplayHeight(), getDisplayWidth()) / 2.0); // side length of solo

        hand = RH; // hardcoded
    }

    /**
     This is the point where you can do all of your UI initialization.  It
     is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting)
    {
        super.onStartInput(attribute, restarting);

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        //mComposing.setLength(0);

        /*
        auditoryFeedback = sp.getBoolean("auditoryfeedback_key", false);
        vibrotactileFeedback = sp.getBoolean("vibrotactilefeedback_key", false);
        hideLetters = sp.getBoolean("hideletters_key", false);
        keyStyle = sp.getString("keystyle_key", "SQUARE");
        */

    }

    /**
     Called by the framework when your view for creating input needs to
     be generated.  This will be called the first time your input method
     is displayed, and every time it needs to be re-created such as due to
     a configuration change.
     */
    @Override
    public View onCreateInputView() {
        return V;
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
    }

    /**
     This is the main point where we do our initialization of the input method
     to begin operating on an application.  At this point we have been
     bound to the client, and are now receiving all of the detailed information
     about the target of our edits.co


     This is called when the user is done editing a field.  We can use
     this to reset our state.
     */
    @Override
    public void onFinishInput()
    {
        super.onFinishInput();

        // Clear current composing text and candidates.
        //mComposing.setLength(0);

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        //mCurKeyboard = mQwertyKeyboard;
        //if (mInputView != null) {
        //    mInputView.closing();
        //}
    }

    // Marcus: I think this is used to create the "floating" effect
    @Override
    public void onConfigureWindow(Window win, boolean isFullscreen, boolean isCandidatesOnly) {
        int orient = getResources().getConfiguration().orientation;
        int dispW = getDisplayWidth();
        int dispH = getDisplayHeight();
        int statH = getStatusBarHeight();
        int navH = getNavBarHeight();

        Window W = win;
        WindowManager.LayoutParams LP = W.getAttributes();
        LP.width = dispW; //orient == PORT ? dispW : dispW - navH;
        LP.height = s;
        LP.x = 0; // always from the left
        LP.y = orient == PORT ? navH : 0; // LP is from the bottom!!!!!!!!
        W.setAttributes(LP);

        V = new KBView(this, RH, V.outInsets, s);
        V.createKBGeometry();
        V.setOnTapListener(this);
        V.W = W;
    }

    // will execute upon each finger lift
    public void onTap(KBEvent te) {
        if (te.vibrate) {
            vibrator.vibrate(20);
        } else if (te.langSwitch)
            handleLanguageSwitch();
        else {
            long now = System.currentTimeMillis();
            InputConnection ic = getCurrentInputConnection();
            ic.sendKeyEvent(
                new KeyEvent(now, now, KeyEvent.ACTION_DOWN,te.charCode, 0, te.modifiers));
            ic.sendKeyEvent(
                new KeyEvent(now, now, KeyEvent.ACTION_UP,  te.charCode, 0, te.modifiers));

            /*
            if(!secondaryThumbVActive) {
                V.hand = RH;
                V.G.hand= RH;
                V.invalidate();
                LP.x = offcenterxdist;
                W.setAttributes(LP);
                secondaryThumbVActive = true;
            } else if (secondaryThumbVActive) {
                V.hand = LH;
                V.G.hand= LH;
                V.invalidate();
                LP.x = -offcenterxdist;
                W.setAttributes(LP);
                secondaryThumbVActive = false;
            }

             */
        }
        // use setInputView(View view) to change between 2 thumbs, LH, and RH Thumbs (FUTURE WORK)
    }

    // Used to set where the app underneath (receiving text) starts from the bottom of the nav-bar/screen
    @Override
    public void onComputeInsets(Insets outInsets) {
        // by passing these to the View, we can change these programmatically
        if (V == null) {
            outInsets.contentTopInsets = s;
            outInsets.visibleTopInsets = s;
            V = new KBView(this, RH, outInsets, s);
            V.createKBGeometry();
            V.setOnTapListener(this);
        }
    }

    @Override
    public boolean onEvaluateFullscreenMode() { return false; }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        //mInputView.setSubtypeOnSpaceKey(subtype);
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }
}
