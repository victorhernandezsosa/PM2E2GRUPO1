package com.example.pm2e1grupo1.Contactos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm2e1grupo1.MainActivity;
import com.example.pm2e1grupo1.MapsActivity;
import com.example.pm2e1grupo1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MostrarContactos extends AppCompatActivity {

    Button btnatras,btnadelete,btnactualizar,btnir;
    ListView lista;
    EditText buscar;

    ArrayList<Contactos> list;
    ArrayList<String> arreglocontac;
    ArrayAdapter<String>adp;

    private Contactos selectcontact;

    private int posicion;

    private RequestQueue requestQueue;
    String url = RestApiMethods.ApiGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_contactos);

        btnatras = (Button)  findViewById(R.id.btnatras);
        btnadelete = (Button) findViewById(R.id.btndelete);
        btnactualizar = (Button) findViewById(R.id.btnactualizar);
        buscar = (EditText) findViewById(R.id.buscar);
        lista = (ListView) findViewById(R.id.lista);
        btnir = (Button) findViewById(R.id.btnir) ;

        requestQueue = Volley.newRequestQueue(this);


        arreglocontac = new ArrayList<>();
        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arreglocontac);
        lista.setAdapter(adp);

        list = new ArrayList<>();
        mostrarDatos();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

               String objetosel = (String) adapterView.getItemAtPosition(i);

                Toast.makeText(getApplicationContext(),"Seleccionaste: " +objetosel,Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(MostrarContactos.this);
                builder.setTitle("ACCION");
                builder.setMessage("Desea ir a la ubicacion de "+ list.get(i).getNombres());
                builder.setPositiveButton("Ir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         selectcontact = list.get(i);
                        posicion = i;

                        googlemaps();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnadelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectcontact != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MostrarContactos.this);
                    builder.setTitle("Eliminar contacto");
                    builder.setMessage("¿Estás seguro de que deseas eliminar este contacto?");
                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eliminarContacto();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione un contacto para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectcontact != null) {
                    mostrarDialogoEditarContacto();
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione un contacto para actualizar", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("latitud", 15.471138);
                    intent.putExtra("longitud",  -87.999528);
                    startActivity(intent);

            }
        });

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String busqueda = s.toString().toLowerCase();

                ArrayList<Contactos> contacfilter = filtrar(busqueda);

                adp.clear();
                for (Contactos contacto : contacfilter){
                    adp.add(contacto.getNombres());
                }
                adp.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private ArrayList<Contactos> filtrar(String busqueda) {
        ArrayList<Contactos> contacfilter = new ArrayList<>();

        for (Contactos contacto : list){
            String nombre = contacto.getNombres().toLowerCase();

            if (nombre.contains(busqueda)){
                contacfilter.add(contacto);
            }
        }

        return contacfilter;
    }

    private void googlemaps() {
        if(selectcontact != null){
            String latitudconv = selectcontact.getLatitud();
            String longitudconv = selectcontact.getLongitud();

            try {
                double latitud = Double.parseDouble(latitudconv);
                double longitud = Double.parseDouble(longitudconv);

                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                startActivity(intent);
            }catch (NumberFormatException e){
                Toast.makeText(getApplicationContext(), "Error al convertir la longitud y latitud", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "Seleccione un contacto para ir a su ubicacion", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarContacto(){
        if (selectcontact != null) {
            JSONObject jsonContact = new JSONObject();
            try {
                jsonContact.put("id", selectcontact.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String deleteUrl = RestApiMethods.Apidel;

            JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, jsonContact,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Eliminación exitosa, actualiza la lista
                            list.remove(posicion);
                            adp.remove(arreglocontac.get(posicion));
                            adp.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                            Log.e("Error", "Error al eliminar el contacto: " + error.toString());
                        }
                    });

            requestQueue.add(deleteRequest);
        } else {
            Toast.makeText(getApplicationContext(), "Seleccione un contacto para eliminar", Toast.LENGTH_SHORT).show();
        }

    }

    private void mostrarDatos() {
        requestQueue = Volley.newRequestQueue(MostrarContactos.this);
        String url = RestApiMethods.ApiGet;

        StringRequest resquest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Respuesta del servidor", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<Contactos> contaclist = new ArrayList<>();


                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Contactos contac = new Contactos();
                            contac.setNombres(jsonObject.getString("nombres"));
                            contac.setTelefono(jsonObject.getString("telefono"));
                            contac.setLatitud(jsonObject.getString("latitud"));
                            contac.setLongitud(jsonObject.getString("longitud"));
                            contac.setImagen(jsonObject.getString("imagen"));



                            list.add(contac);
                            arreglocontac.add(contac.getNombres());
                        }

                        list = contaclist;
                        adp.clear();
                        for (Contactos contac : list) {
                            arreglocontac.add(contac.getNombres());
                            adp.add(contac.getNombres());
                        }
                        adp.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "No hay contactos disponibles", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ex) {
                    if (!isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MostrarContactos.this);
                        builder.setTitle("Error");
                        builder.setMessage("No se pudieron cargar los datos :(\nPor favor revisa tu conexión");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        builder.show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isFinishing()) {
                    error.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MostrarContactos.this);
                    builder.setTitle("Error");
                    builder.setMessage("No se pudieron cargar los datos :(\nPor favor revisa tu conexión");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        });

        requestQueue.add(resquest);
    }

    private void mostrarDialogoEditarContacto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MostrarContactos.this);
        builder.setTitle("Editar contacto");
        builder.setMessage("Ingrese los nuevos datos para actualizar el contacto");

        // Crear el diseño del cuadro de diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_editar_contacto, null);
        builder.setView(dialogView);

        EditText editNombres = dialogView.findViewById(R.id.edit_nombres);
        EditText editTelefono = dialogView.findViewById(R.id.edit_telefono);
        EditText editLatitud = dialogView.findViewById(R.id.edit_latitud);
        EditText editLongitud = dialogView.findViewById(R.id.edit_longitud);

        // Establecer los valores actuales del contacto en los campos de edición
        editNombres.setText(selectcontact.getNombres());
        editTelefono.setText(selectcontact.getTelefono());
        editLatitud.setText(selectcontact.getLatitud());
        editLongitud.setText(selectcontact.getLongitud());

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener los nuevos valores del contacto
                String nuevosNombres = editNombres.getText().toString();
                String nuevosTelefono = editTelefono.getText().toString();
                String nuevosLatitud = editLatitud.getText().toString();
                String nuevosLongitud = editLongitud.getText().toString();

                // Actualizar el contacto
                actualizarContacto(nuevosNombres, nuevosTelefono, nuevosLatitud, nuevosLongitud);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void actualizarContacto(String nuevosNombres, String nuevosTelefono, String nuevosLatitud, String nuevosLongitud) {
        if (selectcontact != null) {
            // Crear un objeto JSON con los nuevos datos del contacto
            JSONObject jsonContact = new JSONObject();
            try {
                jsonContact.put("id", selectcontact.getId());
                jsonContact.put("nombres", nuevosNombres);
                jsonContact.put("telefono", nuevosTelefono);
                jsonContact.put("latitud", nuevosLatitud);
                jsonContact.put("longitud", nuevosLongitud);
                jsonContact.put("imagen", selectcontact.getImagen()); // Mantener la imagen actual sin cambios en esta implementación
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String updateUrl = RestApiMethods.Apiupd; // Reemplaza con la URL correcta de tu API para actualizar el contacto

            JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, jsonContact,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Actualización exitosa, actualizar los datos del contacto en la lista
                            selectcontact.setNombres(nuevosNombres);
                            selectcontact.setTelefono(nuevosTelefono);
                            selectcontact.setLatitud(nuevosLatitud);
                            selectcontact.setLongitud(nuevosLongitud);
                            adp.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Error al actualizar el contacto, muestra un mensaje de error
                            Toast.makeText(getApplicationContext(), "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
                            Log.e("Error", "Error al actualizar el contacto: " + error.toString());
                        }
                    });

            requestQueue.add(updateRequest);
        } else {
            Toast.makeText(getApplicationContext(), "Seleccione un contacto para actualizar", Toast.LENGTH_SHORT).show();
        }
    }


}