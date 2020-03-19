package shafi.sbf.com.foodjinniadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import shafi.sbf.com.foodjinniadmin.pojo.AreaPojo;

public class AddFood extends NaviBase {

    Spinner Catagori;
    EditText name,discription,Price,food_ID;

    String fname,fdis,fprice,catagori,id,fid,area;

    String check;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference areaRef;
    private DatabaseReference catagRef;
    private DatabaseReference catagRefGet;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private AddFoodDetails addFood;
    private Button adf,dlet;
    private TextView Catag;
    String cataID,caID;

    ImageView imageView;

    private List<AddFoodDetails> addFoods = new ArrayList<AddFoodDetails>();
    private List<CatagoriPojo> catagoriPojos = new ArrayList<>();
    private boolean checkNothing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mtollbarText=(TextView) findViewById(R.id.toolbar_text_base);
        mtollbarText.setText("Add Food");

        check = getIntent().getStringExtra("up");
        catagori = getIntent().getStringExtra("cata");
        caID = getIntent().getStringExtra("cataID");
        addFood = (AddFoodDetails) getIntent().getSerializableExtra("Details");


        //Init FireBase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        Catagori = findViewById(R.id.select_cata);
        name = findViewById(R.id.food_name);
        discription = findViewById(R.id.food_des);
        Price = findViewById(R.id.food_price);
        food_ID = findViewById(R.id.food_id);
        adf = findViewById(R.id.add_food);
        dlet = findViewById(R.id.delet_food);
        Catag = findViewById(R.id.catagori);


        rootRef = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();


        user = auth.getCurrentUser();

        findArea();


        final String catag [] = {"Select Categories","Pizza","Bugerer","Chicken","Grilled","Rice","Shawarma","Sandwich","Pasta","Noodels","Snacks","Soup","Cake","Ice-Cream","Coffee","Drinks"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,catag);

        Catagori.setAdapter(adapter);

        Catagori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    catagori = Catagori.getItemAtPosition(i).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        if (check.equals("yes")){
            adf.setText("UPDATE");
            dlet.setVisibility(View.VISIBLE);
            Catagori.setVisibility(View.GONE);
            Catag.setVisibility(View.VISIBLE);

            id = addFood.getFoodId();

            cataID = caID;

            Catag.setText(catagori);
            name.setText(addFood.getFoodName());
            discription.setText(addFood.getFoodDescription());
            Price.setText(addFood.getFoodPrice());
            food_ID.setText(addFood.getFoodNumber());

            adf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fname = name.getText().toString();
                    fid = food_ID.getText().toString();
                    fprice = Price.getText().toString();
                    fdis = discription.getText().toString();

                    userRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("Food List").child(catagori);
                    catagRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("catagori");

                    Addfood();
                }
            });


        }
        else if (check.equals("new")){

            adf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (catagori.equals("new")) {
                        Toast.makeText(AddFood.this, "Select Category", Toast.LENGTH_SHORT).show();
                    }
                    fid = food_ID.getText().toString();
                    if (fid.isEmpty()) {
                        food_ID.setError("Enter Food number");
                        food_ID.requestFocus();
                        return;
                    }
                    fname = name.getText().toString();
                    if (fname.isEmpty()) {
                        name.setError("Enter name");
                        name.requestFocus();
                        return;
                    }
                    fdis = discription.getText().toString();
                    if (fdis==null) {
                        fdis = " ";
                        return;
                    }
                    fprice = Price.getText().toString();
                    if (fprice.isEmpty()) {
                        Price.setError("Enter Price");
                        Price.requestFocus();
                        return;
                    }else {

                        userRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("Food List").child(catagori);
                        catagRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("catagori");
                        id = userRef.push().getKey();

                        for (CatagoriPojo c: catagoriPojos){
                            if (c.getCatagori().equals(catagori)){
                                cataID = c.getId();
                                checkNothing =  true;
                                break;
                            }else {
                                checkNothing = false;
                            }
                        }
                        if (!checkNothing){

                            cataID = catagRef.push().getKey();
                        }

                        Addfood();

                    }
                }
            });

        }
    }



    private void findArea() {

        areaRef = rootRef.child("Restaurant").child("Area").child(user.getUid());

        areaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AreaPojo h = dataSnapshot.getValue(AreaPojo.class);

                 area= h.getArea();

                 getCataGori();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getCataGori() {
        catagRefGet = rootRef.child("Restaurant").child(area).child(user.getUid()).child("catagori");
        catagRefGet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                catagoriPojos.clear();
                // Toast.makeText(FoodList.this, ""+dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
//                historyTV.setText(String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot hd: dataSnapshot.getChildren()){
                    CatagoriPojo doc = hd.getValue(CatagoriPojo.class);
                    catagoriPojos.add(doc);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void Addfood() {

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading...");
        mDialog.show();


        final  CatagoriPojo cAtagoriPojo = new CatagoriPojo(cataID,catagori);
        final AddFoodDetails addFood = new AddFoodDetails(id,fname,fid,fdis,fprice);
        userRef.child(id).child("details").setValue(addFood).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    catagRef.child(cataID).setValue(cAtagoriPojo);
                    name.getText().clear();
                    discription.getText().clear();
                    food_ID.getText().clear();
                    Price.getText().clear();
                    mDialog.dismiss();
                    if (check.equals("yes")){
                        startActivity(new Intent(AddFood.this,FoodList.class));
                        finish();
                    }
                    Toast.makeText(AddFood.this, "Successful", Toast.LENGTH_SHORT).show();

                } else {
                    mDialog.dismiss();
                    Toast.makeText(AddFood.this, "Failed to add Doctor", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                Toast.makeText(AddFood.this, "Failed : "+e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void deletefood(View view) {
        userRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("Food List").child(catagori);
        userRef.child(id).removeValue();
        startActivity(new Intent(AddFood.this,FoodList.class));
        finish();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddFood.this,FoodList.class));
        finish();
    }
}
