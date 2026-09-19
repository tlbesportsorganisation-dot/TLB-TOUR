package com.tlb.tour;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.*;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.util.*;
import java.util.regex.*;

public class MainActivity extends Activity {
    private static final int BLUE=Color.rgb(25,88,220), GREEN=Color.rgb(20,150,90), RED=Color.rgb(220,60,60);
    private LinearLayout root; private FirebaseAuth auth; private FirebaseFirestore db; private FirebaseStorage storage;
    private Uri pendingImage; private String pendingMatchId="";

    @Override protected void onCreate(Bundle b){super.onCreate(b);auth=FirebaseAuth.getInstance();db=FirebaseFirestore.getInstance();storage=FirebaseStorage.getInstance(); if(auth.getCurrentUser()!=null) showHome(); else showLogin();}
    private TextView tv(String s,int z){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(Color.DKGRAY);t.setPadding(12,12,12,12);return t;}
    private Button btn(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);return b;}
    private EditText field(String h){EditText e=new EditText(this);e.setHint(h);e.setPadding(14,8,14,8);root.addView(e);return e;}
    private void base(String title){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(18,18,18,24);root.setBackgroundColor(Color.rgb(245,248,255));TextView h=tv(title,25);h.setTextColor(Color.WHITE);h.setGravity(Gravity.CENTER);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);h.setBackgroundColor(BLUE);root.addView(h);sc.addView(root);setContentView(sc);}
    private void back(){Button b=btn("← Back");root.addView(b);b.setOnClickListener(v->showHome());}
    private void msg(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    private String safe(String s){return s==null?"":s;}
    private double num(DocumentSnapshot d,String k){Number n=d.get(k,Number.class);return n==null?0:n.doubleValue();}
    private double parse(String s){try{return Double.parseDouble(s.trim());}catch(Exception e){return 0;}}
    private String uname(){return auth.getCurrentUser()==null?"":auth.getCurrentUser().getUid();}

    private void showLogin(){
        base("👑 TLB TOUR"); TextView sub=tv("FREE FIRE TOURNAMENT\nSecure • Fair • Competitive",18);sub.setGravity(Gravity.CENTER);root.addView(sub);
        EditText u=field("Username"), p=field("6-digit Password");p.setInputType(2);
        Button l=btn("🔐 Login");root.addView(l);Button r=btn("📝 Create New Account");root.addView(r);
        l.setOnClickListener(v->{String us=u.getText().toString().trim(),pw=p.getText().toString();if(us.isEmpty()||!pw.matches("\\d{6}")){msg("Username এবং ঠিক 6-digit password দিন");return;}
            l.setEnabled(false);db.collection("users").whereEqualTo("usernameLower",us.toLowerCase(Locale.US)).limit(1).get().addOnSuccessListener(q->{
                if(q.isEmpty()){l.setEnabled(true);msg("Username পাওয়া যায়নি");return;} String email=q.getDocuments().get(0).getString("email");
                auth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(t->{l.setEnabled(true);if(t.isSuccessful())showHome();else msg("Login ব্যর্থ");});
            }).addOnFailureListener(e->{l.setEnabled(true);msg("Login error: "+e.getMessage());});
        }); r.setOnClickListener(v->showRegister());
    }

    private void showRegister(){
        base("📝 Create Account"); EditText n=field("Full Name"),u=field("Username"),e=field("Email"),ph=field("Phone Number"),p=field("6-digit Password"),c=field("Confirm Password"),ref=field("Referral Code (Optional)");p.setInputType(2);c.setInputType(2);
        Button b=btn("✅ Create Account");root.addView(b);Button bk=btn("← Back to Login");root.addView(bk);
        b.setOnClickListener(v->{String ns=n.getText().toString().trim(),us=u.getText().toString().trim(),es=e.getText().toString().trim(),pw=p.getText().toString(),cp=c.getText().toString(),rs=ref.getText().toString().trim();
            if(ns.isEmpty()||us.isEmpty()||es.isEmpty()||!pw.matches("\\d{6}")){msg("সব তথ্য দিন এবং password ঠিক 6 digit করুন");return;}if(!pw.equals(cp)){msg("Password মিলছে না");return;}
            b.setEnabled(false);db.collection("users").whereEqualTo("usernameLower",us.toLowerCase(Locale.US)).limit(1).get().addOnSuccessListener(q->{if(!q.isEmpty()){b.setEnabled(true);msg("এই username আগে থেকেই আছে");return;}
                auth.createUserWithEmailAndPassword(es,pw).addOnCompleteListener(t->{if(!t.isSuccessful()){b.setEnabled(true);msg(t.getException()!=null?t.getException().getMessage():"Account তৈরি হয়নি");return;}
                    String uid=auth.getCurrentUser().getUid();Map<String,Object> m=new HashMap<>();m.put("uid",uid);m.put("name",ns);m.put("username",us);m.put("usernameLower",us.toLowerCase(Locale.US));m.put("email",es);m.put("phone",ph.getText().toString().trim());m.put("referralCode","TLB"+uid.substring(0,6).toUpperCase(Locale.US));m.put("referralInput",rs);m.put("walletBalance",0.0);m.put("role","user");m.put("createdAt",System.currentTimeMillis());
                    db.collection("users").document(uid).set(m).addOnCompleteListener(x->{b.setEnabled(true);if(x.isSuccessful())showHome();else msg("Profile save হয়নি");});
                });
            });
        });bk.setOnClickListener(v->showLogin());
    }

    private void showHome(){
        base("👑 TLB TOUR");TextView sub=tv("FREE FIRE TOURNAMENT\nLive • Fair • Competitive",18);sub.setGravity(Gravity.CENTER);root.addView(sub);
        TextView bal=tv("💰 Wallet\nLoading...",21);bal.setTextColor(Color.WHITE);bal.setGravity(Gravity.CENTER);bal.setBackgroundColor(GREEN);root.addView(bal);
        String[] a={"🏆 Tournaments","🎮 My Matches","📊 Results","📈 Auto Point Table","💰 Wallet","📥 Deposit","📤 Withdraw","🎁 Referral","🔥 Daily Earnings","🔔 Notifications","👤 Profile","⚙️ Admin Panel","🚪 Logout"};
        for(String s:a){Button b=btn(s);root.addView(b);b.setOnClickListener(v->{if(s.contains("Tournaments"))showTournaments();else if(s.contains("My Matches"))showMyMatches();else if(s.contains("Results"))showResults();else if(s.contains("Point"))showPointTable();else if(s.equals("💰 Wallet"))showWallet();else if(s.contains("Deposit"))showDeposit();else if(s.contains("Withdraw"))showWithdraw();else if(s.contains("Referral"))showReferral();else if(s.contains("Daily"))showLeaderboard();else if(s.contains("Notifications"))showNotifications();else if(s.contains("Profile"))showProfile();else if(s.contains("Admin"))showAdmin();else{auth.signOut();showLogin();}});}loadBalance(bal);
    }
    private void loadBalance(TextView b){db.collection("users").document(uname()).get().addOnSuccessListener(d->b.setText(String.format(Locale.US,"💰 Wallet\n৳ %.2f",num(d,"walletBalance"))));}

    private void showTournaments(){
        base("🏆 Tournaments");root.addView(tv("Live tournament list • বিস্তারিত তথ্য • Real-time status",15));
        db.collection("tournaments").get().addOnSuccessListener(s->{if(s.isEmpty())root.addView(tv("এখনও কোনো tournament publish হয়নি।",17));
            for(DocumentSnapshot d:s){String id=d.getId(),name=safe(d.getString("name")),status=safe(d.getString("status"));double fee=num(d,"entryFee");
                String info="🏆 "+name+"\nMode: "+safe(d.getString("mode"))+"   Map: "+safe(d.getString("map"))+"\nSchedule: "+safe(d.getString("startTime"))+"\nPrize: ৳"+num(d,"prize")+"   Per Kill: ৳"+num(d,"perKill")+"\nPlayers: "+(long)num(d,"joinedPlayers")+"/"+(long)num(d,"maxPlayers")+"   Entry: ৳"+fee+"\nDevice: "+safe(d.getString("device"))+"\nPrize Pool: ৳"+num(d,"prizePool")+"\nStatus: "+status;
                if("MATCH_FOUND".equalsIgnoreCase(status)||"LIVE".equalsIgnoreCase(status))info+="\nRoom ID: "+safe(d.getString("roomId"))+"\nRoom Password: "+safe(d.getString("roomPassword"));
                LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(12,12,12,12);card.setBackgroundColor(Color.WHITE);card.addView(tv(info,16));Button j=btn("🎮 Join Now");card.addView(j);root.addView(card);
                j.setEnabled(!"CLOSED".equalsIgnoreCase(status)&&!"MATCH_FOUND".equalsIgnoreCase(status)&&!"LIVE".equalsIgnoreCase(status));j.setOnClickListener(v->joinTournament(id,name,fee,j));
            }
        });back();
    }
    private void joinTournament(String id,String name,double fee,Button b){
        String uid=uname();b.setEnabled(false);db.runTransaction(tr->{DocumentReference u=db.collection("users").document(uid),e=db.collection("entries").document(uid+"_"+id),tref=db.collection("tournaments").document(id);DocumentSnapshot ud=tr.get(u),td=tr.get(tref);
            if(tr.get(e).exists())throw new FirebaseFirestoreException("আপনি আগে থেকেই joined",FirebaseFirestoreException.Code.ABORTED);
            long joined=(long)num(td,"joinedPlayers"),max=(long)num(td,"maxPlayers");if(max>0&&joined>=max)throw new FirebaseFirestoreException("Match full",FirebaseFirestoreException.Code.ABORTED);
            double bal=num(ud,"walletBalance");if(bal<fee)throw new FirebaseFirestoreException("Wallet balance কম",FirebaseFirestoreException.Code.ABORTED);
            tr.update(u,"walletBalance",bal-fee);tr.update(tref,"joinedPlayers",joined+1);
            Map<String,Object> m=new HashMap<>();m.put("uid",uid);m.put("tournamentId",id);m.put("tournamentName",name);m.put("entryFee",fee);m.put("status","JOINED");m.put("createdAt",System.currentTimeMillis());tr.set(e,m);return null;
        }).addOnSuccessListener(x->{msg("Tournament joined");}).addOnFailureListener(x->{b.setEnabled(true);msg(x.getMessage()!=null?x.getMessage():"Join failed");});
    }

    private void showMyMatches(){base("🎮 My Matches");db.collection("entries").whereEqualTo("uid",uname()).get().addOnSuccessListener(s->{if(s.isEmpty())root.addView(tv("কোনো joined match নেই।",17));for(DocumentSnapshot d:s){String tid=safe(d.getString("tournamentId"));String info="🎮 "+safe(d.getString("tournamentName"))+"\nEntry: ৳"+num(d,"entryFee")+"\nStatus: "+safe(d.getString("status"));db.collection("tournaments").document(tid).get().addOnSuccessListener(t->{String st=safe(t.getString("status"));if("MATCH_FOUND".equalsIgnoreCase(st)||"LIVE".equalsIgnoreCase(st))info+="\nRoom ID: "+safe(t.getString("roomId"))+"\nRoom Password: "+safe(t.getString("roomPassword"));root.addView(tv(info,17));});}});back();}
    private void showResults(){base("📊 Results");db.collection("results").get().addOnSuccessListener(s->{if(s.isEmpty())root.addView(tv("কোনো result নেই।",17));for(DocumentSnapshot d:s)root.addView(tv("🏆 "+safe(d.getString("matchId"))+"\nPlayer: "+safe(d.getString("playerName"))+"\nRank: "+num(d,"rank")+"  Kills: "+num(d,"kills")+"  Points: "+num(d,"totalPoints"),16));});back();}

    private void showWallet(){base("💰 Wallet");TextView b=tv("Loading...",24);root.addView(b);loadBalance(b);Button h=btn("📜 Transaction History");root.addView(h);h.setOnClickListener(v->showTransactions());back();}
    private void showTransactions(){base("📜 Transactions");db.collection("transactions").whereEqualTo("uid",uname()).get().addOnSuccessListener(s->{if(s.isEmpty())root.addView(tv("কোনো transaction নেই।",17));for(DocumentSnapshot d:s)root.addView(tv(safe(d.getString("type"))+" | ৳"+num(d,"amount")+" | "+safe(d.getString("status"))+"\n"+safe(d.getString("method")),16));});back();}
    private void showDeposit(){base("📥 Deposit");Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"bKash","Nagad"}));root.addView(sp);EditText am=field("Amount (৳)"),tr=field("Transaction ID");Button b=btn("Submit Deposit");root.addView(b);b.setOnClickListener(v->{double a=parse(am.getText().toString());if(a<=0||tr.getText().toString().trim().isEmpty()){msg("সঠিক তথ্য দিন");return;}Map<String,Object>m=tx("DEPOSIT",a,(String)sp.getSelectedItem());m.put("uid",uname());m.put("transactionId",tr.getText().toString().trim());m.put("status","PENDING");db.collection("transactions").add(m).addOnSuccessListener(x->{msg("Deposit request submitted");showWallet();});});back();}
    private void showWithdraw(){base("📤 Withdraw");Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"bKash","Nagad"}));root.addView(sp);EditText num=field("bKash / Nagad Number"),am=field("Amount (৳)");Button b=btn("Submit Withdraw");root.addView(b);b.setOnClickListener(v->{double a=parse(am.getText().toString());if(a<=0||num.getText().toString().trim().isEmpty()){msg("সঠিক তথ্য দিন");return;}Map<String,Object>m=tx("WITHDRAW",a,(String)sp.getSelectedItem());m.put("uid",uname());m.put("accountNumber",num.getText().toString().trim());m.put("status","PENDING");db.collection("transactions").add(m).addOnSuccessListener(x->{msg("Withdraw request submitted");showWallet();});});back();}

    private void showReferral(){base("🎁 Referral");db.collection("users").document(uname()).get().addOnSuccessListener(d->{String c=safe(d.getString("referralCode"));root.addView(tv("Your Referral Code\n"+c,24));Button cp=btn("📋 Copy Code");root.addView(cp);cp.setOnClickListener(v->{ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);cm.setPrimaryClip(ClipData.newPlainText("TLB Referral",c));msg("Code copied");});db.collection("users").whereEqualTo("referralInput",c).get().addOnSuccessListener(q->root.addView(tv("Direct Referrals: "+q.size(),18)));});back();}
    private void showPointTable(){base("📈 Auto Point Table");db.collection("pointTable").get().addOnSuccessListener(s->{ArrayList<DocumentSnapshot>a=new ArrayList<>(s.getDocuments());Collections.sort(a,(x,y)->Double.compare(num(y,"points"),num(x,"points")));int r=1;for(DocumentSnapshot d:a)root.addView(tv((r++)+". "+safe(d.getString("playerName"))+" — "+num(d,"points")+" pts",17));if(a.isEmpty())root.addView(tv("কোনো point data নেই।",17));});back();}
    private void showLeaderboard(){base("🔥 Daily Earnings");db.collection("leaderboard").get().addOnSuccessListener(s->{ArrayList<DocumentSnapshot>a=new ArrayList<>(s.getDocuments());Collections.sort(a,(x,y)->Double.compare(num(y,"earnings"),num(x,"earnings")));int r=1;for(DocumentSnapshot d:a)root.addView(tv((r++)+". "+safe(d.getString("playerName"))+" — ৳"+num(d,"earnings"),17));if(a.isEmpty())root.addView(tv("আজকের leaderboard নেই।",17));});back();}
    private void showNotifications(){base("🔔 Notifications");Query q=db.collection("notifications").whereEqualTo("userId",uname());q.get().addOnSuccessListener(s->{for(DocumentSnapshot d:s)root.addView(tv("🔔 "+safe(d.getString("title"))+"\n"+safe(d.getString("body")),16));db.collection("notifications").whereEqualTo("userId","").get().addOnSuccessListener(all->{for(DocumentSnapshot d:all)root.addView(tv("📢 "+safe(d.getString("title"))+"\n"+safe(d.getString("body")),16));if(s.isEmpty()&&all.isEmpty())root.addView(tv("কোনো notification নেই।",17));});});back();}
    private void showProfile(){base("👤 Profile");db.collection("users").document(uname()).get().addOnSuccessListener(d->{root.addView(tv("Name: "+safe(d.getString("name")),18));root.addView(tv("Username: "+safe(d.getString("username")),18));root.addView(tv("Email: "+safe(d.getString("email")),18));root.addView(tv("Phone: "+safe(d.getString("phone")),18));root.addView(tv("Referral: "+safe(d.getString("referralCode")),18));root.addView(tv("Wallet: ৳"+num(d,"walletBalance"),18));});back();}

    private void showAdmin(){db.collection("users").document(uname()).get().addOnSuccessListener(d->{if(!"admin".equalsIgnoreCase(safe(d.getString("role")))){msg("Admin access required");return;}adminMenu();});}
    private void adminMenu(){base("⚙️ Admin Panel");String[]a={"➕ Create Tournament","📋 Manage Tournaments","🧾 Match Result + OCR","💳 Deposit / Withdraw","👥 User Management","🏆 Earnings Leaderboard","🔔 Send Notification"};for(String s:a){Button b=btn(s);root.addView(b);b.setOnClickListener(v->{if(s.contains("Create"))adminCreate();else if(s.contains("Manage"))adminManage();else if(s.contains("OCR"))adminOCR();else if(s.contains("Deposit"))adminTransactions();else if(s.contains("User"))adminUsers();else if(s.contains("Earnings"))adminLeaderboard();else adminNotification();});}back();}
    private void adminCreate(){base("➕ Create Tournament");EditText n=field("Tournament Name"),mode=field("Mode (Solo/Duo/Squad)"),map=field("Map"),start=field("Start Time"),fee=field("Entry Fee"),pr=field("Prize"),pk=field("Per Kill"),max=field("Max Players"),dev=field("Device"),pool=field("Prize Pool");Button b=btn("Publish");root.addView(b);b.setOnClickListener(v->{if(n.getText().toString().trim().isEmpty()){msg("Name দিন");return;}Map<String,Object>m=new HashMap<>();m.put("name",n.getText().toString().trim());m.put("mode",mode.getText().toString().trim());m.put("map",map.getText().toString().trim());m.put("startTime",start.getText().toString().trim());m.put("entryFee",parse(fee.getText().toString()));m.put("prize",parse(pr.getText().toString()));m.put("perKill",parse(pk.getText().toString()));m.put("maxPlayers",(long)parse(max.getText().toString()));m.put("joinedPlayers",0L);m.put("device",dev.getText().toString().trim());m.put("prizePool",parse(pool.getText().toString()));m.put("status","OPEN");m.put("createdAt",System.currentTimeMillis());db.collection("tournaments").add(m).addOnSuccessListener(x->{msg("Tournament published");adminMenu();});});back();}
    private void adminManage(){base("📋 Manage Tournaments");db.collection("tournaments").get().addOnSuccessListener(s->{for(DocumentSnapshot d:s){Button b=btn("✏️ "+safe(d.getString("name"))+" • "+safe(d.getString("status")));root.addView(b);b.setOnClickListener(v->adminEditTournament(d));}});back();}
    private void adminEditTournament(DocumentSnapshot d){base("⚙️ Match Control");EditText room=field("Room ID"),pw=field("Room Password"),status=field("Status: OPEN/MATCH_FOUND/LIVE/CLOSED");Button b=btn("Save Match Info");root.addView(b);b.setOnClickListener(v->{Map<String,Object>m=new HashMap<>();m.put("roomId",room.getText().toString().trim());m.put("roomPassword",pw.getText().toString().trim());m.put("status",status.getText().toString().trim().toUpperCase(Locale.US));d.getReference().update(m).addOnSuccessListener(x->{msg("Updated");adminMenu();});});back();}
    private void adminTransactions(){base("💳 Pending Requests");db.collection("transactions").whereEqualTo("status","PENDING").get().addOnSuccessListener(s->{if(s.isEmpty())root.addView(tv("Pending request নেই।",17));for(DocumentSnapshot d:s){Button b=btn(safe(d.getString("type"))+" ৳"+num(d,"amount")+" • "+safe(d.getString("method")));root.addView(b);b.setOnClickListener(v->approve(d));}});back();}
    private void approve(DocumentSnapshot d){db.collection("users").document(safe(d.getString("uid"))).get().addOnSuccessListener(u->{double a=num(d,"amount"),bal=num(u,"walletBalance");String type=safe(d.getString("type"));if("WITHDRAW".equals(type)&&bal<a){msg("Balance insufficient");return;}double nb=bal+("DEPOSIT".equals(type)?a:-a);u.getReference().update("walletBalance",nb);d.getReference().update("status","APPROVED").addOnSuccessListener(x->{Map<String,Object>n=new HashMap<>();n.put("userId",d.getString("uid"));n.put("title",type+" approved");n.put("body","Request of ৳"+a+" approved.");n.put("createdAt",System.currentTimeMillis());db.collection("notifications").add(n);adminTransactions();});});}
    private void adminUsers(){base("👥 Users");db.collection("users").get().addOnSuccessListener(s->{for(DocumentSnapshot d:s)root.addView(tv(safe(d.getString("username"))+" | "+safe(d.getString("email"))+" | ৳"+num(d,"walletBalance")+" | "+safe(d.getString("role")),15));});back();}
    private void adminLeaderboard(){base("🏆 Earnings");db.collection("leaderboard").get().addOnSuccessListener(s->{for(DocumentSnapshot d:s)root.addView(tv(safe(d.getString("playerName"))+" — ৳"+num(d,"earnings"),17));});back();}
    private void adminNotification(){base("🔔 Send Notification");EditText uid=field("User UID (blank = all)"),title=field("Title"),body=field("Message");Button b=btn("Send");root.addView(b);b.setOnClickListener(v->{Map<String,Object>m=new HashMap<>();m.put("userId",uid.getText().toString().trim());m.put("title",title.getText().toString().trim());m.put("body",body.getText().toString().trim());m.put("createdAt",System.currentTimeMillis());db.collection("notifications").add(m).addOnSuccessListener(x->{msg("Notification sent");adminMenu();});});back();}

    private void adminOCR(){base("🧾 Result OCR");EditText mid=field("Match ID");Button pick=btn("📷 Select Screenshot");root.addView(pick);TextView out=tv("Screenshot select করে OCR চালান।",15);root.addView(out);Button save=btn("Save OCR Result");root.addView(save);save.setEnabled(false);
        pick.setOnClickListener(v->{pendingMatchId=mid.getText().toString().trim();Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,9001);});
        save.setOnClickListener(v->{if(pendingImage==null||pendingMatchId.isEmpty()){msg("Match ID ও screenshot দিন");return;}try{TextRecognizer r=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);r.process(InputImage.fromFilePath(this,pendingImage)).addOnSuccessListener(x->{out.setText(x.getText());saveOCR(pendingMatchId,x.getText(),out);}).addOnFailureListener(e->out.setText("OCR failed: "+e.getMessage()));}catch(Exception e){out.setText(e.getMessage());}});
        back();
    }
    private void saveOCR(String match,String raw,TextView out){int count=0;for(String line:raw.split("\\n")){Matcher m=Pattern.compile("^(\\d{1,2})\\s+(.+?)\\s+(\\d{1,2})\\s*$").matcher(line.trim().replaceAll("\\s+"," "));if(m.find()){int r=(int)parse(m.group(1)),k=(int)parse(m.group(3));if(r>0&&r<=50&&k>=0&&k<=50){Map<String,Object>x=new HashMap<>();x.put("matchId",match);x.put("playerName",m.group(2).trim());x.put("rank",r);x.put("kills",k);x.put("totalPoints",placement(r)+k);x.put("createdAt",System.currentTimeMillis());db.collection("results").add(x);count++;}}}out.setText(out.getText()+"\\nSaved rows: "+count);}
    private int placement(int r){int[]p={0,12,9,8,7,6,5,4,3,2,1};return r>=1&&r<=10?p[r]:0;}
    @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(req==9001&&result==RESULT_OK&&data!=null){pendingImage=data.getData();msg("Screenshot selected");}}
    private Map<String,Object> tx(String type,double amount,String method){Map<String,Object>m=new HashMap<>();m.put("type",type);m.put("amount",amount);m.put("method",method);m.put("createdAt",System.currentTimeMillis());return m;}
}
