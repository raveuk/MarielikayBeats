// IParentalControlService.aidl
package com.zimbabeats.family.ipc;

import com.zimbabeats.family.ipc.PlaybackVerdict;
import com.zimbabeats.family.ipc.UnlockResult;
import com.zimbabeats.family.ipc.RestrictionState;
import com.zimbabeats.family.ipc.ContentInfo;
import com.zimbabeats.family.ipc.IParentalControlCallback;

/**
 * Main AIDL interface for the Parental Control Service.
 * ZimbaBeats binds to this service in the companion app.
 */
interface IParentalControlService {
    // ========== CONTENT GATING ==========

    /**
     * Check if content can be played. Called BEFORE starting playback.
     * @param content Content metadata
     * @return Verdict with allow/block decision and reason
     */
    PlaybackVerdict canPlayContent(in ContentInfo content);

    /**
     * Notify that content playback has started. Used for screen time tracking.
     * @param contentId Unique content identifier
     * @param title Content title for logging
     */
    void onContentStarted(String contentId, String title);

    /**
     * Notify that content playback has stopped. Used for screen time tracking.
     * @param contentId Unique content identifier
     * @param watchedDurationSeconds How long the content was watched
     */
    void onContentStopped(String contentId, long watchedDurationSeconds);

    // ========== RESTRICTION STATE ==========

    /**
     * Get current restriction state (screen time, bedtime, age level).
     * @return Current restriction configuration and status
     */
    RestrictionState getActiveRestrictions();

    /**
     * Check if search is allowed with current restrictions.
     * @param query The search query
     * @return true if search is allowed
     */
    boolean isSearchAllowed(String query);

    /**
     * Check if downloads require PIN verification.
     * @return true if PIN is required for downloads
     */
    boolean isDownloadPinRequired();

    // ========== PARENT UNLOCK ==========

    /**
     * Request parent unlock. Opens companion app's PIN entry screen.
     * Result delivered via IParentalControlCallback.onUnlockResult()
     * @param unlockType 0=ACCESS_SETTINGS, 1=EXTEND_SCREEN_TIME, 2=BEDTIME_OVERRIDE
     */
    void requestParentUnlock(int unlockType);

    /**
     * Verify PIN directly (for in-app PIN dialogs).
     * @param pin The PIN to verify
     * @return Result with success/failure and unlock details
     */
    UnlockResult verifyPin(String pin);

    // ========== CALLBACKS ==========

    /**
     * Register callback for async events (warnings, blocks, settings changes).
     * @param callback The callback implementation
     */
    void registerCallback(IParentalControlCallback callback);

    /**
     * Unregister callback.
     * @param callback The callback to remove
     */
    void unregisterCallback(IParentalControlCallback callback);

    // ========== LIFECYCLE ==========

    /**
     * Called when main app becomes active. Starts screen time session.
     */
    void onAppActive();

    /**
     * Called when main app goes to background. Ends screen time session.
     */
    void onAppBackground();

    /**
     * Periodic tick for time-based checks. Call every minute.
     */
    void tick();

    // ========== VERSION ==========

    /**
     * Get IPC protocol version for compatibility checking.
     * @return Protocol version number
     */
    int getProtocolVersion();
}
