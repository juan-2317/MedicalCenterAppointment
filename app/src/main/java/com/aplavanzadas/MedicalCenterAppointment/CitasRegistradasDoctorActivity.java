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

public class CitasRegistradasDoctorActivity extends AppCompatActivity {

    private static final String TAG = "CitasRegistradasDoctor";

    private Bundle datosSesion;

    private TextView doctorSession2;

    private ListView listaCitasRegistradas;
    private ArrayList<CitaItem> lista;
    private AdapterCita adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_registradas_doctor);

        // Crear barra de herramientas personalizada
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_registered);
        setSupportActionBar(toolbar);

        // Recuperar variables de sesión del usuario
        datosSesion = getIntent().getExtras();

        String nombres = datosSesion.getString("nombres");
        String apellidos = datosSesion.getString("apellidos");

        // Mostrar nombre del usuario que inició sesión en la actividad
        doctorSession2 = (TextView) findViewById(R.id.doctorSession2);
        doctorSession2.append(" " + nombres + " " + apellidos);

        // Recuperar el listview del layout y asociarlo con su adaptador
        listaCitasRegistradas = (ListView) findViewById(R.id.lv_registered_appointments);
        lista = new ArrayList<>();
        adaptador = new AdapterCita(this, lista);

        // Solicitar al webservice en php los datos de las citas disponibles
        new ListarCitasRegistradas()
                .execute(MainActivity.URL_SERVIDOR + "listar_citas_disponibles.php");
    }

    private class ListarCitasRegistradas extends HttpGetAsyncTask {

        private ListarCitasRegistradas() {
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
                listaCitasRegistradas.setAdapter(adaptador);
            } catch (JSONException e) {
                Log.e(TAG, "Error al listar las citas registradas", e);
            }
        }
    }
}
