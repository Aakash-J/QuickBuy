package com.example.aakash.quickbuy;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Serializable {

    EditText name,price,description,contact;
    Button submit,upload,viewData;
    Spinner category;
    DatabaseReference rootRef,demoRef;
    StorageReference mStorage;
    ProgressDialog progressDialog;
    private static  final int CAMERA_REQUEST_CODE = 1;
    Uri picUri;
    Uri dataUri;
    int MY_PERMISSIONS_REQUEST_STORAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        name = (EditText) findViewById(R.id.name);
        price = (EditText)findViewById(R.id.price);
        description = (EditText)findViewById(R.id.description);
        contact = (EditText)findViewById(R.id.contact);
        submit = (Button) findViewById(R.id.button4);
        upload = (Button) findViewById(R.id.btn_photo);
        category = (Spinner)findViewById(R.id.spinner);




        mStorage = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Item");

        progressDialog = new ProgressDialog(this);


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

       upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

              Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

               File file=getOutputMediaFile(1);
               picUri = Uri.fromFile(file); // create
               intent.putExtra(MediaStore.EXTRA_OUTPUT,picUri);

               startActivityForResult(intent,CAMERA_REQUEST_CODE);

           }
       });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sName =  name.getText().toString();
                String sPrice =  price.getText().toString();
                String sDescription = description.getText().toString();
                String sContact = contact.getText().toString();
                String sCategory =  category.getSelectedItem().toString();
                if(sName.equals("") || sPrice.equals("")
                        || sDescription.equals("") || sContact.equals("")
                        || dataUri == null){

                    Toast.makeText(MainActivity.this,"Please complete the form!",Toast.LENGTH_SHORT).show();

                    return;
                }

                String sImagePath = dataUri.toString();



                ItemInformation info = new ItemInformation(sName,sPrice,sDescription,sContact,sCategory,sImagePath);


                rootRef.child("users").push().setValue(info);



//
//
//                String item = "Items";
//                String value = name.getText().toString();
//
//                Map<String, String> map = new HashMap<>();
//                JSONObject tempObject = new JSONObject();
//
//                try{
//                    map.put("category",category.getSelectedItem().toString());
//                    map.put("name",name.getText().toString());
//                    map.put("price",price.getText().toString());
//                    map.put("description",description.getText().toString());
//                    map.put("contact",contact.getText().toString());
//                    map.put("Image",dataUri.toString());
//
//
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                //userMap.put("myUser", map);
//
//                demoRef.push().setValue(map.toString());

                name.setText("");
                price.setText("");
                description.setText("");
                contact.setText("");

                Toast.makeText(MainActivity.this,"Item Posted!!!",Toast.LENGTH_SHORT).show();


            }
        });


    }

    /** Create a File for saving an image */
    private  File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "QuickBuy");
        Log.d("jain","aakash");
        /**Create the storage directory if it does not exist*/
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        Log.d("jain","aakash");
        /**Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;


        if (type == 1){

            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){


            progressDialog.setMessage("Uploading image..");
            progressDialog.show();

            Uri uri = picUri;

            StorageReference filePath = mStorage.child("Photos").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    dataUri  =  taskSnapshot.getDownloadUrl();
                    progressDialog.dismiss();
                 //   Toast.makeText(MainActivity.this,dataUri.toString(),Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }


}
