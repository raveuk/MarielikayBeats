// IParentalControlCallback.aidl
package com.zimbabeats.family.ipc;

import com.zimbabeats.family.ipc.RestrictionState;

/**
 * Callback interface for receiving parental control events from the companion app.
 * Implemented by ZimbaBeats to receive async notifications.
 */
interface IParentalControlCallback {
    /**
     * Screen time warning approaching limit.
     * @param remainingMinutes Minutes remaining (typically 5 or 1)
     */
    void onScreenTimeWarning(int remainingMinutes);

    /**
     * Screen time limit reached - playback should be blocked.
     * @param usedMinutes Total minutes used today
     * @param limitMinutes Daily limit in minutes
     */
    void onScreenTimeLimitReached(int usedMinutes, int limitMinutes);

    /**
     * Screen time unlocked by parent.
     * @param additionalMinutes Extra time granted (0 = unlimited for today)
     */
    void onScreenTimeUnlocked(int additionalMinutes);

    /**
     * Bedtime has started - playback should be blocked.
     * @param endTime When bedtime ends (HH:mm format)
     */
    void onBedtimeStarted(String endTime);

    /**
     * Bedtime has ended - playback allowed.
     */
    void onBedtimeEnded();

    /**
     * Bedtime temporarily overridden by parent.
     * @param overrideMinutes Duration of override
     */
    void onBedtimeOverridden(int overrideMinutes);

    /**
     * Content was blocked by filtering rules.
     * @param contentId The blocked content ID
     * @param reason Human-readable reason
     * @param blockType 1=CONTENT_FILTER, 2=CHANNEL_BLOCKED, 3=KEYWORD_BLOCKED
     */
    void onContentBlocked(String contentId, String reason, int blockType);

    /**
     * Parental control settings changed.
     * @param newState Updated restriction state
     */
    void onSettingsChanged(in RestrictionState newState);

    /**
     * Parental control enabled/disabled.
     * @param enabled Whether controls are now enabled
     */
    void onParentalControlToggled(boolean enabled);

    /**
     * Result of requestParentUnlock() call.
     * @param success Whether unlock was successful
     * @param unlockType Type of unlock performed
     * @param additionalMinutes Extra time granted (for screen time unlocks)
     */
    void onUnlockResult(boolean success, int unlockType, int additionalMinutes);
}
