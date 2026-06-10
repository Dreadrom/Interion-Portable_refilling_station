# 🎯 QUICK UPLOAD CHECKLIST - Google Play Console

**Use this checklist as you fill out the Google Play Console form.**

---

## ✅ FILES TO UPLOAD

### App Bundle:
- [ ] **File:** `app-release-v2.aab` (68.6 MB)
- [ ] **Version Code:** 2
- [ ] **Location:** Google Play Console → Release → Create new release

### Graphics:
- [ ] **App Icon:** `icon-512x512.png` (512x512 px) ✅ Ready
- [ ] **Feature Graphic:** Generate from `feature-graphic-FINAL.html` (1024x500 px)
- [ ] **Screenshots:** Minimum 2 phone screenshots (see GENERATE_GRAPHICS_GUIDE.md)

---

## 📝 TEXT CONTENT (Copy from files)

### App Name:
- [ ] Copy from: `01-app-name.txt`
- [ ] Paste into: "App name" field (30 char limit)

### Short Description:
- [ ] Copy from: `02-short-description.txt`
- [ ] Paste into: "Short description" field (80 char limit)

### Full Description:
- [ ] Copy from: `03-full-description.txt`
- [ ] Paste into: "Full description" field (4000 char limit)

### App Details:
- [ ] Copy from: `04-app-details.txt` for:
  - Category: Business
  - Tags: adblue, refueling, fleet management, commercial vehicles, diesel
   - Email: info@bluediesel.com.my
  - Website: https://cp1.interion.com.sg

---

## 🔗 URLS (Upload to Netlify first!)

**Action Required:**
1. Go to: https://app.netlify.com/drop
2. Drag these files:
   - `privacy-policy.html`
   - `delete-account.html`
3. Get your site URL (example: https://bluediesel-12345.netlify.app)

**Then paste URLs:**
- [ ] **Privacy Policy:** https://your-site.netlify.app/privacy-policy.html
- [ ] **Delete Account:** https://your-site.netlify.app/delete-account.html

---

## 📋 CONTENT RATING

When asked these questions, answer:

- [ ] Violence: **No**
- [ ] Sexual Content: **No**
- [ ] Profanity: **No**
- [ ] Controlled Substances: **No**
- [ ] Gambling: **No**
- [ ] User-Generated Content: **No**
- [ ] User Interaction: **Yes** (users can create accounts)
- [ ] Shares Location: **Yes** (to show nearby stations)
- [ ] Digital Purchases: **Yes** (wallet top-ups)

**Expected Rating:** PEGI 3 / Everyone

---

## 💰 PRICING & DISTRIBUTION

- [ ] **Pricing:** Free
- [ ] **In-app purchases:** Yes (wallet top-ups)
- [ ] **Contains ads:** No
- [ ] **Countries:** Select all or specific countries (Malaysia recommended for start)

---

## 👥 TESTERS (For Alpha/Internal Testing)

- [ ] **Alpha Testers:** Upload `alpha-testers.csv` (9 emails)
- [ ] **Beta Testers:** Upload `beta-testers.csv` (3 emails) when ready

---

## 📄 RELEASE NOTES

- [ ] Copy from: `release-notes.txt`
- [ ] Paste into: "Release notes" field in Release section

---

## 🚀 SUBMISSION ORDER

### Step 1: App Content
1. Go to: **App content** (left sidebar)
2. Fill out all policies:
   - Privacy Policy ✓
   - Data safety ✓
   - Government apps (No)
   - Financial features (No for now)
   - Health & fitness (No)
   - COVID-19 contact tracing (No)
   - Data deletion (Yes - use delete-account URL)

### Step 2: Store Settings
1. Go to: **Store settings** → **Store listing**
2. Upload all graphics
3. Paste all text content
4. Fill contact details

### Step 3: Main Store Listing
1. Go to: **Store presence** → **Main store listing**
2. Select app category
3. Add tags
4. Upload graphics
5. Add descriptions

### Step 4: Content Rating
1. Go to: **Store presence** → **Content rating**
2. Start questionnaire
3. Answer all questions (see answers above)
4. Complete rating

### Step 5: Pricing & Distribution
1. Go to: **Production** → **Countries/regions**
2. Select countries
3. Set pricing (Free)
4. Confirm distribution

### Step 6: Create Release
1. Go to: **Testing** → **Internal testing** (or Alpha)
2. Click **Create new release**
3. Upload `app-release-v2.aab`
4. Add release notes
5. Add testers
6. **Review** → **Start rollout to internal testing**

---

## ✨ FINAL CHECK

Before clicking "Submit for review":

- [ ] App name, short description, full description all filled
- [ ] App icon uploaded
- [ ] Feature graphic uploaded
- [ ] At least 2 screenshots uploaded
- [ ] Privacy policy URL added
- [ ] Delete account URL added
- [ ] Content rating completed
- [ ] App category selected
- [ ] Contact email added
- [ ] AAB file uploaded
- [ ] Release notes added
- [ ] Testers added (for internal/alpha testing)

---

## 🎉 YOU'RE READY!

Once all checkboxes are ticked, click:
- "Save" (if draft)
- "Review release" (to check everything)
- "Start rollout to internal testing" (to publish to testers)

**Your testers will receive an email within a few hours!**

---

**Files Location:** `c:\Users\songj\Interion\Interion-Portable_refilling_station\portable-refill-app\playstore\`

**Questions?** Check the other guide files in the playstore folder for detailed instructions.
