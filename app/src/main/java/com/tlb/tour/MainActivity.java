private void showMyMatches(){
    base("🎮 My Matches");

    db.collection("entries")
        .whereEqualTo("uid", uname())
        .get()
        .addOnSuccessListener(s -> {

            if(s.isEmpty()){
                root.addView(tv("কোনো joined match নেই।",17));
                return;
            }

            for(DocumentSnapshot d : s){

                String tid = safe(d.getString("tournamentId"));

                final String[] info = {
                    "🎮 " + safe(d.getString("tournamentName"))
                    + "\nEntry: ৳" + num(d,"entryFee")
                    + "\nStatus: " + safe(d.getString("status"))
                };

                db.collection("tournaments")
                    .document(tid)
                    .get()
                    .addOnSuccessListener(t -> {

                        String st = safe(t.getString("status"));

                        if("MATCH_FOUND".equalsIgnoreCase(st)
                                || "LIVE".equalsIgnoreCase(st)){

                            info[0] += "\nRoom ID: "
                                    + safe(t.getString("roomId"))
                                    + "\nRoom Password: "
                                    + safe(t.getString("roomPassword"));
                        }

                        root.addView(tv(info[0],17));
                    });
            }
        });

    back();
}
