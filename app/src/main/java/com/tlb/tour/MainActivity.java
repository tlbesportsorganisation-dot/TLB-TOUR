package com.tlb.tour;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    int blue = Color.rgb(25, 88, 220);
    LinearLayout root;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        showLogin();
    }

    TextView text(String s, int size) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(Color.DKGRAY);
        t.setPadding(10, 12, 10, 12);
        return t;
    }

    Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        return b;
    }

    void baseLayout() {
        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(245, 248, 255));

        scroll.addView(root);
        setContentView(scroll);
    }

    void showLogin() {

        baseLayout();

        TextView title = text("👑 TLB TOUR", 30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(blue);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView sub = text("FREE FIRE TOURNAMENT", 18);
        sub.setGravity(Gravity.CENTER);
        root.addView(sub);

        EditText email = new EditText(this);
        email.setHint("Email");
        email.setInputType(33);
        root.addView(email);

        EditText password = new EditText(this);
        password.setHint("Password");
        password.setInputType(129);
        root.addView(password);

        Button login = button("🔐 Login");
        root.addView(login);

        Button register = button("📝 Create New Account");
        root.addView(register);

        TextView info = text("Secure • Fair • Competitive", 15);
        info.setGravity(Gravity.CENTER);
        root.addView(info);

        login.setOnClickListener(v -> {

            if (email.getText().toString().trim().isEmpty()
                    || password.getText().toString().trim().isEmpty()) {

                Toast.makeText(
                        this,
                        "Email and Password দিন",
                        Toast.LENGTH_SHORT
                ).show();

            } else {
                showHome();
            }
        });

        register.setOnClickListener(v -> showRegister());
    }

    void showRegister() {

        baseLayout();

        TextView title = text("📝 Create Account", 28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(blue);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        EditText name = new EditText(this);
        name.setHint("Full Name");
        root.addView(name);

        EditText username = new EditText(this);
        username.setHint("Username");
        root.addView(username);

        EditText email = new EditText(this);
        email.setHint("Email");
        email.setInputType(33);
        root.addView(email);

        EditText phone = new EditText(this);
        phone.setHint("Phone Number");
        phone.setInputType(2);
        root.addView(phone);

        EditText password = new EditText(this);
        password.setHint("Password");
        password.setInputType(129);
        root.addView(password);

        EditText confirm = new EditText(this);
        confirm.setHint("Confirm Password");
        confirm.setInputType(129);
        root.addView(confirm);

        EditText referral = new EditText(this);
        referral.setHint("Referral Code (Optional)");
        root.addView(referral);

        Button create = button("✅ Create Account");
        root.addView(create);

        Button back = button("← Back to Login");
        root.addView(back);

        create.setOnClickListener(v -> {

            if (name.getText().toString().trim().isEmpty()
                    || username.getText().toString().trim().isEmpty()
                    || email.getText().toString().trim().isEmpty()
                    || password.getText().toString().trim().isEmpty()) {

                Toast.makeText(
                        this,
                        "সব প্রয়োজনীয় তথ্য দিন",
                        Toast.LENGTH_SHORT
                ).show();

            } else if (!password.getText().toString()
                    .equals(confirm.getText().toString())) {

                Toast.makeText(
                        this,
                        "Password মিলছে না",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Account তৈরি হচ্ছে...",
                        Toast.LENGTH_SHORT
                ).show();

                showHome();
            }
        });

        back.setOnClickListener(v -> showLogin());
    }

    void showHome() {

        baseLayout();

        TextView title = text("👑 TLB TOUR", 30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(blue);
        root.addView(title);

        TextView sub = text(
                "FREE FIRE TOURNAMENT\nLive • Fair • Competitive",
                18
        );
        sub.setGravity(Gravity.CENTER);
        root.addView(sub);

        TextView balance = text(
                "💰 Wallet Balance\n৳ 0.00",
                20
        );
        balance.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        balance.setTextColor(Color.WHITE);
        balance.setGravity(Gravity.CENTER);
        balance.setBackgroundColor(Color.rgb(20, 150, 90));
        root.addView(balance);

        TextView live = text("🔴 LIVE TOURNAMENTS", 20);
        live.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        live.setTextColor(blue);
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
                "👤 Profile",
                "⚙️ Admin Panel"
        };

        for (String s : items) {
            Button b = button(s);
            root.addView(b);

            b.setOnClickListener(v ->
                    Toast.makeText(
                            this,
                            s + " — Coming Next",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }
    }
            }
