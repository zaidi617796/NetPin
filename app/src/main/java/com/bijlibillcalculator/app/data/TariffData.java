package com.bijlibillcalculator.app.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Bijli tariff slabs — federal notification S.R.O. 279(I)/2026, effective 12 Feb 2026.
 * Uniform across LESCO, IESCO, FESCO, GEPCO, MEPCO, PESCO, HESCO, SEPCO, QESCO, TESCO, K-Electric.
 * Research date: 18 Oct 2026 (3 independent sources agree).
 *
 * Rates ko editable data table ki tarah rakha gaya hai — agli notification par sirf
 * yahan numbers badalne hain, app logic waisa hi rahega.
 */
public class TariffData {

    public static final String EFFECTIVE_DATE = "12 Feb 2026";
    public static final String NOTIFICATION = "S.R.O. 279(I)/2026";

    public static class Slab {
        public final int from;           // is slab ka pehla unit
        public final int to;             // is slab ka aakhri unit (Integer.MAX_VALUE = open-ended)
        public final double rate;        // Rs per unit
        public final double fixedPerKw;  // Rs per kW sanctioned load (lifeline me 0)

        public Slab(int from, int to, double rate, double fixedPerKw) {
            this.from = from;
            this.to = to;
            this.rate = rate;
            this.fixedPerKw = fixedPerKw;
        }
    }

    public static class Tariff {
        public final String id;
        public final String nameEn;
        public final String nameUr;
        public final double minBill;     // lifeline: 75, baqi: 0
        public final List<Slab> slabs = new ArrayList<>();

        public Tariff(String id, String nameEn, String nameUr, double minBill) {
            this.id = id;
            this.nameEn = nameEn;
            this.nameUr = nameUr;
            this.minBill = minBill;
        }

        public Tariff add(int from, int to, double rate, double fixedPerKw) {
            slabs.add(new Slab(from, to, rate, fixedPerKw));
            return this;
        }
    }

    public static List<Tariff> all() {
        List<Tariff> list = new ArrayList<>();

        Tariff np = new Tariff("non_protected", "Non-Protected (A-1b)", "\u063A\u06CC\u0631 \u0645\u062D\u0641\u0648\u0638 (A-1b)", 0);
        np.add(1, 100, 22.44, 275)
          .add(101, 200, 28.91, 300)
          .add(201, 300, 33.10, 350)
          .add(301, 400, 36.46, 400)
          .add(401, 500, 38.95, 500)
          .add(501, 600, 40.22, 675)
          .add(601, 700, 41.85, 675)
          .add(701, Integer.MAX_VALUE, 47.20, 675);
        list.add(np);

        Tariff p = new Tariff("protected", "Protected (A-1a)", "\u0645\u062D\u0641\u0648\u0638 (A-1a)", 0);
        p.add(1, 100, 10.54, 200)
         .add(101, 200, 13.01, 300);
        list.add(p);

        Tariff ll = new Tariff("lifeline", "Lifeline", "\u0644\u0627\u0626\u0641 \u0644\u0627\u0626\u0646", 75);
        ll.add(1, 50, 3.95, 0)
          .add(51, 100, 7.74, 0);
        list.add(ll);

        return list;
    }

    /** Ek slab ki line — breakdown me dikhayi jati hai. */
    public static class Row {
        public final String label;   // e.g. "1 – 100 units"
        public final int units;
        public final double rate;
        public final double amount;

        public Row(String label, int units, double rate) {
            this.label = label;
            this.units = units;
            this.rate = rate;
            this.amount = units * rate;
        }
    }

    public static class Bill {
        public final List<Row> rows = new ArrayList<>();
        public double variable;       // slabs ka total
        public double fixed;          // fixed charge (sanctioned load ke hisab se)
        public double total;          // variable + fixed (min bill applied)
        public double appliedFixedRate;
        public boolean minBillApplied;
    }

    /** Incremental slab calculation. Fixed charge = units jis slab me hain, us ka rate x sanctioned kW. */
    public static Bill calculate(Tariff t, int units, double sanctionedKw) {
        Bill b = new Bill();
        if (units <= 0) {
            return b;
        }
        for (Slab s : t.slabs) {
            if (units < s.from) {
                break;
            }
            int hi = Math.min(units, s.to);
            int n = hi - s.from + 1;
            if (n <= 0) {
                continue;
            }
            String label = (s.to == Integer.MAX_VALUE)
                    ? s.from + "+ units"
                    : s.from + " \u2013 " + s.to + " units";
            b.rows.add(new Row(label, n, s.rate));
            b.variable += n * s.rate;
            b.appliedFixedRate = s.fixedPerKw;
        }
        b.fixed = b.appliedFixedRate * sanctionedKw;
        b.total = b.variable + b.fixed;
        if (t.minBill > 0 && b.total < t.minBill) {
            b.total = t.minBill;
            b.minBillApplied = true;
        }
        b.variable = round2(b.variable);
        b.fixed = round2(b.fixed);
        b.total = round2(b.total);
        return b;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
