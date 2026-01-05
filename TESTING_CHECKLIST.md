# MarelikayBeats + MarelikayFamily Testing Checklist

## Overview

Both apps must be installed on the **same device** (the child's device).

```
┌─────────────────────────────────────────────────────────┐
│                   CHILD'S DEVICE                        │
│                                                         │
│  MarelikayBeats ◄──── AIDL IPC ────► MarelikayFamily    │
│  (Kid's App)                         (Parent Config)    │
└─────────────────────────────────────────────────────────┘
```

---

## Pre-Test Setup

### Build APKs

```bash
# Build MarelikayBeats
cd "C:\Users\Marielikay\AndroidStudioProjects\KidzTubePlayer"
./gradlew.bat assembleDebug

# Build MarelikayFamily
cd "C:\Users\Marielikay\AndroidStudioProjects\Parental_Control"
./gradlew.bat assembleDebug
```

### Install on Device/Emulator

```bash
# Connect device via USB or start emulator
adb devices

# Install MarelikayFamily first
adb install -r "C:\Users\Marielikay\AndroidStudioProjects\Parental_Control\app\build\outputs\apk\debug\app-debug.apk"

# Install MarelikayBeats
adb install -r "C:\Users\Marielikay\AndroidStudioProjects\KidzTubePlayer\app\build\outputs\apk\debug\app-debug.apk"
```

### Monitor Logs

```bash
# In a separate terminal - watch IPC communication
adb logcat | grep -E "(ServiceConnManager|ParentalBridge|ParentalControlService|CallbackHandler)"
```

---

## Test Scenarios

### 1. Standalone Mode (No Companion)

| # | Test | Steps | Expected Result | Pass |
|---|------|-------|-----------------|------|
| 1.1 | Launch without companion | Uninstall MarelikayFamily, open MarelikayBeats | App works, shows onboarding | ☐ |
| 1.2 | Skip parental controls | Complete onboarding, tap "Skip for Now" | Main app loads, unrestricted | ☐ |
| 1.3 | Play video | Search and play any video | Video plays without restrictions | ☐ |
| 1.4 | Play music | Navigate to music, play track | Music plays without restrictions | ☐ |
| 1.5 | Settings shows companion prompt | Go to Settings > Parental Controls | Shows "Install MarelikayFamily" | ☐ |

### 2. Companion App Setup

| # | Test | Steps | Expected Result | Pass |
|---|------|-------|-----------------|------|
| 2.1 | Install companion | Install MarelikayFamily APK | App installs successfully | ☐ |
| 2.2 | Open companion | Launch MarelikayFamily | Main setup screen appears | ☐ |
| 2.3 | Create PIN | Set a 4-digit PIN | PIN saved successfully | ☐ |
| 2.4 | Service binding | Check logcat | "Service connected" message | ☐ |
| 2.5 | MarelikayBeats detects companion | Open MarelikayBeats Settings | Shows "Connected" status | ☐ |

### 3. Screen Time Limits

| # | Test | Steps | Expected Result | Pass |
|---|------|-------|-----------------|------|
| 3.1 | Set time limit | In MarelikayFamily: Set 5 min daily limit | Setting saved | ☐ |
| 3.2 | Time tracking | Play video in MarelikayBeats for 3 min | Time tracked (check logs) | ☐ |
| 3.3 | Warning at 1 min | Continue watching until 1 min left | Warning dialog appears | ☐ |
| 3.4 | Block at limit | Watch until limit reached | Block screen appears | ☐ |
| 3.5 | Parent unlock | Tap "Parent Access" on block screen | Opens MarelikayFamily | ☐ |
| 3.6 | Extend time | Enter PIN, extend 30 min | Block lifted, can watch | ☐ |

### 4. Bedtime Settings

| # | Test | Steps | Expected Result | Pass |
|---|------|-------|-----------------|------|
| 4.1 | Set bedtime | In MarelikayFamily: Set bedtime to now | Setting saved | ☐ |
| 4.2 | Bedtime block | Open MarelikayBeats | Bedtime block screen shows | ☐ |
| 4.3 | Block screen UI | Verify bedtime screen | Moon icon, "Time for Bed" | ☐ |
| 4.4 | Parent override | Tap "Parent Access", enter PIN | Bedtime overridden | ☐ |
| 4.5 | Disable bedtime | Turn off bedtime in MarelikayFamily | App unrestricted | ☐ |

### 5. Content Filtering

| # | Test | Steps | Expected Result | Pass |
|---|------|-------|-----------------|------|
| 5.1 | Set age level | In MarelikayFamily: Set to "Under 5" | Setting saved | ☐ |
| 5.2 | Safe search | Search for common kids content | Results appear | ☐ |
| 5.3 | Blocked content | Try to play inappropriate content | Content blocked dialog | ☐ |
| 5.4 | Change age level | Set to "Under 13" | More content available | ☐ |
| 5.5 | Music filtering | Search for music | Explicit music filtered | ☐ |

### 6. Service Disconnection & Recovery

| # | Test | Steps | Expected Result | Pass |
|---|------|-------|-----------------|------|
| 6.1 | Force stop companion | Force stop MarelikayFamily | MarelikayBeats detects disconnect | ☐ |
| 6.2 | Fallback mode | Try to play video | Video plays (unrestricted) | ☐ |
| 6.3 | Auto-reconnect | Reopen MarelikayFamily | Service reconnects | ☐ |
| 6.4 | Restrictions restored | Check MarelikayBeats settings | Shows "Connected" again | ☐ |

### 7. Edge Cases

| # | Test | Steps | Expected Result | Pass |
|---|------|-------|-----------------|------|
| 7.1 | PIN retry limit | Enter wrong PIN 5 times | Lockout/error message | ☐ |
| 7.2 | No internet | Disable wifi/data, use app | Offline content works | ☐ |
| 7.3 | App restart | Force close MarelikayBeats, reopen | Restrictions still active | ☐ |
| 7.4 | Device restart | Reboot device | Both apps restore state | ☐ |

---

## Log Messages to Watch For

### Successful Connection
```
ServiceConnManager: Service connected: ComponentInfo{com.marielikay.parentalcontrol/...}
ParentalBridge: Companion app connected, restrictions active
```

### Content Check
```
ParentalBridge: Checking content: [videoId] - [title]
ParentalControlService: canPlayContent: ALLOW/BLOCK - [reason]
```

### Screen Time
```
ParentalControlService: onContentStarted: [videoId]
ParentalControlService: Screen time: 3/5 minutes used
CallbackHandler: Screen time warning: 1 minutes remaining
```

### Disconnection
```
ServiceConnManager: Service disconnected
ParentalBridge: Companion disconnected, falling back to unrestricted
```

---

## Troubleshooting

### Service Won't Connect
1. Check both apps have same signing key (debug builds)
2. Verify MarelikayFamily is running
3. Check logcat for permission errors

### Block Screen Not Showing
1. Verify PIN is set in MarelikayFamily
2. Check parental controls are enabled
3. Look for errors in ParentalBridge logs

### Content Not Filtering
1. Check age level setting in companion
2. Verify service connection is active
3. Check canPlayContent logs for verdicts

---

## APK Locations

After building:
- **MarelikayBeats**: `KidzTubePlayer/app/build/outputs/apk/debug/app-debug.apk`
- **MarelikayFamily**: `Parental_Control/app/build/outputs/apk/debug/app-debug.apk`
