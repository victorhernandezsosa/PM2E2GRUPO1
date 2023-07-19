package com.example.pm2e1grupo1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm2e1grupo1.Contactos.Contactos;
import com.example.pm2e1grupo1.Contactos.MostrarContactos;
import com.example.pm2e1grupo1.Contactos.RestApiMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button botonfoto,btnguardar,btnvercontactos;
    EditText nombre,telefono,latitud,longitud;

    ImageView imagen;

    static final  int REQUEST_IMAGE = 101;
    static final  int PETICION_ACCESS_CAM = 201;

    private LocationManager locationManager;

    String currentPhotoPath;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonfoto = (Button) findViewById(R.id.botonfoto);
        btnguardar = (Button) findViewById(R.id.btnguardar);
        btnvercontactos = (Button) findViewById(R.id.btnvercontactos);
        nombre = (EditText) findViewById(R.id.nombre);
        telefono = (EditText) findViewById(R.id.telefono);
        latitud = (EditText) findViewById(R.id.latitud);
        longitud = (EditText) findViewById(R.id.longitud);
        imagen = (ImageView) findViewById(R.id.imagen);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mostrarMensajeGPSInactivo();
        }

        botonfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarcontactos();
            }
        });

        btnvercontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vercontactos();

            }
        });

    }

    private void mostrarMensajeGPSInactivo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS Inactivo");
        builder.setMessage("El GPS no está activo. Por favor, activalo");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Abrir la configuración de ubicación del dispositivo
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void salvarcontactos() {
        String lact = latitud.getText().toString();
        String longi = longitud.getText().toString();

        if (imagen.getDrawable() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ALERTA");
            builder.setMessage("Por favor, debes tomar una foto");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        } else if (lact.isEmpty() || longi.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ALERTA");
            builder.setMessage("Debes completar los campos de latitud y longitud");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        } else {
            requestQueue = Volley.newRequestQueue(this);

            Contactos contac = new Contactos();
            contac.setNombres(nombre.getText().toString());
            contac.setTelefono(telefono.getText().toString());
            contac.setLatitud(lact);
            contac.setLongitud(longi);
            contac.setImagen(ConvertImage64(currentPhotoPath));

            JSONObject jsoncontac = new JSONObject();

            try {
                jsoncontac.put("nombres", contac.getNombres());
                jsoncontac.put("telefono", contac.getTelefono());
                jsoncontac.put("latitud", contac.getLatitud());
                jsoncontac.put("longitud", contac.getLongitud());
                jsoncontac.put("imagen", contac.getImagen());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JsonObjectRequest resquest = new JsonObjectRequest(Request.Method.POST,
                    RestApiMethods.ApiPost,
                    jsoncontac, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mensaje = response.getString("message");
                        if (mensaje.equals("success")) {
                            Toast.makeText(MainActivity.this, "Los datos fueron guardados con éxito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Los datos no se guardaron con éxito", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", "ERROR AL GUARDAR LOS DATOS", error);
                }
            });

            requestQueue.add(resquest);
        }
    }


    private void vercontactos () {
            Intent intent = new Intent(getApplicationContext(), MostrarContactos.class);
            startActivity(intent);
        }

    private void permisos()
    {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},PETICION_ACCESS_CAM);
        }
        else
        {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESS_CAM)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "se necesita el permiso de la camara",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE)
        {
            //Bundle extra = data.getExtras();
            //Bitmap imagen = (Bitmap) extra.get("data");
            //imageView.setImageBitmap(imagen);

            try {
                File foto = new File(currentPhotoPath);
                imagen.setImageURI(Uri.fromFile(foto));
            }
            catch (Exception ex)
            {
                ex.toString();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.pm2e1grupo1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String ConvertImage64(String path)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imagearray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imagearray, Base64.DEFAULT);
        }else{
            return "";
        }
    }
}