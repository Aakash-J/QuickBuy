package com.example.aakash.quickbuy;

/**
 * Created by Aakash on 12/13/2017.
 */


        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;

        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;


public class ViewDatabase extends AppCompatActivity {

    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    String userID;

    private ListView mListView;
    private TextView sHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_database_layout);
        mListView = (ListView) findViewById(R.id.listview);
         sHeader = (TextView) findViewById(R.id.tvUserInfo);
        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
       // FirebaseUser user = mAuth.getCurrentUser();
        //userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                 //   toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                 //   toastMessage("Successfully signed out.");
                }
                // ...
            }
        };


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void showData(DataSnapshot dataSnapshot){

        Intent intent = getIntent();

        // fetch value from key-value pair and make it visible on TextView.
        String item = intent.getStringExtra("item");
        sHeader.setText(item);
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

                //     Log.d(TAG, "showData: email: " + uInfo.getEmail());
                //     Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());

                if(item.equalsIgnoreCase(uInfo.getCategory())) {

                    Log.d(TAG, "showData: name: " + uInfo.getName() + item + uInfo.getCategory());
                    array.add(uInfo.getName());
                }


            }
                //   array.add(uInfo.getEmail());
                //  array.add(uInfo.getPhone_num());
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);

                mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    String item = ((TextView)view).getText().toString();

                    Intent intent = new Intent(ViewDatabase.this, DisplayItem.class);
                    // add the selected text item to our intent.
                    intent.putExtra("selected-item", item);
                    startActivity(intent);
                    //Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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