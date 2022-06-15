package com.example.laxman.bussuvidha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BusInformationActivity extends AppCompatActivity {

    private TextView textView;
    private ListView listViewId;

    DatabaseReference databaseReference;
    private List<UserInformation> userInformationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_information);
        textView =findViewById(R.id.textViewBusInfo);
        listViewId = findViewById(R.id.listViewId);

        databaseReference = FirebaseDatabase.getInstance().getReference("DriverInformation");
        userInformationList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                     UserInformation userInformation = userSnapshot.getValue(UserInformation.class);
                     userInformationList.add(userInformation);
                 }
                BusList adapter =new BusList(BusInformationActivity.this,userInformationList);
                 listViewId.setAdapter(adapter);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }
}
