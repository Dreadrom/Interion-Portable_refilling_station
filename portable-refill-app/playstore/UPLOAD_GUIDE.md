# Google Play Store — Upload Guide for BlueDiesel Refill Kiosk

## The Upload File: AAB, not APK

Google Play requires an **AAB (Android App Bundle)** for all new app submissions.
APK uploads are rejected for new apps as of August 2021.

The `.aab` file is what you drag into the Play Console upload box.

---

## Step 1 — One-time Setup

```powershell
# Install EAS CLI globally
npm install -g eas-cli

# Log in to your Expo account
eas login

# Link this project to your Expo account (run from portable-refill-app/)
eas init
```

---

## Step 2 — Build the AAB

```powershell
# Navigate to the app folder
cd portable-refill-app

# Build the production AAB (takes ~10-15 min on EAS servers)
eas build --platform android --profile production
```

EAS will:
1. Ask you to generate or upload a **keystore** (choose "Generate new keystore" on first build — EAS stores it securely)
2. Upload your code and build remotely
3. Give you a download link for the `.aab` file

Download the `.aab` from the link and place it in this `playstore/` folder.

---

## Step 3 — Upload to Play Console

1. Go to [play.google.com/console](https://play.google.com/console)
2. Select your app → **Release** → **Production** (or Internal Testing first)
3. Click **Create new release**
4. Upload the `.aab` file
5. Fill in release notes
6. Save and review → Rollout

---

## Required Assets Checklist

### Mandatory for Play Console

| Asset | Size | Format | Status |
|---|---|---|---|
| **App Icon (Hi-res)** | 512 × 512 px | PNG, no alpha | ☐ |
| **Feature Graphic** | 1024 × 500 px | PNG or JPG | ☐ |
| **Phone Screenshots** | Min 2, max 8 | PNG or JPG, 16:9 or 9:16 | ☐ |
| **Privacy Policy URL** | — | Live URL | ☐ |

### Recommended

| Asset | Size | Notes |
|---|---|---|
| **Tablet Screenshots** | 7" and/or 10" | Good for discoverability |
| **Promo Video** | YouTube URL | Optional but helps conversion |

### Screenshot Dimensions

| Device | Minimum | Maximum |
|---|---|---|
| Phone | 320 px on short side | 3840 px on long side |
| Tablet 7" | Same | Same |
| Tablet 10" | Same | Same |

Recommended phone screenshot size: **1080 × 1920 px** (portrait)

---

## Store Listing Text (fill in before submitting)

### App Name (max 30 chars)
```
BlueDiesel Refill Kiosk
```

### Short Description (max 80 chars)
```
Contactless portable refilling — pay, pump, and go with BlueDiesel.
```

### Full Description (max 4000 chars)
```
BlueDiesel Refill Kiosk is a smart portable fuel and AdBlue refilling
solution. Connect to BlueDiesel stations, pre-authorise your wallet, and
dispense exactly what you need — no queues, no cash.

Features:
• Scan QR code to connect to any BlueDiesel station
• Pre-authorise and set a fill limit before pumping
• Live dispensing monitor with real-time volume and cost
• Automatic stop at your set limit or tank full
• Digital receipt with full pump timestamps and SST breakdown
• Transaction history with downloadable PDF receipts
• Secure BlueDiesel Digital Wallet — top up and cash out any time
• Works with AdBlue, petrol, and diesel nozzles
```

---

## Content Rating

When setting content rating in Play Console, answer:
- Violence: No
- Sexual content: No
- Language: No
- Controlled substances: No (fuel dispensing is not a controlled substance category)

**Expected rating: Everyone (E)**

---

## App Category

- Category: **Tools** or **Business**
- Tags: fuel, adblue, refill, fleet management, kiosk

---

## Files in This Folder

```
playstore/
├── UPLOAD_GUIDE.md           ← This file
├── service-account-key.json  ← (Add your Google Play service account key here for eas submit)
└── [your-app-release.aab]    ← (Place downloaded AAB here after eas build)
```

### service-account-key.json
To use `eas submit` (automatic upload), create a service account in Google Play Console:
1. Play Console → Setup → API access → Link to Google Cloud project
2. Create a service account with **Release Manager** role
3. Download the JSON key → save as `playstore/service-account-key.json`
4. Run: `eas submit --platform android --profile production`

> **Keep `service-account-key.json` out of git.** It is already listed in `.gitignore`.

---

## Version Management

When releasing an update:
1. Bump `"version"` in `app.json` (e.g. `"1.0.1"`)
2. Increment `"versionCode"` in `app.json` (e.g. `2`) — must always increase
3. Run `eas build --platform android --profile production` again
4. Upload the new AAB to Play Console

---

## Quick Reference Commands

```powershell
# Production AAB build
eas build --platform android --profile production

# Internal testing APK (for testers without Play Store)
eas build --platform android --profile preview

# Submit directly to Play Console (needs service-account-key.json)
eas submit --platform android --profile production

# Check build status
eas build:list
```
