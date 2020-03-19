package shafi.sbf.com.foodjinniadmin.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import shafi.sbf.com.foodjinniadmin.ActiveOrderAdapter;
import shafi.sbf.com.foodjinniadmin.MainActivity;
import shafi.sbf.com.foodjinniadmin.R;
import shafi.sbf.com.foodjinniadmin.pojo.AreaPojo;
import shafi.sbf.com.foodjinniadmin.pojo.ConfirmOrder;
import shafi.sbf.com.foodjinniadmin.pojo.RestaurantDetails;

public class ActiveFragment extends Fragment {

    private List<ConfirmOrder> orderCharts = new ArrayList<ConfirmOrder>();

    private DatabaseReference rootRef;
    private DatabaseReference orderRef;

    private FirebaseAuth auth;
    private FirebaseUser userauth;

    String userId;

    private RestaurantDetails restaurantDetails;

    private DatabaseReference userRef;
    private DatabaseReference areaRef;
    private DatabaseReference catagRef;

    String area;

    RecyclerView Category,FoodRecycler, test;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_active, container, false);

        auth = FirebaseAuth.getInstance();
        userauth=auth.getCurrentUser();
        userId=userauth.getUid();


        rootRef = FirebaseDatabase.getInstance().getReference();

        areaRef = rootRef.child("Restaurant").child("Area").child(userId);



        FoodRecycler = view.findViewById(R.id.foodRecy);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        areaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AreaPojo h = dataSnapshot.getValue(AreaPojo.class);

                area = h.getArea();

                orderRef = rootRef.child("Restaurant").child(area).child(userId).child("ActivOrder");

                orderRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        orderCharts.clear();

                        for (DataSnapshot donnerSnapshot : dataSnapshot.getChildren()) {
                            ConfirmOrder confirmOrder = donnerSnapshot.child("details").getValue(ConfirmOrder.class);
                            orderCharts.add(confirmOrder);
                        }


                        ActiveOrderAdapter recylaerViewAdapter = new ActiveOrderAdapter(getActivity(),orderCharts);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        FoodRecycler.setLayoutManager(mLayoutManager);
                        FoodRecycler.setItemAnimator(new DefaultItemAnimator());
                        FoodRecycler.setAdapter(recylaerViewAdapter);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
