package com.aplavanzadas.medicalcenterappointment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private static final String TAG = "PerfilUsuarioActivity";

    private Bundle datosSesion;

    private Toolbar toolbar;
    private TextView userSession;
    private SearchView buscador;

    private ListView listaBusqueda;
    private ArrayList<CitaItem> lista;
    private AdapterCita adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        // Recuperar variables de sesión del usuario
        datosSesion = getIntent().getExtras();

        String nombres = datosSesion.getString("nombres");
        String apellidos = datosSesion.getString("apellidos");

        // Crear barra de herramientas personalizada
        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        // Mostrar nombre del usuario que inició sesión en la actividad
        userSession = (TextView) findViewById(R.id.userSession);
        userSession.append(" " + nombres + " " + apellidos);

        // Recuperar el listview del layout y asociarlo con su adaptador
        lista = new ArrayList<>();
        /* lista.add(new CitaItem("Juan Pepe Pérez", "Medicina General", "La castellana", "Cll 83 N° 33 - 20", 303, "2018-11-20", "7:00 a.m."));
        lista.add(new CitaItem("Sarah Daniela González", "Medicina General", "Chapinero", "Cll 68 N° 13 - 54", 205, "2018-12-03", "9:00 a.m."));
        lista.add(new CitaItem("Daniel Quintana", "Medicina General", "Quiroga", "Cll 38 sur N° 12 - 30", 403, "2018-12-15", "10:00 a.m.")); */

        listaBusqueda = (ListView) findViewById(R.id.lv_busqueda);
        adaptador = new AdapterCita(this, lista);

        listaBusqueda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ((CitaItem) adapterView.getItemAtPosition(i)).getId();
                obtenerRegistro(i);
            }
        });

        buscador = (SearchView) findViewById(R.id.seeker);

        // Personalizar visualmente el cuadro de búsqueda
        int id = buscador.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) buscador.findViewById(id);

        textView.setHintTextColor(Color.parseColor("#BFC9CA"));
        textView.setTextColor(Color.WHITE);

        textView.setHint(R.string.search_info);

        // Activando el filtro de búsqueda
        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // Este método se ejecutará cada vez que el usuario escriba una letra en el campo de búsqueda
            @Override
            public boolean onQueryTextChange(String query) {
                adaptador.getFilter().filter(query);
                return false;
            }
        });

        // Solicitar al webservice en php los datos de las citas disponibles
        new ListarCitasDisponibles().execute(MainActivity.URL_SERVIDOR + "listar_citas_disponibles.php");
    }

    private class ListarCitasDisponibles extends HttpGetAsyncTask {

        private ListarCitasDisponibles() {
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
                listaBusqueda.setAdapter(adaptador);
            } catch (JSONException e) {
                Log.e(TAG, "Error al listar las citas disponibles", e);
            }
        }
    }

    public void obtenerRegistro(final int indice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_schedule);

        // Añadir botones
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                registrarCitaConfirmada(indice);
            }
        });

        // Mostrar alertDialog al usuario
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void registrarCitaConfirmada(int indice) {
        String codigoUsuario = Integer.toString(datosSesion.getInt("codigo_usuario"));
        String codigoCita = Integer.toString(lista.get(indice).getId());

        // Enviar al webservice en php los datos de la cita creada
        Map<String, String> parametros = new LinkedHashMap<>();
        parametros.put("codigo_usuario", codigoUsuario);
        parametros.put("codigo_cita", codigoCita);

        new AgendarCita(parametros).execute(MainActivity.URL_SERVIDOR + "agendar_cita.php");

        // Remover cita de la lista
        lista.remove(indice);
        adaptador.notifyDataSetChanged();
    }

    private class AgendarCita extends HttpPostAsyncTask {

        private AgendarCita(Map<String, String> postData) {
            super(postData);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            Toast.makeText(PerfilUsuarioActivity.this, getString(R.string.save_success), Toast.LENGTH_LONG).show();
        }
    }

    public void confirmarCierre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_log_out);

        // Añadir botones
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Destruimos las variables de sesión por seguridad
                datosSesion.clear();

                Intent intent = new Intent(PerfilUsuarioActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Mostrar alertDialog al usuario
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu miMenu) {
        getMenuInflater().inflate(R.menu.menu_usuario, miMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcionMenu) {
        int id = opcionMenu.getItemId();

        if (id == R.id.salir_usuario) {
            confirmarCierre();
            return true;
        } else if (id == R.id.ver_citas_agendadas) {
            Intent intent = new Intent(this, CitasAgendadasUsuarioActivity.class);
            intent.putExtras(datosSesion);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(opcionMenu);
    }

    @Override
    public void onBackPressed() {
        confirmarCierre();
    }
}
