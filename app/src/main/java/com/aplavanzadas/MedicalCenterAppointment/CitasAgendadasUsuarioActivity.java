package com.aplavanzadas.medicalcenterappointment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CitasAgendadasUsuarioActivity extends AppCompatActivity {

    private static final String TAG = "CitasAgendadasUsuario";

    private Bundle datosSesion;

    private TextView userSession2;

    private ListView listaCitasAgendadas;
    private ArrayList<CitaItem> lista;
    private AdapterCita adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_agendadas_usuario);

        // Crear barra de herramientas personalizada
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_scheduled);
        setSupportActionBar(toolbar);

        // Recuperar variables de sesión del usuario
        datosSesion = getIntent().getExtras();

        int codigoUsuario = datosSesion.getInt("codigo_usuario");
        String nombres = datosSesion.getString("nombres");
        String apellidos = datosSesion.getString("apellidos");

        // Mostrar nombre del usuario que inició sesión en la actividad
        userSession2 = (TextView) findViewById(R.id.userSession2);
        userSession2.append(" " + nombres + " " + apellidos);

        // Recuperar el listview del layout y asociarlo con su adaptador
        listaCitasAgendadas = (ListView) findViewById(R.id.lv_scheduled_appointments);
        lista = new ArrayList<>();
        adaptador = new AdapterCita(this, lista);

        // URL Genymotion: http://10.0.3.2/
        // URL emulador android studio: http://10.0.2.2/
        // Solicitar al webservice en php los datos de las citas agendadas
        new ListarCitasAgendadas()
                .execute(MainActivity.URL_SERVIDOR + "listar_citas_agendadas.php?codigo_usuario=" + codigoUsuario);
    }

    private class ListarCitasAgendadas extends HttpGetAsyncTask {

        private ListarCitasAgendadas() {
            super();
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Log.d(TAG, result);

            try {
                JSONArray ja = new JSONArray(result);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject obj = ja.getJSONObject(i);

                    // Se mostrará en el listView el objeto de tipo CitaItem que se guarde en el ArrayList
                    CitaItem datos = new CitaItem(obj.getInt("codigo_cita"), obj.getString("nombre_doctor"),
                            obj.getString("lugar"), obj.getString("direccion"), obj.getString("tipo_cita"),
                            obj.getInt("consultorio"), obj.getString("fecha"), obj.getString("hora"));
                    lista.add(datos);
                }

                // Lo aplico
                listaCitasAgendadas.setAdapter(adaptador);
            } catch (JSONException e) {
                Log.e(TAG, "Error al listar las citas agendadas", e);
            }
        }
    }
}
