package com.bijlibillcalculator.app;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bijlibillcalculator.app.data.TariffData;

import java.util.List;
import java.util.Locale;

/** Bijli Bill Calculator v1 — slab-wise bill estimate, Urdu + English, zero permissions. */
public class MainActivity extends AppCompatActivity {

    private List<TariffData.Tariff> tariffs;
    private int selected = 0;
    private Button[] typeButtons;
    private EditText unitsInput;
    private EditText loadInput;
    private LinearLayout resultBox;

    private boolean isUrdu() {
        return getResources().getConfiguration().getLocales().get(0).getLanguage().equals("ur");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tariffs = TariffData.all();

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        root.setPadding(pad, pad, pad, pad);
        scroll.addView(root, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        boolean urdu = isUrdu();

        // Title
        TextView title = new TextView(this);
        title.setText(getString(R.string.app_name));
        title.setTextSize(24);
        title.setTextColor(Color.parseColor("#0F172A"));
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText(getString(R.string.tagline));
        sub.setTextSize(14);
        sub.setTextColor(Color.parseColor("#64748B"));
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.topMargin = dp(4);
        slp.bottomMargin = dp(12);
        root.addView(sub, slp);

        // Tariff date badge
        TextView badge = new TextView(this);
        badge.setText(getString(R.string.tariff_date));
        badge.setTextSize(12);
        badge.setTextColor(Color.WHITE);
        badge.setGravity(Gravity.CENTER);
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.parseColor("#0288D1"));
        badgeBg.setCornerRadius(dp(12));
        badge.setBackground(badgeBg);
        int bpad = dp(8);
        badge.setPadding(bpad, dp(6), bpad, dp(6));
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp.bottomMargin = dp(16);
        root.addView(badge, blp);

        // Consumer type
        root.addView(label(getString(R.string.consumer_type)));
        typeButtons = new Button[tariffs.size()];
        for (int i = 0; i < tariffs.size(); i++) {
            final int idx = i;
            TariffData.Tariff t = tariffs.get(i);
            Button b = new Button(this);
            b.setText(urdu ? t.nameUr : t.nameEn);
            b.setTextSize(16);
            b.setAllCaps(false);
            b.setOnClickListener(v -> {
                selected = idx;
                refreshTypeButtons();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(8);
            root.addView(b, lp);
            typeButtons[i] = b;
        }
        refreshTypeButtons();

        // Units
        LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gap.topMargin = dp(8);
        root.addView(label(getString(R.string.units_label)), gap);
        unitsInput = new EditText(this);
        unitsInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        unitsInput.setHint(getString(R.string.units_hint));
        unitsInput.setTextSize(18);
        root.addView(unitsInput);

        // Sanctioned load
        root.addView(label(getString(R.string.load_label)), gap);
        loadInput = new EditText(this);
        loadInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        loadInput.setHint(getString(R.string.load_hint));
        loadInput.setText("1");
        loadInput.setTextSize(18);
        root.addView(loadInput);

        // Calculate button
        Button calc = new Button(this);
        calc.setText(getString(R.string.calculate));
        calc.setTextSize(18);
        calc.setAllCaps(false);
        calc.setTextColor(Color.WHITE);
        GradientDrawable calcBg = new GradientDrawable();
        calcBg.setColor(Color.parseColor("#0F172A"));
        calcBg.setCornerRadius(dp(16));
        calc.setBackground(calcBg);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.topMargin = dp(20);
        clp.bottomMargin = dp(8);
        calc.setOnClickListener(v -> calculate());
        root.addView(calc, clp);

        // Result box
        resultBox = new LinearLayout(this);
        resultBox.setOrientation(LinearLayout.VERTICAL);
        root.addView(resultBox);

        // Disclaimer
        TextView foot = new TextView(this);
        foot.setText(getString(R.string.disclaimer));
        foot.setTextSize(11);
        foot.setTextColor(Color.parseColor("#94A3B8"));
        foot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.topMargin = dp(24);
        root.addView(foot, flp);

        setContentView(scroll);
    }

    private TextView label(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor("#0F172A"));
        return tv;
    }

    private void refreshTypeButtons() {
        for (int i = 0; i < typeButtons.length; i++) {
            Button b = typeButtons[i];
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(14));
            if (i == selected) {
                bg.setColor(Color.parseColor("#0F172A"));
                b.setTextColor(Color.WHITE);
            } else {
                bg.setColor(Color.parseColor("#E2E8F0"));
                b.setTextColor(Color.parseColor("#0F172A"));
            }
            b.setBackground(bg);
        }
    }

    private void calculate() {
        String u = unitsInput.getText().toString().trim();
        String l = loadInput.getText().toString().trim();
        int units;
        try {
            units = Integer.parseInt(u);
        } catch (NumberFormatException e) {
            units = -1;
        }
        double kw;
        try {
            kw = Double.parseDouble(l);
        } catch (NumberFormatException e) {
            kw = 1;
        }
        if (units <= 0 || kw <= 0) {
            Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
            return;
        }

        TariffData.Tariff t = tariffs.get(selected);
        TariffData.Bill bill = TariffData.calculate(t, units, kw);

        resultBox.removeAllViews();
        resultBox.addView(sectionTitle(getString(R.string.result_title)));

        for (TariffData.Row r : bill.rows) {
            TextView row = new TextView(this);
            row.setText(r.label + "  ×  " + fmt(r.rate) + "  =  " + money(r.amount));
            row.setTextSize(14);
            row.setTextColor(Color.parseColor("#334155"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = dp(2);
            resultBox.addView(row, lp);
        }

        resultBox.addView(totalLine(getString(R.string.variable_total), money(bill.variable)));
        if (bill.fixed > 0) {
            resultBox.addView(totalLine(
                    getString(R.string.fixed_charge) + " (" + money(bill.appliedFixedRate) + " × " + trimKw(kw) + " kW)",
                    money(bill.fixed)));
        }
        TextView grand = new TextView(this);
        grand.setText(getString(R.string.grand_total) + ":  " + money(bill.total));
        grand.setTextSize(20);
        grand.setTextColor(Color.parseColor("#0F172A"));
        LinearLayout.LayoutParams glp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        glp.topMargin = dp(10);
        resultBox.addView(grand, glp);

        // Warnings
        if (t.id.equals("protected") && units > 200) {
            resultBox.addView(warn(getString(R.string.protected_warning)));
        }
        if (t.id.equals("lifeline") && units > 100) {
            resultBox.addView(warn(getString(R.string.lifeline_warning)));
        }
        if (bill.minBillApplied) {
            resultBox.addView(warn(getString(R.string.min_bill_note)));
        }

        // Extras note
        TextView extras = new TextView(this);
        extras.setText(getString(R.string.extras_note));
        extras.setTextSize(12);
        extras.setTextColor(Color.parseColor("#64748B"));
        LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        elp.topMargin = dp(12);
        resultBox.addView(extras, elp);
    }

    private TextView sectionTitle(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(18);
        tv.setTextColor(Color.parseColor("#0F172A"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(16);
        lp.bottomMargin = dp(6);
        tv.setLayoutParams(lp);
        return tv;
    }

    private TextView totalLine(String name, String value) {
        TextView tv = new TextView(this);
        tv.setText(name + ":  " + value);
        tv.setTextSize(15);
        tv.setTextColor(Color.parseColor("#0F172A"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(8);
        tv.setLayoutParams(lp);
        return tv;
    }

    private TextView warn(String text) {
        TextView tv = new TextView(this);
        tv.setText("⚠ " + text);
        tv.setTextSize(13);
        tv.setTextColor(Color.parseColor("#B45309"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(8);
        tv.setLayoutParams(lp);
        return tv;
    }

    private String money(double v) {
        return String.format(Locale.US, "Rs %,.2f", v);
    }

    private String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private String trimKw(double v) {
        return (v == Math.floor(v)) ? String.valueOf((int) v) : String.valueOf(v);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
