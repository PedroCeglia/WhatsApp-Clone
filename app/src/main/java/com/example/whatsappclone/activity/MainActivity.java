package com.example.whatsappclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activity.loginEcadastro.CadastroActivity;
import com.example.whatsappclone.activity.loginEcadastro.LoginActivity;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    public void verificarUsuarioLogado(){
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (auth.getCurrentUser() == null){
            Log.i("erro", "testando");
        }else{abrirTelaPrincipal();}
    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(this, TelaPrincipalActivity.class);
        startActivity( intent );
    }
    public void abrirLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity( intent );
    }
    public void abrirCadastro(View view){
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity( intent );
    }
}