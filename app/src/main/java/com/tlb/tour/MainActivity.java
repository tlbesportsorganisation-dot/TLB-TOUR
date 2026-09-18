package com.tlb.tour;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;
import android.view.View;

public class MainActivity extends Activity {
    int blue = Color.rgb(25, 88, 220);
    LinearLayout root;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        showHome();
    }

    TextView title(String s, int size) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(Color.WHITE);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(20,18,20,18);
        return t;
    }

    Button btn(String text) {
        Button b = new Button(this);
        b.setText(text); b.setAllCaps(false);
        b.setOnClickListener(v -> Toast.makeText(this, text + " — coming next", Toast.LENGTH_SHORT).show());
        return b;
    }

    void showHome() {
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18,18,18,18);
        root.setBackgroundColor(Color.rgb(245,248,255));

        TextView head = title("👑  TLB TOUR", 28);
        head.setGravity(Gravity.CENTER);
        head.setBackgroundColor(blue);
        root.addView(head);

        TextView sub = new TextView(this);
        sub.setText("FREE FIRE TOURNAMENT\nLive • Fair • Competitive");
        sub.setTextSize(18); sub.setTextColor(Color.DKGRAY);
        sub.setGravity(Gravity.CENTER); sub.setPadding(10,22,10,22);
        root.addView(sub);

        TextView live = new TextView(this);
        live.setText("🔴 LIVE TOURNAMENTS");
        live.setTextSize(20); live.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        live.setTextColor(blue); live.setPadding(8,12,8,8);
        root.addView(live);

        String[] items = {"🏆 Tournaments", "💰 Wallet", "📥 Deposit", "📤 Withdraw",
                          "🎁 Referral", "📊 Point Table", "🔥 Daily Highest Income", "🔔 Notifications", "⚙️ Admin Panel"};
        for (String s : items) root.addView(btn(s));

        scroll.addView(root);
        setContentView(scroll);
    }
}
