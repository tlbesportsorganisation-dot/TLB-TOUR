package com.tlb.tour;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private final int blue = Color.rgb(25, 88, 220);
    private final int green = Color.rgb(20, 150, 90);

    private LinearLayout root;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            showHome();
        } else {
            showLogin();
        }
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.DKGRAY);
        t.setPadding(10, 12, 10, 12);
        return t;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setAllCaps(false);
        return b;
    }

    private void baseLayout() {
        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(245, 248, 255));

        scroll.addView(root);
        setContentView(scroll);
    }

    private void showLogin() {
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
        email.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        root.addView(email);

        EditText password = new EditText(this);
        password.setHint("Password");
        password.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        root.addView(password);

        Button login = button("🔐 Login");
        root.addView(login);

        Button register = button("📝 Create New Account");
        root.addView(register);

        TextView info = text("Secure • Fair • Competitive", 15);
        info.setGravity(Gravity.CENTER);
        root.addView(info);

        login.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(
                        this,
                        "Email এবং Password দিন",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            login.setEnabled(false);
            Toast.makeText(
                    this,
                    "Login হচ্ছে...",
                    Toast.LENGTH_SHORT
            ).show();

            auth.signInWithEmailAndPassword(
                    emailText,
                    passwordText
            ).addOnCompleteListener(task -> {
                login.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(
                            this,
                            "Login সফল হয়েছে",
                            Toast.LENGTH_SHORT
                    ).show();

                    showHome();
                } else {
                    String message = task.getException() != null
                            ? task.getException().getMessage()
                            : "Login ব্যর্থ হয়েছে";

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        register.setOnClickListener(v -> showRegister());
    }

    private void showRegister() {
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
        email.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        root.addView(email);

        EditText phone = new EditText(this);
        phone.setHint("Phone Number");
        phone.setInputType(InputType.TYPE_CLASS_PHONE);
        root.addView(phone);

        EditText password = new EditText(this);
        password.setHint("Password");
        password.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        root.addView(password);

        EditText confirm = new EditText(this);
        confirm.setHint("Confirm Password");
        confirm.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        root.addView(confirm);

        EditText referral = new EditText(this);
        referral.setHint("Referral Code (Optional)");
        root.addView(referral);

        Button create = button("✅ Create Account");
        root.addView(create);

        Button back = button("← Back to Login");
        root.addView(back);

        create.setOnClickListener(v -> {
            String nameText = name.getText().toString().trim();
            String usernameText = username.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String phoneText = phone.getText().toString().trim();
            String passwordText = password.getText().toString();
            String confirmText = confirm.getText().toString();
            String referralText = referral.getText().toString().trim();

            if (nameText.isEmpty()
                    || usernameText.isEmpty()
                    || emailText.isEmpty()
                    || passwordText.isEmpty()) {

                Toast.makeText(
                        this,
                        "সব প্রয়োজনীয় তথ্য দিন",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!passwordText.equals(confirmText)) {
                Toast.makeText(
                        this,
                        "Password মিলছে না",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (passwordText.length() < 6) {
                Toast.makeText(
                        this,
                        "Password কমপক্ষে 6 অক্ষরের হতে হবে",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            create.setEnabled(false);

            Toast.makeText(
                    this,
                    "Account তৈরি হচ্ছে...",
                    Toast.LENGTH_SHORT
            ).show();

            auth.createUserWithEmailAndPassword(
                    emailText,
                    passwordText
            ).addOnCompleteListener(task -> {

                if (!task.isSuccessful()) {
                    create.setEnabled(true);

                    String message = task.getException() != null
                            ? task.getException().getMessage()
                            : "Account তৈরি করা যায়নি";

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }

                String uid = auth.getCurrentUser().getUid();

                Map<String, Object> user = new HashMap<>();

                user.put("uid", uid);
                user.put("name", nameText);
                user.put("username", usernameText);
                user.put("email", emailText);
                user.put("phone", phoneText);
                user.put("referralCode", referralText);
                user.put("walletBalance", 0.0);
                user.put("createdAt", System.currentTimeMillis());

                db.collection("users")
                        .document(uid)
                        .set(user)
                        .addOnCompleteListener(saveTask -> {

                            create.setEnabled(true);

                            if (saveTask.isSuccessful()) {
                                Toast.makeText(
                                        this,
                                        "Account সফলভাবে তৈরি হয়েছে",
                                        Toast.LENGTH_SHORT
                                ).show();

                                showHome();

                            } else {
                                Toast.makeText(
                                        this,
                                        "Account হয়েছে, কিন্তু Profile data save হয়নি",
                                        Toast.LENGTH_LONG
                                ).show();

                                showHome();
                            }
                        });
            });
        });

        back.setOnClickListener(v -> showLogin());
    }

    private void showHome() {
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

        balance.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        balance.setTextColor(Color.WHITE);
        balance.setGravity(Gravity.CENTER);
        balance.setBackgroundColor(green);
        root.addView(balance);

        TextView live = text(
                "🔴 LIVE TOURNAMENTS",
                20
        );

        live.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

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

        for (String item : items) {
            Button b = button(item);

            root.addView(b);

            b.setOnClickListener(v ->
                    Toast.makeText(
                            this,
                            item + " — Coming Next",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }

        Button logout = button("🚪 Logout");
        root.addView(logout);

        logout.setOnClickListener(v -> {
            auth.signOut();
            showLogin();
        });
    }
        }
