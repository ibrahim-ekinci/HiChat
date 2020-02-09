package com.example.hichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    EditText messageText;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private ArrayList<String> chatMessages =new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menümüzü bağlıyoruz...
        MenuInflater menuInflater =getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //menüden seçimlernde neyapıcagını ayarlıyoruz....
        if (item.getItemId()==R.id.options_menu_sign_out){
            Intent intent =new Intent(getApplicationContext(),SingUpActivity.class);
            startActivity(intent);
            mAuth.signOut();

        } else if ( item.getItemId()==R.id.options_menu_profile){
            Intent intent =new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

       // chatMessages.add("sss");
       // chatMessages.add("ssss");

        messageText = findViewById(R.id.chat_activity_message_text);
        //recycler işlemleri...
        recyclerView=findViewById(R.id.recycler_view);
        recyclerViewAdapter = new RecyclerViewAdapter(chatMessages);

        RecyclerView.LayoutManager recyclerViewManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewManager);//ayarlar için... kaydırma vs vs.
        recyclerView.setItemAnimator(new DefaultItemAnimator());//animasyonlar için...

        recyclerView.setAdapter(recyclerViewAdapter);

        mAuth=FirebaseAuth.getInstance();

        //data base
        database=FirebaseDatabase.getInstance();
        databaseReference = database.getReference();//serverda saklanan verilere ulaşabilmek için kullanıyoruz.

        getData();
    }
    public void sendMessage(View view){
        String messageToSend = messageText.getText().toString();
        UUID uuid = UUID.randomUUID();
        String uuidString =uuid.toString();

        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail().toString();
        String userID =user.getUid().toString();
        databaseReference.child("Chats").child(uuidString).child("usermessage").setValue(messageToSend);
        databaseReference.child("Chats").child(uuidString).child("useremail").setValue(userEmail);
        databaseReference.child("Chats").child(uuidString).child("userid").setValue(userID);
        databaseReference.child("Chats").child(uuidString).child("usermessagetime").setValue(ServerValue.TIMESTAMP);

        messageText.setText("");

        getData();
    }

    public void getData(){
        DatabaseReference newReference = database.getReference("Chats");

        Query query = newReference.orderByChild("usermessagetime");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //System.out.println("dataSnapshot Childeren:"+dataSnapshot.getChildren());
                //System.out.println("dataSnapshot Value:"+dataSnapshot.getValue());
                //System.out.println("dataSnapshot Key:"+dataSnapshot.getKey());
                chatMessages.clear();
                for (DataSnapshot ds :dataSnapshot.getChildren()){
                    HashMap<String,String> hashMap =(HashMap<String, String>) ds.getValue();
                    String useremail = hashMap.get("useremail");
                    String usermessage = hashMap.get("usermessage");
                    String userid = hashMap.get("userid");

                    chatMessages.add(useremail+": "+usermessage);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {//db ye bağlanmada sıkıntı olursa...
                Toast.makeText(getApplicationContext(),databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
