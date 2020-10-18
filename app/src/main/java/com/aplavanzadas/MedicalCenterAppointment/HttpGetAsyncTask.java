package com.aplavanzadas.medicalcenterappointment;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/* Los parámetros genéricos de esta clase son:
    Params: El tipo de parámetros a enviar a la tarea en ejecución
    Progress: El tipo de unidades utilizadas durante el calculo de fondo
    Result: El tipo del resultado de la tarea en segundo plano */
public class HttpGetAsyncTask extends AsyncTask<String, Void, String> {

    public HttpGetAsyncTask() {
        super();
    }

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        try {
            return downloadUrl(urls[0]);

        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL", myurl);
        myurl = myurl.replace(" ", "%20");
        HttpURLConnection conn = null;
        String response;

        try {
            URL url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                // Convert the InputStream into a string
                response = readIt(in);
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
                in.close();
            } else {
                response = "Error en la solicitud: " + responseCode;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }

    private String readIt(BufferedReader stream) throws IOException {
        StringBuilder text = new StringBuilder();
        int character;

        while ((character = stream.read()) != -1) {
            text.append((char) character);
        }

        return text.toString();
    }
}
