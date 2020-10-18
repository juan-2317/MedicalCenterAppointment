package com.aplavanzadas.medicalcenterappointment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";

    private EditText tvFirstName;
    private EditText tvSurname;
    private EditText tvUserName;
    private EditText tvEmail;
    private EditText tvPassword;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        tvFirstName = (EditText) findViewById(R.id.tv_first_name);
        tvSurname = (EditText) findViewById(R.id.tv_surname);
        tvUserName = (EditText) findViewById(R.id.tv_user_name);
        tvEmail = (EditText) findViewById(R.id.tv_email);
        tvPassword = (EditText) findViewById(R.id.tv_password);

        btnCreate = (Button) findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerRegistro();
            }
        });
    }

    public void obtenerRegistro() {
        String fistName = tvFirstName.getText().toString();
        String surname = tvSurname.getText().toString();
        String userName = tvUserName.getText().toString();
        String password = tvPassword.getText().toString();
        String email = tvEmail.getText().toString();

        Map<String, String> parametros = new LinkedHashMap<>();
        parametros.put("nombres", fistName);
        parametros.put("apellidos", surname);
        parametros.put("nom_usuario", userName);
        parametros.put("clave", password);
        parametros.put("correo", email);

        // Solicitar al webservice en php los datos de login de usuario o doctor
        new RegistrarUsuario(parametros).execute(MainActivity.URL_SERVIDOR + "registrar.php");
    }

    private class RegistrarUsuario extends HttpPostAsyncTask {

        private RegistrarUsuario(Map<String, String> postData) {
            super(postData);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            Toast.makeText(RegistroActivity.this, R.string.save_success, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
