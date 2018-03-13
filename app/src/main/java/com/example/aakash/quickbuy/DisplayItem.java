package com.example.aakash.quickbuy;

/**
 * Created by Aakash on 12/13/2017.
 */

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


public class DisplayItem extends AppCompatActivity {



    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    TextView name,price,description,contact;
    ImageView imageView;
    String URL = "";
    ProgressDialog progressDialog;
    private ImageButton call;
    String number = "";
    private ImageButton message;
    private String itemName;
    int MY_PERMISSIONS_REQUEST_CALL;




    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayitem);

        imageView = (ImageView) findViewById(R.id.imageView);


        if (ContextCompat.checkSelfPermission(DisplayItem.this,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DisplayItem.this,
                    android.Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(DisplayItem.this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        name = (TextView) findViewById(R.id.name);
        price = (TextView)findViewById(R.id.price);
        description = (TextView)findViewById(R.id.description);
        contact = (TextView)findViewById(R.id.contact);
        call = (ImageButton)findViewById(R.id.call);
        message = (ImageButton) findViewById(R.id.message);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving Data..");
        progressDialog.show();

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
       // mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        // FirebaseUser user = mAuth.getCurrentUser();
        //userID = user.getUid();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DisplayItem.this.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+ number));
                        startActivity(callIntent);

                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+ number));
                        startActivity(callIntent);
                    }
                } else {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+ number));
                    startActivity(callIntent);
                }

            }
        });


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, URL);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm interested in buying item you posted: " + itemName);
                        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("1"+number) + "@s.whatsapp.net"); //phone number without "+" prefix
                        sendIntent.setPackage("com.whatsapp");
                        startActivity(sendIntent);



            }
        });




        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                showData(dataSnapshot);
                GetXMLTask task = new GetXMLTask();
                // Execute the task
                task.execute(new String[] { URL });


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

    }
    void callPhone(){

    }

    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            progressDialog.dismiss();
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    private void showData(DataSnapshot dataSnapshot){

        Intent intent = getIntent();

        // fetch value from key-value pair and make it visible on TextView.
        String item = intent.getStringExtra("selected-item");
        for(DataSnapshot ds : dataSnapshot.getChildren()) {

            ItemInformation uInfo = new ItemInformation();
            ArrayList<String> array = new ArrayList<>();
            for (DataSnapshot ds1 : ds.getChildren()) {
                Log.d("keys", ds1.getKey());

                //  ds.getChildren());
                uInfo.setName(ds.child(ds1.getKey()).getValue(ItemInformation.class).getName());
                uInfo.setCategory(ds.child(ds1.getKey()).getValue(ItemInformation.class).getCategory());//set the name
                uInfo.setPrice(ds.child(ds1.getKey()).getValue(ItemInformation.class).getPrice());
                uInfo.setDescription(ds.child(ds1.getKey()).getValue(ItemInformation.class).getDescription());
                uInfo.setContact(ds.child(ds1.getKey()).getValue(ItemInformation.class).getContact());
                uInfo.setImagePath(ds.child(ds1.getKey()).getValue(ItemInformation.class).getImagePath());

                //   uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail()); //set the email
                //   uInfo.setPhone_num(ds.child(userID).getValue(UserInformation.class).getPhone_num()); //set the phone_num

                //display all the information

                //    Log.d( "showData: email: " , uInfo.getImagePath());
                //     Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());

                   if(uInfo.getName().equals(item)){

                       name.setText(name.getText() + ":  " + uInfo.getName());
                       price.setText(price.getText() + ":  " + uInfo.getPrice());
                       description.setText(description.getText() + ":  " + uInfo.getDescription());
                       contact.setText(contact.getText() + "Tel: " + uInfo.getContact());
                       number  = uInfo.getContact();
                       URL =  uInfo.getImagePath();
                       itemName = uInfo.getName();

                   }



            }
            //   array.add(uInfo.getEmail());
            //  array.add(uInfo.getPhone_num());

        }
    }

    @Override
    public void onStart() {
        super.onStart();
       // mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
