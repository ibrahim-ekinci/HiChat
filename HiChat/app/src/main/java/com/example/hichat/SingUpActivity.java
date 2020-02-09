package com.example.hichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SingUpActivity extends AppCompatActivity {
    EditText emailText,passwordText;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        emailText = findViewById(R.id.editText_user_email);
        passwordText = findViewById(R.id.editText_password);
        mAuth=FirebaseAuth.getInstance();//Firabase sınıfından yeni bir obja yaratıp atıyoruz.


        //daha önce giriş yapmış kullanıcı olup olmadıgını kontrol edişyoruz.
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(getApplicationContext(),ChatActivity.class);//yeni sayfaya yönlendirmek için...
            startActivity(intent);
        }
    }
    public void  signUp(View view){

        mAuth.createUserWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                              // FirebaseUser user =  mAuth.getCurrentUser();//güncel kullanıcı giriş yapmış kullanıyıcıy getyir.
                              // String userEmail= user.getEmail().toString();
                              // System.out.println("user email :"+userEmail);

                              //intent kaydolduktan sonra yapılacaklar....

                            Intent intent = new Intent(getApplicationContext(),ChatActivity.class);//yeni sayfaya yönlendirmek için...
                            startActivity(intent);//yönlendiriyoruz...

                        }else{
                            Toast.makeText(SingUpActivity.this,"Failed", Toast.LENGTH_LONG).show();//Başarısız kayıt için bilgilendirme yapar.
                        }
                    }
                });

    }
    public void  signIn(View view){

        mAuth.signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(),ChatActivity.class);//yeni sayfaya yönlendirmek için...
                            startActivity(intent);
                        }else{
                            Toast.makeText(SingUpActivity.this,"Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
