package com.example.rushikesh.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rushikesh.quiz.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText edtNewUser,edtNewPassword,edtNewEmail; //for signup
    EditText edtUser,edtPassword; //for signin

    Button btnSignUp,btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase

        database=FirebaseDatabase.getInstance();
        users=database.getReference("Users");

        edtUser=findViewById(R.id.edtUser);
        edtPassword=findViewById(R.id.edtPassword);
        btnSignIn=findViewById(R.id.btn_sign_in);
        btnSignUp=findViewById(R.id.btn_sign_up);

        //Listner

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUser.getText().toString(),edtPassword.getText().toString());
            }
        });

    }

    private void signIn(final String user, final String pwd) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user).exists()){
                    if(!user.isEmpty()) {
                        User login = dataSnapshot.child(user).getValue(User.class);
                        assert login != null;
                        if (login.getPassword().equals(pwd))
                            Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                        else
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                    }
                        else{
                        Toast.makeText(MainActivity.this,"Please enter user Username",Toast.LENGTH_SHORT).show();
                    }
                    }
                else {
                    Toast.makeText(MainActivity.this,"User Name does not Exist !",Toast.LENGTH_SHORT).show();
                }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showSignUpDialog() {
        Context context;
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full Information");

        LayoutInflater inflater=this.getLayoutInflater();
        View sign_up_layout=inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser=sign_up_layout.findViewById(R.id.edtNewUserName);
        edtNewEmail=sign_up_layout.findViewById(R.id.edtNewEmail);
        edtNewPassword=sign_up_layout.findViewById(R.id.edtNewPassword);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_launcher_foreground);

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final User user=new User(edtNewUser.getText().toString(),
                        edtNewPassword.getText().toString(),
                        edtNewEmail.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user.getUserName()).exists())
                            Toast.makeText(MainActivity.this,"User already Exists",Toast.LENGTH_SHORT).show();
                        else {
                            users.child(user.getUserName()).setValue(user);
                            Toast.makeText(MainActivity.this,"User Registration Successful",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
