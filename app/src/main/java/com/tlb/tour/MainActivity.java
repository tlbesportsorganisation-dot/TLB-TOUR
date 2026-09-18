package com.tlb.tour;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {

    int blue = Color.rgb(25, 88, 220);
    LinearLayout root;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        showHome();
    }

    TextView title(String s, int size) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(20, 18, 20, 18);
        return t;
    }

    Button btn(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setOnClickListener(v ->
            Toast.makeText(this, text + " — coming next",
            Toast.LENGTH_SHORT).show()
        );
        return b;
    }

    void showHome() {

        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 18);
        root.setBackgroundColor(Color.rgb(245, 248, 255));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.tlb_tour_logo);
        logo.setAdjustViewBounds(true);
        logo.setPadding(10, 10, 10, 10);

        LinearLayout.LayoutParams logoParams =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            );

        logoParams.gravity = Gravity.CENTER;
        root.addView(logo, logoParams);

        TextView head = title("👑 TLB TOUR", 28);
        head.setGravity(Gravity.CENTER);
        head.setBackgroundColor(blue);
        root.addView(head);

        TextView sub = new TextView(this);
        sub.setText("FREE FIRE TOURNAMENT\nLive • Fair • Competitive");
        sub.setTextSize(18);
        sub.setTextColor(Color.DKGRAY);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(10, 22, 10, 22);
        root.addView(sub);

        TextView balance = new TextView(this);
        balance.setText("💰 Wallet Balance\n৳ 0.00");
        balance.setTextSize(20);
        balance.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        balance.setTextColor(Color.WHITE);
        balance.setGravity(Gravity.CENTER);
        balance.setPadding(10, 22, 10, 22);
        balance.setBackgroundColor(Color.rgb(20, 150, 90));
        root.addView(balance);

        TextView live = new TextView(this);
        live.setText("🔴 LIVE TOURNAMENTS");
        live.setTextSize(20);
        live.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        live.setTextColor(blue);
        live.setPadding(8, 18, 8, 8);
        root.addView(live);

        String[] items = {
            "🏆 Tournaments",
            "💰 Wallet",
            "📥 Deposit",
            "📤 Withdraw",
            "🎁 Referral",
            "📊 Point Table",
            "🔥 Daily Highest Income",
            "🔔 Notifications",
            "⚙️ Admin Panel"
        };

        for (String s : items) {
            root.addView(btn(s));
        }

        scroll.addView(root);
        setContentView(scroll);
    }
    }
