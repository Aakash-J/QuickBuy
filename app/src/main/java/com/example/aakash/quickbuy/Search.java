package com.example.aakash.quickbuy;

/**
 * Created by Aakash on 12/13/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


public class Search extends AppCompatActivity {



    //add Firebase Database stuff

    ImageButton car,house,furniture,electronic,book,food;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_item);

        car = (ImageButton) findViewById(R.id.car);
        house = (ImageButton) findViewById(R.id.house);
        furniture = (ImageButton) findViewById(R.id.furniture);
        electronic = (ImageButton) findViewById(R.id.electronic);
        book = (ImageButton) findViewById(R.id.book);
        food = (ImageButton) findViewById(R.id.food);


        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, ViewDatabase.class);
                intent.putExtra("item","Car");
                startActivity(intent);
            }
        });
        house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, ViewDatabase.class);
                intent.putExtra("item","Housing");
                startActivity(intent);
            }
        });
        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, ViewDatabase.class);
                intent.putExtra("item","Furniture");
                startActivity(intent);
            }
        });
        electronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, ViewDatabase.class);
                intent.putExtra("item","Electronics");
                startActivity(intent);
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, ViewDatabase.class);
                intent.putExtra("item","Books");
                startActivity(intent);
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, ViewDatabase.class);
                intent.putExtra("item","Food");
                startActivity(intent);
            }
        });





    }



    @Override
    public void onStart() {
        super.onStart();
        // mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

 /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
