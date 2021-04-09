package com.thumbsolo.keyboard;

public class KBEvent
{
    int charCode, modifiers;
    boolean vibrate, langSwitch;

    /**
     * KBEvent - an event object passed to onTap() callback method
     *
     * @param charCode           character code for sending to a text field
     * @param modifiers          state of the modifier keys {LeftShift,LeftCtrl,LeftAlt,MetaLeft}
     * @param vibrate            tells KBService whether to vibrate or not
     * @param langSwitch         tells KBService whether to switch keyboards or not
     */
    public KBEvent(int charCode, int modifiers, boolean vibrate, boolean langSwitch)
    {
        this.charCode = charCode;
        this.modifiers = modifiers;
        this.vibrate = vibrate;
        this.langSwitch = langSwitch;
    }
}
