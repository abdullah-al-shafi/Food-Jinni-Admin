package shafi.sbf.com.foodjinniadmin;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import java.util.List;

import shafi.sbf.com.foodjinniadmin.pojo.AreaPojo;

public class FoodList extends NaviBase implements MenuCatagoriAdapter.CatagoriListAdapterListener,FoodRecylaerViewAdapter.FoodListiner{

    RecyclerView Category,FoodRecycler, test;

    private List<AddFoodDetails> addFoods = new ArrayList<AddFoodDetails>();
    private List<CatagoriPojo> catagoriPojos = new ArrayList<>();

    List<String> cataList = new ArrayList<String>();

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference areaRef;
    private DatabaseReference catagRef;
    private DatabaseReference foodRef;

    Dialog mydialog;

    private boolean checkNothing = false;

    TextView Item;
    String catagory,CataID;

    FrameLayout frameLayout;

    private double totalPrice;

    int it = 0;

    EditText txtnote;


    String location,store,System,area;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        mtollbarText=(TextView) findViewById(R.id.toolbar_text_base);
        mtollbarText.setText("Food List");

        Category = findViewById(R.id.menuName);
        FoodRecycler = findViewById(R.id.foodRecy);
        rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        findArea();


    }

    private void findArea() {

        areaRef = rootRef.child("Restaurant").child("Area").child(user.getUid());

        areaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AreaPojo h = dataSnapshot.getValue(AreaPojo.class);

                area= h.getArea();



                Catago();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void Catago() {
        catagRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("catagori");
        catagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                catagoriPojos.clear();

               // Toast.makeText(FoodList.this, ""+dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
//                historyTV.setText(String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot hd: dataSnapshot.getChildren()){
                    CatagoriPojo doc = hd.getValue(CatagoriPojo.class);
                    catagoriPojos.add(doc);
                }

                catagory = catagoriPojos.get(0).getCatagori();
                CataID = catagoriPojos.get(0).getId();

                MenuCatagoriAdapter recylaerViewAdapter = new MenuCatagoriAdapter(FoodList.this, catagoriPojos);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                Category.setLayoutManager(mLayoutManager);
                Category.setItemAnimator(new DefaultItemAnimator());
                Category.setAdapter(recylaerViewAdapter);

                showFirstList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showFirstList() {

        userRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("Food List").child(catagory);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addFoods.clear();
                //historyTV.setText(String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot hd: dataSnapshot.getChildren()){
                    AddFoodDetails doc = hd.child("details").getValue(AddFoodDetails.class);
                    addFoods.add(doc);
                }

                FoodRecylaerViewAdapter  recylaerViewAdapter = new FoodRecylaerViewAdapter(FoodList.this,addFoods);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                FoodRecycler.setLayoutManager(mLayoutManager);
                FoodRecycler.setItemAnimator(new DefaultItemAnimator());
                FoodRecycler.setAdapter(recylaerViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onFooodCurt(AddFoodDetails addFood) {

        Intent intent = new Intent(FoodList.this, AddFood.class);
        intent.putExtra("up","yes");
        intent.putExtra("cata",catagory);
        intent.putExtra("cataID",CataID);
        intent.putExtra("Details", addFood);
        startActivity(intent);
        finish();

    }

    @Override
    public void onCompleteBooking(CatagoriPojo hb) {

        catagory = hb.getCatagori();
        CataID = hb.getId();

        userRef = rootRef.child("Restaurant").child(area).child(user.getUid()).child("Food List").child(catagory);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addFoods.clear();
                //historyTV.setText(String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot hd: dataSnapshot.getChildren()){
                    AddFoodDetails doc = hd.child("details").getValue(AddFoodDetails.class);
                    addFoods.add(doc);
                }

                FoodRecylaerViewAdapter  recylaerViewAdapter = new FoodRecylaerViewAdapter(FoodList.this,addFoods);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                FoodRecycler.setLayoutManager(mLayoutManager);
                FoodRecycler.setItemAnimator(new DefaultItemAnimator());
                FoodRecycler.setAdapter(recylaerViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
