# 👋 Welcome JQ - Getting Started Guide

**Project**: Portable Petrol Station Mobile App  
**Date**: January 24, 2026  
**Your Mission**: Build the React Native mobile app for our cloud-connected petrol station system

---

## 🎯 What You're Building

A mobile app (iOS + Android) that allows users to:
- Find nearby petrol stations
- Pay via QR code (Fiuu payment gateway)
- Start fueling remotely
- Monitor fueling progress in real-time
- View transaction history

---

## ✅ What's Already Done (Foundation Complete!)

The senior architect has set up everything you need to start coding:

### 1. Complete Project Architecture ✅
- Full system design documented
- AWS backend integration planned
- Payment flow (Fiuu) designed
- All technical decisions made

### 2. TypeScript Type System ✅
- All domain models defined (User, Station, Payment, Transaction, etc.)
- API request/response types ready
- 50+ interfaces ready to use

### 3. API Client Layer ✅
- Axios configured with authentication
- All API methods implemented (login, stations, payments, fueling)
- Error handling built-in
- Token management automated

### 4. Utility Functions ✅
- Currency, date, volume formatters
- Form validators
- Constants and configurations

### 5. Documentation ✅
- Architecture diagrams
- Setup guides
- Code examples
- Task tracking (TODO.md)

**Bottom line**: You can focus 100% on building screens and UI! All the "hard stuff" is done. 🎉

---

## 🚀 Quick Start (3 Steps)

### Step 1: Clone and Navigate
```powershell
# Clone the repository
git clone <repository-url>
cd Interion-Portable_refilling_station/New_App/portable-refill-app
```

### Step 2: Install Dependencies
```powershell
# Run the install script (easiest)
.\install.ps1

# OR install manually
npm install axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns @types/node
```

### Step 3: Configure Environment
```powershell
# Create .env file from template
copy .env.example .env

# Edit .env with the backend API URL (ask your supervisor)
notepad .env
```

### Step 4: Start Development
```powershell
npm start
```

Then press:
- `i` for iOS simulator
- `a` for Android emulator
- Or scan QR code with Expo Go app on your phone

---

## 📚 Essential Documents (Read These First!)

**Start here in this order:**

1. **[README.md](../../README.md)** (5 min read)
   - Project overview
   - What we're building

2. **[TODO.md](../../TODO.md)** (10 min read)
   - **THIS IS YOUR ROADMAP!** 🗺️
   - Shows what's done and what you need to build
   - Phase-by-phase checklist
   - Start with Phase 1: Authentication

3. **[QUICKSTART.md](QUICKSTART.md)** (5 min read)
   - Quick reference
   - Code examples
   - Common commands

4. **[ARCHITECTURE.md](../../ARCHITECTURE.md)** (Optional - deep dive)
   - Full system architecture
   - Only read if you want to understand the big picture

---

## 🎯 Your First Task: Phase 1 - Authentication

### Goal
Build the login, registration, and forgot password screens with full authentication flow.

### What You'll Create
1. **Auth Store** (`src/stores/useAuthStore.ts`)
   - Manages user login state
   - Handles token storage
   - Example code provided in QUICKSTART.md

2. **Login Screen** (`app/(auth)/login.tsx`)
   - Email and password inputs
   - Form validation
   - Error messages
   - "Forgot Password?" link

3. **Registration Screen** (`app/(auth)/register.tsx`)
   - Name, email, phone, password inputs
   - Validation
   - Success → auto-login

4. **Forgot Password Screen** (`app/(auth)/forgot-password.tsx`)
   - Email input
   - Send reset link

5. **Protected Navigation**
   - Logged in → Main app
   - Not logged in → Auth screens

### Estimated Time: 2-3 hours

### Checklist
Open [TODO.md](../../TODO.md) and find "Phase 1" - that's your checklist! Check off items as you complete them.

---

## 💡 How to Use What's Already Built

### Example 1: Login a User
```typescript
import { authApi } from '@/src/api';

const handleLogin = async () => {
  try {
    const { user, token } = await authApi.login({
      email: 'user@example.com',
      password: 'password123',
    });
    
    // Token is automatically stored!
    console.log('Logged in:', user.name);
  } catch (error) {
    console.error('Login failed:', error.message);
  }
};
```

### Example 2: Format Currency
```typescript
import { formatCurrency } from '@/src/utils';

const price = formatCurrency(50.00, 'MYR'); // "MYR 50.00"
```

### Example 3: Validate Form
```typescript
import { validateLoginForm } from '@/src/utils';

const result = validateLoginForm({
  email: 'test@example.com',
  password: 'password123',
});

if (!result.isValid) {
  console.log('Errors:', result.errors);
}
```

**See [QUICKSTART.md](QUICKSTART.md) for more examples!**

---

## 📋 Development Workflow

### Daily Workflow
1. **Check TODO.md** - See what's next
2. **Create/edit files** - Build the feature
3. **Test it** - Run on iOS/Android
4. **Update TODO.md** - Check off completed items
5. **Update PROMPTS.md** - Document what you did
6. **Commit & push** - Save your work

### When You Complete a Task
```markdown
# In TODO.md, change:
- [ ] Build login screen

# To:
- [x] Build login screen
```

### When You Finish a Phase
Add an entry to PROMPTS.md:
```markdown
## Version 1.1 - Authentication Complete
**Date**: January XX, 2026
**Completed By**: [Your Name]

**Tasks Completed**:
- [x] Auth store created
- [x] Login screen working
- [x] Registration screen working
... etc
```

---

## 🆘 Getting Help

### When You're Stuck

1. **Check Documentation**
   - README.md - Overview
   - QUICKSTART.md - Code examples
   - TODO.md - What to build
   - SETUP.md - Setup issues

2. **Check Existing Code**
   - Look at `src/api/` for API examples
   - Look at `src/types/` for data structures
   - Look at `src/utils/` for helper functions

3. **Google It**
   - "Expo Router authentication"
   - "React Native login screen"
   - "Zustand store example"

4. **Ask Your Supervisor**
   - Slack/email your questions
   - Include error messages
   - Say what you've tried

### Common Issues

**Issue**: TypeScript errors about paths
```json
// Check tsconfig.json has:
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./*"]
    }
  }
}
```

**Issue**: Metro bundler not starting
```powershell
# Clear cache
npm start -- --clear
```

**Issue**: Module not found
```powershell
# Reinstall
rm -rf node_modules
npm install
```

---

## 🎓 Learning Resources

### React Native & Expo
- [Expo Docs](https://docs.expo.dev/) - Official Expo documentation
- [React Native Docs](https://reactnative.dev/) - React Native guide

### State Management
- [Zustand](https://github.com/pmndrs/zustand) - Simple state management
- [React Query](https://tanstack.com/query) - Server state & caching

### UI Components
- [React Native Elements](https://reactnativeelements.com/)
- [NativeBase](https://nativebase.io/)
- Or build your own!

---

## ✨ Pro Tips

### 1. Start Small
Don't try to build everything at once. Start with login screen, get it working, then move to the next.

### 2. Use TypeScript
The types are already defined. Use them! Your editor will autocomplete and catch errors.

### 3. Test Often
Don't write 100 lines then test. Write 10 lines, test, repeat.

### 4. Read Error Messages
React Native errors are actually helpful. Read them carefully.

### 5. Use the Debugger
Expo has great debugging tools. Use `console.log()` liberally.

### 6. Ask Questions
If you're stuck for >30 minutes, ask for help. Don't waste time.

---

## 📊 Progress Tracking

### Phase 1: Authentication (YOU ARE HERE 👈)
- [ ] Auth store
- [ ] Login screen
- [ ] Registration screen
- [ ] Forgot password
- [ ] Protected routes
**Target**: End of Week 1

### Phase 2: Stations
- [ ] Station list
- [ ] Station detail
- [ ] Tank levels
**Target**: End of Week 2

### Phase 3: Payment
- [ ] Payment QR
- [ ] Status polling
**Target**: Week 3

### Phase 4: Fueling
- [ ] Progress screen
- [ ] Emergency stop
**Target**: Week 3-4

### Phase 5: History & Profile
- [ ] Transaction history
- [ ] User profile
**Target**: Week 4

### Phase 6: Polish
- [ ] Error handling
- [ ] Loading states
- [ ] Animations
**Target**: Week 5

---

## 🎯 Success Criteria

### You're doing great if:
- ✅ App runs without crashes
- ✅ Code follows existing patterns
- ✅ TypeScript has no errors
- ✅ Features work on both iOS and Android
- ✅ You're checking off items in TODO.md

### You need help if:
- ❌ Stuck on same issue for >1 hour
- ❌ Not sure what to build next
- ❌ App crashes constantly
- ❌ TypeScript errors everywhere

---

## 🚀 Let's Build This!

You have everything you need:
- ✅ Complete architecture
- ✅ All API methods ready
- ✅ All types defined
- ✅ Utilities and helpers
- ✅ Clear roadmap (TODO.md)
- ✅ Code examples

**Your job**: Build beautiful screens and connect them to the existing API layer.

**You've got this!** 💪

---

## 📞 Quick Reference

| I need to... | Look here |
|--------------|-----------|
| See what to build | [TODO.md](../../TODO.md) |
| See code examples | [QUICKSTART.md](QUICKSTART.md) |
| Understand architecture | [ARCHITECTURE.md](../../ARCHITECTURE.md) |
| Setup the project | [SETUP.md](SETUP.md) |
| Check progress | [TODO.md](../../TODO.md) |
| Document work | [PROMPTS.md](../../PROMPTS.md) |

---

## 🎉 Final Words

This is a real production app that will be used by actual users! You're building something meaningful.

The foundation is solid. The architecture is production-ready. Now we need your skills to bring it to life with great UI/UX.

Take your time, ask questions, test thoroughly, and most importantly - have fun building this!

**Welcome to the team! Let's ship this! 🚀**

---

**Questions?** Slack/email your supervisor  
**Stuck?** Check the docs first, then ask  
**Done with Phase 1?** High-five yourself and move to Phase 2! 🎉
