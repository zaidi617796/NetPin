# Bijli Bill Calculator v1

Urdu + English bijli bill estimator — zero permissions, works offline.

## Data (verified 18 Oct 2026)
Federal notification **S.R.O. 279(I)/2026, effective 12 Feb 2026** — uniform across all
DISCOs + K-Electric. 3 independent sources agree (pakera.pk, lescobillcheck.pk, pakhisaab.com).
Older sites showing pre-Feb-2026 rates are outdated — this app uses the current notification.

## Calculation
- **Incremental slabs** (each chunk billed at its own rate).
- **Fixed charge** = slab's Rs/kW × sanctioned load (default 1 kW).
- Shows base estimate only; FPA, quarterly adjustment, GST, TV fee (Rs 35) and
  electricity duty are clearly labelled as extra on the real bill.
- **Disclaimer (policy safety):** not a government app — verify with your bill.

## Files
- `TariffData.java` — editable slab table (next notification par sirf yahan update)
- `MainActivity.java` — programmatic UI (no XML layouts)
- `values/strings.xml` + `values-ur/strings.xml` — English + Urdu

## Build
GitHub Actions (`build.yml`, debug-only — repo me keystore secrets nahi, is liye
release job nahi). **Note:** workflow file GitHub App ki permission se push nahi hota —
`.github/workflows/build.yml` manually add karna hota hai (Settings Guide wala step).
