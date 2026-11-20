package com.devilgamehub.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View
import com.devilgamehub.data.model.UserPreferences

object AppAudioManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 50)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playTapSound(view: View?, prefs: UserPreferences) {
        if (!prefs.soundEnabled) return

        try {
            if (view != null) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            } else {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 20)
            }
        } catch (_: Exception) {}
    }

    fun performHaptic(view: View?, prefs: UserPreferences) {
        if (!prefs.hapticFeedbackEnabled || view == null) return

        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (_: Exception) {}
    }
}
