package com.aplavanzadas.medicalcenterappointment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PerfilMedicoActivity extends AppCompatActivity {

    private static final String TAG = "PerfilMedicoActivity";

    private Bundle datosSesion;

    private TextView doctorSession;

    private Spinner listaLugares;
    private Spinner listaTipoCitas;
    private Spinner listaConsultorios;

    private EditText etFecha;
    private EditText etHora;

    private Button btnCita;

    private Calendar calendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_medico);

        // Crear barra de herramientas personalizada
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_doctor);
        setSupportActionBar(toolbar);

        // Recuperar variables de sesión del usuario
        datosSesion = getIntent().getExtras();

        String nombres = datosSesion.getString("nombres");
        String apellidos = datosSesion.getString("apellidos");

        // Mostrar nombre del usuario que inició sesión en la actividad
        doctorSession = (TextView) findViewById(R.id.doctorSession);
        doctorSession.append(" " + nombres + " " + apellidos);

        LugarItem[] lugares = new LugarItem[3];
        lugares[0] = new LugarItem("La castellana", "Cll 83 N° 33 - 30");
        lugares[1] = new LugarItem("Chapinero", "Cll 68 N° 13 - 54");
        lugares[2] = new LugarItem("Quiroga", "Cll 38 sur N° 12 - 30");

        // Recuperar el listview del layout y asociarlo con su adaptador
        listaLugares = (Spinner) findViewById(R.id.list_places);
        listaLugares.setAdapter(new AdapterLugar(this, lugares));

        /* listaLugares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // Este método se ejecutará cuando se seleccione elemento del spinner
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Toast.makeText(adapterView.getContext(),
                        ((LugarItem) adapterView.getItemAtPosition(pos)).getSubtitulo(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }); */

        String[] especialidad = {getString(R.string.specialty)};
        listaTipoCitas = (Spinner) findViewById(R.id.list_type_appointments);
        ArrayAdapter<String> adapterTipoCita = new ArrayAdapter<>(this, R.layout.simple_spinner, especialidad);
        // Establecer la apariencia que tendrá el item cuando se despliege la lista
        adapterTipoCita.setDropDownViewResource(android.R.layout.simple_spinner_item);
        listaTipoCitas.setAdapter(adapterTipoCita);

        /* listaTipoCitas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // Este método se ejecutará cuando se seleccione elemento del spinner
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Toast.makeText(adapterView.getContext(),
                        (String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }); */

        String[] consultorios = {"303", "205", "403"};
        listaConsultorios = (Spinner) findViewById(R.id.list_offices);
        ArrayAdapter adapterConsultorios = new ArrayAdapter<>(this, R.layout.simple_spinner, consultorios);
        // Establecer la apariencia que tendrá el item cuando se despliege la lista
        adapterConsultorios.setDropDownViewResource(android.R.layout.simple_spinner_item);
        listaConsultorios.setAdapter(adapterConsultorios);

        /* listaConsultorios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // Este método se ejecutará cuando se seleccione elemento del spinner
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Toast.makeText(adapterView.getContext(),
                        (String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }); */

        calendario = Calendar.getInstance();
        etFecha = (EditText) findViewById(R.id.etFecha);
        etHora = (EditText) findViewById(R.id.etHora);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH, monthOfYear);
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                actualizarCampoFecha();
            }

        };

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(PerfilMedicoActivity.this, date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener hour = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                calendario.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendario.set(Calendar.MINUTE, minute);
                actualizarCampoHora(hourOfDay, minute);
            }

        };

        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(PerfilMedicoActivity.this, hour, calendario.get(Calendar.HOUR_OF_DAY),
                        calendario.get(Calendar.MINUTE), false).show();
            }
        });

        btnCita = (Button) findViewById(R.id.btn_create_appointment);
        btnCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerRegistro();
            }
        });
    }

    public void obtenerRegistro() {
        LugarItem sitio = (LugarItem) listaLugares.getSelectedItem();

        String nombres = datosSesion.getString("nombres");
        String lugar = sitio.getSubtitulo();
        String direccion = sitio.getDireccion();
        String tipoCita = (String) listaTipoCitas.getSelectedItem();
        String consultorio = (String) listaConsultorios.getSelectedItem();
        String fecha = etFecha.getText().toString();
        String hora = etHora.getText().toString() + ":00";
        String codigoDoctor = Integer.toString(datosSesion.getInt("codigo_doctor"));

        Map<String, String> parametros = new LinkedHashMap<>();
        parametros.put("nom_usu", nombres);
        parametros.put("lugar", lugar);
        parametros.put("direccion", direccion);
        parametros.put("tipo_cita", tipoCita);
        parametros.put("consultorio", consultorio);
        parametros.put("fecha", fecha);
        parametros.put("hora", hora);
        parametros.put("codigo_doctor", codigoDoctor);

        // Enviar al webservice en php los datos de la cita creada
        new CrearCita(parametros).execute(MainActivity.URL_SERVIDOR + "crear_cita.php");
    }

    private class CrearCita extends HttpPostAsyncTask {

        private CrearCita(Map<String, String> postData) {
            super(postData);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            Toast.makeText(PerfilMedicoActivity.this, R.string.save_success, Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarCampoFecha() {
        String formatoDeFecha = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha, Locale.US);

        etFecha.setText(sdf.format(calendario.getTime()));
    }

    private void actualizarCampoHora(int hourOfDay, int minute) {
        etHora.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
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

                Intent intent = new Intent(PerfilMedicoActivity.this, MainActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_doctor, miMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcionMenu) {
        int id = opcionMenu.getItemId();

        if (id == R.id.salir_doctor) {
            confirmarCierre();
            return true;
        } else if (id == R.id.ver_citas_registradas) {
            Intent intent = new Intent(this, CitasRegistradasDoctorActivity.class);
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
