package com.aplavanzadas.medicalcenterappointment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // URL Genymotion: http://10.0.3.2/
    // URL emulador android studio: http://10.0.2.2/
    // URL hosting gratuito: http://desarrollomoviles.000webhostapp.com/
    public static final String URL_SERVIDOR = "http://a61f689f.ngrok.io/desarrollo_apl_avanzadas/proyecto_final/";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.cusuario);

        mPasswordView = (EditText) findViewById(R.id.cclave);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.btn_aceptar);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView tvRegistrar = (TextView) findViewById(R.id.tv_registrar);
        tvRegistrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intentReg = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intentReg);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Ocultar teclado virtual
        View v = getCurrentFocus();

        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            Map<String, String> parametros = new LinkedHashMap<>();
            parametros.put("nom_usu", email);
            parametros.put("clave", password);

            mAuthTask = new UserLoginTask(parametros);

            // Solicitar al webservice en php los datos de login de usuario o doctor
            mAuthTask.execute(URL_SERVIDOR + "consultar_login.php");
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends HttpPostAsyncTask {

        private UserLoginTask(Map<String, String> postData) {
            super(postData);
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;

            // Log.d(TAG, result);
            try {

                JSONObject jsonResponse = new JSONObject(result);
                // Si el json devuelve una clave success con el valor en false, significa que las credenciales
                //  para iniciar sesión son incorrectas
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    int perfil = jsonResponse.getInt("codigo_perfil");

                    Bundle datosSesion = new Bundle();
                    datosSesion.putString("nombres", jsonResponse.getString("nombres"));
                    datosSesion.putString("apellidos", jsonResponse.getString("apellidos"));

                    if (perfil == 1) {
                        // Pasa los datos recogidos a la otra activity
                        Intent intent = new Intent(MainActivity.this, PerfilUsuarioActivity.class);
                        datosSesion.putInt("codigo_usuario", jsonResponse.getInt("codigo_usuario"));
                        intent.putExtras(datosSesion);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, PerfilMedicoActivity.class);
                        datosSesion.putInt("codigo_doctor", jsonResponse.getInt("codigo_doctor"));
                        intent.putExtras(datosSesion);
                        startActivity(intent);
                    }

                    finish();
                } else {
                    showProgress(false);
                    mUsernameView.requestFocus();

                    Toast.makeText(MainActivity.this, R.string.error_incorrect_credentials, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error al obtener los datos de inicio de sesión", e);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void salir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_exit);

        // Añadir botones
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.super.onBackPressed();
            }
        });

        // Mostrar alertDialog al usuario
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        salir();
    }
}

