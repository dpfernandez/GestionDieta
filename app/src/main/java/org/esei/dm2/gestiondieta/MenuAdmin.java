package org.esei.dm2.gestiondieta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView( R.layout.activity_admin_menu);

        final Button btUsuarios = this.findViewById( R.id.btUsuarios );
        final Button btAlimentos = this.findViewById( R.id.btAlimentos );
        final Button btPerfil = this.findViewById( R.id.btPerfilAdmin );

        datos = ( (App) this.getApplication() ).getDatos();

        btUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzaListaUsuarios();
            }
        });

        btAlimentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzaListaAlimentos();
            }
        });

        btPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzaPerfilUsuario(datos.getUsername());
            }
        });
    }

    private void lanzaListaUsuarios() {
        MenuAdmin.this.startActivity(new Intent(MenuAdmin.this, ListaUsuarios.class));
    }

    private void lanzaListaAlimentos() {
        MenuAdmin.this.startActivity(new Intent(MenuAdmin.this, ListaAlimentos.class));
    }
    private void lanzaPerfilUsuario(String username)
    {
        Intent subActividad = new Intent( MenuAdmin.this, PerfilUsuario.class );

        MenuAdmin.this.startActivity(subActividad);
    }

    private DatosUsuario datos;
}