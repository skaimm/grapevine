package com.grapevine.grapevine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grapevine.grapevine.Utils.ConnectionClass;
import com.grapevine.grapevine.Utils.PreferenceUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginRegisterActivity extends AppCompatActivity {

    EditText email,pass;
    Button register,login;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        if(PreferenceUtils.getEmail(this) != null){
            if(!PreferenceUtils.getEmail(this).equals("")){
                Intent intent = new Intent(LoginRegisterActivity.this,MainActivity.class);
                int user = PreferenceUtils.getUserID(this);
                intent.putExtra("userid",user);
                startActivity(intent);
            }
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Doregister doregister = new Doregister();
                doregister.execute("");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dologin dologin = new Dologin();
                dologin.execute("");
            }
        });
    }

    public class Doregister extends AsyncTask<String,String,String>
    {

        String emailstr = email.getText().toString();
        String passstr = pass.getText().toString();
        String typeofUser = "user";
        String notification = "";
        boolean isSuccess = false;
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            if(emailstr.trim().equals("")|| passstr.trim().equals("")){
                notification = "Please enter all fields ";
            }else{
                try{

                    Connection con = connectionClass.CONN();
                    if(con==null){
                        notification="Please check your Internet Connection";
                    }else{
                        String query = "INSERT INTO person(email,password,type) VALUES ('"+emailstr+"','"+passstr+"','"+typeofUser+"')";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        String query2 = "SELECT * FROM person WHERE email ='"+emailstr+"' and password ='"+passstr+"'";

                        Statement stmt2 = con.createStatement();
                        //stmt.executeUpdate(query);
                        ResultSet rs2 = stmt2.executeQuery(query2);

                        while (rs2.next()){

                            int userid = rs2.getInt(1);
                            String emaildb = rs2.getString(2);
                            String passdb = rs2.getString(3);


                            if(emaildb.equals(emailstr) && passdb.equals(passstr)){

                                PreferenceUtils.saveEmail(emaildb,getApplicationContext());
                                PreferenceUtils.savePassword(passdb,getApplicationContext());
                                PreferenceUtils.saveUserID(userid,getApplicationContext());
                                notification="Registration is succesfull";
                                isSuccess=true;
                            }
                        }
                    }

                }catch (SQLException e) {
                    e.printStackTrace();
                    isSuccess=false;
                    notification = "Exception "+e;
                    if(notification.equals("Exception com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry 'asdf' for key 'email'")){
                        notification = "E-mail already exist.";
                    }

                }
            }
            return notification;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+notification,Toast.LENGTH_LONG).show();

            if(isSuccess){
                startActivity(new Intent(LoginRegisterActivity.this,MainActivity.class));
            }

            progressDialog.cancel();
        }
    }

    public class Dologin extends AsyncTask<String,String,String>
    {

        String emailstr = email.getText().toString();
        String passstr = pass.getText().toString();
        String notification = "";

        int userid;

        String emaildb,passdb;

        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            boolean connected = false;

            if(emailstr.trim().equals("")|| passstr.trim().equals("")){
                notification = "Please enter all fields ";
            }else{
                try{
                    Connection con = connectionClass.CONN();
                    if(con==null){
                        notification="Please check your Internet Connection";
                    }else{
                        connected = true;

                        String query = "SELECT * FROM person WHERE email ='"+emailstr+"' and password ='"+passstr+"'";

                        Statement stmt = con.createStatement();
                        //stmt.executeUpdate(query);
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()){

                            userid = rs.getInt(1);
                            emaildb = rs.getString(2);
                            passdb = rs.getString(3);


                            if(emaildb.equals(emailstr) && passdb.equals(passstr)){

                                PreferenceUtils.saveEmail(emaildb,getApplicationContext());
                                PreferenceUtils.savePassword(passdb,getApplicationContext());
                                PreferenceUtils.saveUserID(userid,getApplicationContext());
                                isSuccess = true;
                                notification = "Login Succesfull";

                            }
                        }
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                    isSuccess=false;
                    notification = "Exception"+e;
                }
                if(!isSuccess && connected){
                    notification = "Check your Email or Password";
                }
            }
            return notification;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+notification,Toast.LENGTH_LONG).show();

            if(isSuccess){
                Intent intent = new Intent(LoginRegisterActivity.this,MainActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }

            progressDialog.cancel();
        }
    }
}
