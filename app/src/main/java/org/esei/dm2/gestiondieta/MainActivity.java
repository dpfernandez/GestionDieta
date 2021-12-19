package org.esei.dm2.gestiondieta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncherEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView( R.layout.activity_main );

        final EditText edUsername = this.findViewById( R.id.edUsernameLogin);
        final EditText edPassword = this.findViewById( R.id.edPasswordLogin);
        final Button btLogin = this.findViewById( R.id.btLogin );
        final Button btRegister = this.findViewById( R.id.btRegister );

        this.gestorDB = DBManager.getManager(this.getApplicationContext());

        // añadir alimentos por defecto si aún no existen
        if(!this.gestorDB.existeAlimento("manzana")) {
            this.gestorDB.insertaAlimento("manzana", 150, 77, true);
        }
        if(!this.gestorDB.existeAlimento("pollo")) {
            this.gestorDB.insertaAlimento("pollo", 200, 214, true);
        }
        if(!this.gestorDB.existeAlimento("arroz")) {
            this.gestorDB.insertaAlimento("arroz", 100, 129, true);
        }

        // añadir usuarios por defecto si aún no existen
        if(this.gestorDB.existeUsuario("a", "a") == -1) {
            this.gestorDB.insertaUsuario("a", "a", 1);
        }
        if(this.gestorDB.existeUsuario("u", "u") == -1) {
            this.gestorDB.insertaUsuario("u", "u", 0);
        }


        // Inserta
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzaFormUsuario("", "", false);
            }
        });

        // Esto se ejecuta cuando se vuelve de la pantalla FormUsuario
        ActivityResultContract<Intent, ActivityResult> contract =
                new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent retData = result.getData(); //se obtienen los datos de resultado
                        if (result.getResultCode() != Activity.RESULT_CANCELED) { // si no se salió con el botón de cancelar, se intentar leer datos
                            if (retData != null) {
                                String username = retData.getExtras().getString("username");
                                String password = retData.getExtras().getString("password");
                                int admin = retData.getExtras().getInt("admin");
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    if (MainActivity.this.gestorDB.insertaUsuario(username, password, admin)) {
                                        Toast.makeText(MainActivity.this, "Usuario creado correctamente.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Error, el usuario ya existe.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Error desconocido al registrarse usuario.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                };

        this.activityResultLauncherEdit = this.registerForActivityResult(contract, callback);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = edUsername.getText().toString();
                final String password = edPassword.getText().toString();

                int existe = MainActivity.this.gestorDB.existeUsuario(username, password);
                if(existe == 0) { // existe y no es admin
                    ( (App) MainActivity.this.getApplication() )
                            .setDatos(new DatosUsuario(username, false)); // guardar datos usuario
                    lanzaPerfilUsuario();
                } else if(existe == 1) { // existe y es admin
                    ( (App) MainActivity.this.getApplication() )
                            .setDatos(new DatosUsuario(username, true)); // guardar datos usuario
                    lanzaMenuAdmin();
                } else { // usuario no existe
                    Toast.makeText( MainActivity.this, "Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT ).show();
                }
            }
        });
        btLogin.setEnabled( false );
        btRegister.setEnabled( true );

        edUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btLogin.setEnabled( edUsername.getText().toString().trim().length() > 0 && edPassword.getText().toString().trim().length() > 0 );
            }
        });

        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btLogin.setEnabled( edUsername.getText().toString().trim().length() > 0 && edPassword.getText().toString().trim().length() > 0 );
            }
        });
    }

    @Override
    protected void onResume() { // siempre que se vuelva a la MainActivity (pantalla login), se eliminan los datos de la clase App
        super.onResume();
        ( (App) this.getApplication() ).logout();
    }

    private void lanzaFormUsuario(String username, String password, boolean admin)
    {
        Intent subActividad = new Intent( MainActivity.this, FormUsuario.class );

        subActividad.putExtra( "username", username );
        subActividad.putExtra( "password", password );
        subActividad.putExtra( "admin", admin );

        activityResultLauncherEdit.launch(subActividad);
    }

    private void lanzaPerfilUsuario() {
        Intent subActividad = new Intent( MainActivity.this, PerfilUsuario.class );

        MainActivity.this.startActivity(subActividad);
    }

    private void lanzaMenuAdmin() {
        Intent subActividad = new Intent( MainActivity.this, MenuAdmin.class );

        MainActivity.this.startActivity(subActividad);
    }

    private DBManager gestorDB;
}