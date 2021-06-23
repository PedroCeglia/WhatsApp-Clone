package com.example.whatsappclone.activity.grupo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activity.ChatActivity;
import com.example.whatsappclone.adapter.GrupoSelecionadoAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.models.Grupo;
import com.example.whatsappclone.models.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private TextView textTotalParticipantes;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private RecyclerView recyclerMembrosSelecionados;
    private CircleImageView civGrupo;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String identificadorGrupo;
    private Grupo grupo;
    private EditText etNomeGrupo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criando_grupo);


        //Configuracoes iniciais
        textTotalParticipantes = findViewById(R.id.tvTotalParticipantes);
        recyclerMembrosSelecionados = findViewById(R.id.rvMembrosGrupoCadastro);
        civGrupo = findViewById(R.id.civFotoGrupo);
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorGrupo = UsuarioFirebase.getIdentificadorUsuario();
        grupo = new Grupo();
        etNomeGrupo = findViewById(R.id.etNomeDoGrupo);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabGrupoCadastro);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeGrupo = etNomeGrupo.getText().toString();

                listaMembrosSelecionados.add(UsuarioFirebase.getDadosUsuarioLogado());
                grupo.setMembros( listaMembrosSelecionados );
                grupo.setNome(nomeGrupo);

                grupo.salvar();

                finish();

                Intent i = new Intent(CadastroGrupoActivity.this, ChatActivity.class);
                i.putExtra("chatGrupo", grupo );
                startActivity( i );
            }
        });

        // OnClick Escolhendo Imagem
        civGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if ( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA );
                }else{startActivityForResult(i, SELECAO_GALERIA );}
            }
        });

        //Recuperar lista de membros passada
        if( getIntent().getExtras() != null ){
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listaMembrosSelecionados.addAll( membros );

            textTotalParticipantes.setText( "Participantes: " + listaMembrosSelecionados.size() + " + Você");

        }

        //Configurar recyclerview
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados, getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter( grupoSelecionadoAdapter );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Bitmap imagem = null;

            try {

                switch ( requestCode ){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada );
                        break;
                }

                if ( imagem != null ){

                    civGrupo.setImageBitmap( imagem );

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("grupos")
                            .child(grupo.getId() + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroGrupoActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CadastroGrupoActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                            imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //atualizaFotoUsuario( uri );
                                    grupo.setFoto(uri.toString());
                                }
                            });
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public void abrirGrupoActivity(View view){
        //Intent intent = new Intent(this, GrupoActivity.class);
        //startActivity( intent );
        finish();
    }

}

