# Testing Suite — Interion Portable Refilling Station

This folder contains all testing artefacts for the project.

## Structure

```
testing/
├── PENTEST_REPORT.md             # Full penetration test findings
├── package.json                  # Test dependencies (Jest + ts-jest)
├── jest.config.js                # Jest configuration
├── tsconfig.json                 # TypeScript config for tests
│
├── __mocks__/                    # Manual mocks for native modules
│   ├── expo-secure-store.ts
│   ├── expo-constants.ts
│   ├── expo-crypto.ts
│   └── @react-native-async-storage/
│       └── async-storage.ts
│
├── unit/                         # Pure unit tests (no mocked stores/API)
│   ├── validators.test.ts        # All validation functions
│   ├── formatters.test.ts        # All formatting functions
│   ├── bankAccountStore.test.ts  # Bank account CRUD + masking
│   └── transactionStore.test.ts  # Transaction persistence
│
├── stores/                       # Zustand store integration tests
│   ├── useAuthStore.test.ts      # Auth state, login/logout, balance ops
│   ├── usePaymentStore.test.ts   # Payment initiation, polling, cancel
│   └── useFuelingStore.test.ts   # Station loading, dispensing lifecycle
│
├── api/                          # API wrapper tests
│   ├── auth.test.ts              # Auth API functions
│   └── client.test.ts            # Axios interceptors, token injection
│
├── network/                      # Network layer tests
│   └── DigestAuth.test.ts        # Digest Authentication header construction
│
└── security/                     # Security regression tests (maps to pentest findings)
    └── security.test.ts          # One test per pentest finding ID
```

## Quick Start

```powershell
# From the testing/ directory:
cd testing
npm install
npm test
```

## Run Specific Suites

```powershell
npm run test:unit       # validators, formatters, storage utils
npm run test:stores     # Zustand store tests
npm run test:api        # API wrapper tests
npm run test:network    # Network / protocol tests
npm run test:security   # Security regression tests
npm run test:coverage   # All tests + coverage report
npm run test:watch      # Watch mode for development
```

## Coverage Report

After running `npm run test:coverage`, open `coverage/lcov-report/index.html` in a browser.

## Security Tests

The `security/security.test.ts` file maps directly to findings in `PENTEST_REPORT.md`.  
Tests tagged **UNMITIGATED** document vulnerabilities that have not yet been fixed.  
Once a fix is applied, update the corresponding test assertion.

| Test | Finding | Status |
|------|---------|--------|
| `[FINDING C2]` Password reset token not in response | C2 | Unmitigated |
| `[FINDING C3]` JWT_SECRET env check | C3 | Requires env var |
| `[FINDING H2]` OTP input validation | H2 | Mitigated (frontend) |
| `[FINDING H3]` Bank accounts in AsyncStorage | H3 | Unmitigated |
| `[FINDING M3]` No upper bound on volume/amount | M3 | Unmitigated (server must fix) |
| `[FINDING L5]` Password length mismatch | L5 | Partial (tighten backend) |

## Adding New Tests

1. Place tests in the appropriate subfolder.
2. Name files `<module>.test.ts`.
3. For new security checks, add to `security/security.test.ts` with a `[FINDING Xx]` tag.
4. Update this README and `PENTEST_REPORT.md` as findings are mitigated.
