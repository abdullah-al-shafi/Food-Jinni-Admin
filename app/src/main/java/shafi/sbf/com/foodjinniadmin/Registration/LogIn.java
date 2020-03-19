package shafi.sbf.com.foodjinniadmin.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import shafi.sbf.com.foodjinniadmin.MainActivity;
import shafi.sbf.com.foodjinniadmin.R;

public class LogIn extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText input_email,input_password;
    TextView btnSignup,btnForgotPass;
    LinearLayout login_activity;
    String emailm,passwordm;

    private FirebaseAuth auth;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //View
        btnLogin = (Button)findViewById(R.id.login_btn_login);
        input_email = (EditText)findViewById(R.id.login_email);
        input_password = (EditText)findViewById(R.id.login_password);
        btnSignup = (TextView)findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView)findViewById(R.id.login_btn_forgot_password);
        login_activity= (LinearLayout) findViewById(R.id.login_activity);

        emailm = input_email.getText().toString();
        passwordm = input_password.getText().toString();

        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        mProgress = new ProgressDialog(this);

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Check already session , if ok-> DashBoard
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(LogIn.this, MainActivity.class));
            finish();
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_btn_forgot_password)
        {
            startActivity(new Intent(LogIn.this,ForgotPassword.class));

        }
        else if(view.getId() == R.id.login_btn_signup)
        {
            startActivity(new Intent(LogIn.this,AreaSelection.class));

        }
        else if(view.getId() == R.id.login_btn_login)
        {
            mProgress.setMessage("Loading....");
            mProgress.show();
            loginUser(input_email.getText().toString(), input_password.getText().toString());
        }

    }

    private void loginUser(final String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            if(password.length() < 6)
                            {
                                Snackbar snackBar = Snackbar.make(login_activity,"Password length must be over 6",Snackbar.LENGTH_SHORT);
                                snackBar.show();
                                mProgress.dismiss();
                            }

                        }
                        else{
                            mProgress.dismiss();
                            Intent intent = new Intent(LogIn.this, MainActivity.class);
                            // startActivity(new Intent(LogIn.this,Home.class));
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        finish();
                    }
                });
    }
}
