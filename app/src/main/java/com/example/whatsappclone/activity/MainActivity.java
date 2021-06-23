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
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorSecondaryVariant)
                .fragment(R.layout.material_intro_layout_1, R.style.Theme_AppCompat_Light_NoActionBar)
                .canGoBackward(false)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorSecondaryVariant)
                .fragment(R.layout.material_intro_layout_2, R.style.Theme_AppCompat_Light_NoActionBar)
                .canGoForward(false)
                .build());

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