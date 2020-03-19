package shafi.sbf.com.foodjinniadmin.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import shafi.sbf.com.foodjinniadmin.ActiveOrderAdapter;
import shafi.sbf.com.foodjinniadmin.FinishOrderAdapter;
import shafi.sbf.com.foodjinniadmin.R;
import shafi.sbf.com.foodjinniadmin.pojo.AreaPojo;
import shafi.sbf.com.foodjinniadmin.pojo.ConfirmOrder;

public class FinishFragment extends Fragment {

    private List<ConfirmOrder> orderCharts = new ArrayList<ConfirmOrder>();
//
//    private List<DeliveryMan> deliveryMEN = new ArrayList<DeliveryMan>();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

    RadioButton Today,Old;

    private String formattedDate;

    private FirebaseAuth auth;
    private FirebaseUser userauth;

    String userId;

    String area;

    private DatabaseReference rootRef;
    private DatabaseReference orderRef;
    private DatabaseReference areaRef;
    private DatabaseReference orderRef2;

    private LinearLayout DateSelect;
    private TextView DateTv;
    private String date;

    RecyclerView Category,FoodRecycler, test;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finish, container, false);

        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);
        formattedDate = simpleDateFormat.format(c);


        auth = FirebaseAuth.getInstance();
        userauth=auth.getCurrentUser();
        userId=userauth.getUid();


        rootRef = FirebaseDatabase.getInstance().getReference();

        areaRef = rootRef.child("Restaurant").child("Area").child(userId);



        Today = view.findViewById(R.id.radio0);
        Old =view.findViewById(R.id.radio1);
        DateSelect =view.findViewById(R.id.select_date_head);
        DateTv =view.findViewById(R.id.select_date);

        FoodRecycler = view.findViewById(R.id.foodRecy3);

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                date = simpleDateFormat.format(calendar.getTime());
                DateTv.setText(date);
                getCompletedAppointmentList(date);
            }

        };


        Today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSelect.setVisibility(View.GONE);
                todayList();
            }
        });

        Old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSelect.setVisibility(View.VISIBLE);
                DateSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(getContext(), dateSetListener, calendar.get
                                (Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            }
        });

        return view;
    }
    private void getCompletedAppointmentList(String date) {

        orderRef = rootRef.child("Restaurant").child(area).child(userId).child("Finish").child(date);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                orderCharts.clear();

                for (DataSnapshot donnerSnapshot : dataSnapshot.getChildren()) {
                    ConfirmOrder confirmOrder = donnerSnapshot.child("details").getValue(ConfirmOrder.class);
                    orderCharts.add(confirmOrder);
                }
                FinishOrderAdapter recylaerViewAdapter = new FinishOrderAdapter(getActivity(),orderCharts);
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
    public void onStart() {
        super.onStart();
        todayList();
    }

    private void todayList() {

        areaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AreaPojo h = dataSnapshot.getValue(AreaPojo.class);

                area = h.getArea();

                orderRef = rootRef.child("Restaurant").child(area).child(userId).child("Finish").child(formattedDate);

                orderRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        orderCharts.clear();

                        for (DataSnapshot donnerSnapshot : dataSnapshot.getChildren()) {
                            ConfirmOrder confirmOrder = donnerSnapshot.child("details").getValue(ConfirmOrder.class);
                            orderCharts.add(confirmOrder);
                        }
                        FinishOrderAdapter recylaerViewAdapter = new FinishOrderAdapter(getActivity(),orderCharts);
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

