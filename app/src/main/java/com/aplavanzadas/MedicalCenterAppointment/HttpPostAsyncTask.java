package com.aplavanzadas.medicalcenterappointment;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/* Los parámetros genéricos de esta clase son:
    Params: El tipo de parámetros a enviar a la tarea en ejecución
    Progress: El tipo de unidades utilizadas durante el calculo de fondo
    Result: El tipo del resultado de la tarea en segundo plano */
public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {

    // Este objeto mapea los datos por medio de clave => valor que se van a enviar por POST
    private Map<String, String> mapPostData;

    public HttpPostAsyncTask(Map<String, String> mapPostData) {
        super();
        this.mapPostData = mapPostData;
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

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : mapPostData.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            // Realizamos una solicitud al webservice por el método POST por seguridad y para evitar
            //  la restricción del número máximo de caracteres del método GET
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setFixedLengthStreamingMode(postDataBytes.length);
            conn.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));

            // Starts the query
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(postDataBytes);
            out.close();

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
