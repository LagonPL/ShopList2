package com.example.shoplist.shoplist;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Util {

    public static Util instance = null;

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    public static String TitleCustom(String itemText) {
        int Space = itemText.indexOf("\u2063");
        String Title = itemText.substring(0, Space);
        return Title;
    }
    public static void ToastMaker(Context context, String toast ){
        if (context == null)
            return;


        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();

    }


}
