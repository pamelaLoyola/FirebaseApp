package com.loyola.firebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private View loginPanel;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressbar);
        loginPanel = findViewById(R.id.login_panel);

        //Iniciar Firebase Auth

        initFireBaseAuth();

        //Iniciar FirebaseAuthStateListener

        initFirebaseAuthStateListener();

        //Iniciar con Google
        initGoogleSignIn();

    }

    /**
     * Firebase Auth
     */

    private FirebaseAuth auth;

    private EditText emailInput;
    private EditText passInput;

    private void initFireBaseAuth() {
        //Inicializa la instancia de Firebase

        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email_input);
        passInput = findViewById(R.id.password_input);
    }

    public void callLogin(View view) {

        String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        loginPanel.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //Ingresando con el usuario

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "SignInWithEmailAndPassword:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    loginPanel.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "SignInWithEmailAndPassword:failed:" + task.getException());
                    Toast.makeText(LoginActivity.this, "Usuario o Contrasela Inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void callRegister(View view) {

        String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Use una contraseña más larga", Toast.LENGTH_SHORT).show();
            return;
        }

        loginPanel.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmailAndPassword:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmailAndPassword:failed:", task.getException());
                    Toast.makeText(LoginActivity.this, "Error en el Registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Firebase AuthStateListener
     */

    private FirebaseAuth.AuthStateListener authListener;

    private void initFirebaseAuthStateListener() {
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //El usuario SI se encuentra logueado


                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    //Ir al Main Activity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    //EL usuario NO se encuentra logueado

                    Log.d(TAG, "onAuthStateChanged:singed_out:");
                }
            }
        };
    }

    /**
     * Inicio de Sesion con google
     */

    private static final int GOOGLE_SIGNIN_REQUEST = 1000;
    private GoogleApiClient apiGoogle;

    private void initGoogleSignIn() {
        //Configuramos el boton

        SignInButton googleButton = findViewById(R.id.sign_in_button);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPanel.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                //Boton de Inicio con Google
                Intent singInIntent = Auth.GoogleSignInApi.getSignInIntent(apiGoogle);
                startActivityForResult(singInIntent, GOOGLE_SIGNIN_REQUEST);
            }
        });


        //Configuracion del ingreso para obtener como respuesta el perfil básico
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken("918588681602-q4dcmu33sgqbkh6i7fihgsivfnccrlld.apps.googleusercontent.com")
                .requestEmail()
                .build();

        apiGoogle = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG,"OnConcectionFailed " + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authListener != null){
            auth.removeAuthStateListener(authListener);
        }
    }
}

