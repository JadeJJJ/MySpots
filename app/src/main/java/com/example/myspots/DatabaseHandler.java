package com.example.myspots;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference landMarkRef = database.getReference("Landmarks");
    private List<Landmarks> landmarksList = new ArrayList<>();

    public DatabaseHandler() {
    }

    //get the list of landmarks for a user
    public List<Landmarks> GetLandmarksList(){
        landMarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulled : snapshot.getChildren()){
                    Landmarks lm = pulled.getValue(Landmarks.class);
                    landmarksList.add(lm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            }
        });
        return landmarksList;
    }

    //This posts the landmark to the database
    public void PostLandmark(Landmarks newLM){
        landMarkRef.push().setValue(newLM);
    }

    //This deletes the landmark, Removal of the marker is done in the main
    public void DeleteLandmark(String landmarkName){
        landMarkRef.addValueEventListener(new ValueEventListener() {
            boolean flag = false;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulled : snapshot.getChildren())
                {
                    Landmarks lm = pulled.getValue(Landmarks.class);
                    assert lm != null;
                    if(lm.getUserId().equals(MainActivity.UserID) && lm.getLandMarkName().equals(landmarkName) && !flag)
                    {
                        String key = pulled.getKey();
                        assert key != null;
                        landMarkRef.child(key).removeValue();
                        flag = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
