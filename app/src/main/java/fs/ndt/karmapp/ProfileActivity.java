package fs.ndt.karmapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.*;

import cz.msebera.android.httpclient.Header;
import fs.ndt.karmapp.rest.REST;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Guardando tu perfil", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // TODO: REST call to register user
                REST.post("/register", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        goToCalendarView();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        // goToErrorView();
                        goToCalendarView();
                    }
                });
            }
        });
    }

    /**
     * Show the calendar view with any notification available
     */
    private void goToCalendarView() {
        Intent intent = new Intent(this, CardsActivity.class);
        startActivity(intent);
    }

    /**
     * Shows an error message due to problems during register
     */
    private void goToErrorView() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set dialog message
        alertDialogBuilder
                .setMessage("No se pudo almacenar la información de su perfil por un error en el servidor")
                .setCancelable(false)
                .setPositiveButton("Re-intentar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
