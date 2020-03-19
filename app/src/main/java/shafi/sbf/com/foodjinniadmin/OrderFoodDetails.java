package shafi.sbf.com.foodjinniadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import shafi.sbf.com.foodjinniadmin.NaviBase;
import shafi.sbf.com.foodjinniadmin.R;
import shafi.sbf.com.foodjinniadmin.TestAdapter;
import shafi.sbf.com.foodjinniadmin.pojo.ConfirmOrder;
import shafi.sbf.com.foodjinniadmin.pojo.OrderChart;

public class OrderFoodDetails extends NaviBase {

    private DatabaseReference rootRef;
    private DatabaseReference orderRef;
    private DatabaseReference orderRef2;
    private DatabaseReference orderRef3;
    private DatabaseReference driverRef;


    TextView Name, Fon, Mail, Total, VAt, OrderAddress, DeliveBy;

    LinearLayout First, Second, Third, Four;

    Button Change;

    String name, fon, email, group, dtinote, dtime, note, ldis, ltime, location;

    String includeVatPrice, totalPrice, Ordernum, ActivityF;


    String formattedDate, getCurrentTime;

    int onum;

    Spinner spinner;

    RecyclerView test;
    private TestAdapter adapterT;

    private int mHour, mMinute;

    String store, system;

    private ConfirmOrder confirmOrder;

    private List<OrderChart> orderCharts = new ArrayList<OrderChart>();
    List<String> driverList = new ArrayList<String>();

    private Date date;
    private Date dateCompareOne;
    private Date dateCompareTwo;
    public static final String inputFormat = "HH:mm";


    String driver;

    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_food_details);

        confirmOrder = (ConfirmOrder) getIntent().getSerializableExtra("valu");
        Ordernum = getIntent().getStringExtra("no");
        ActivityF = getIntent().getStringExtra("id");


        Name = findViewById(R.id.orderName);
        Fon = findViewById(R.id.orderFonNumber);
        Mail = findViewById(R.id.orderEmail);
        Total = findViewById(R.id.showPrice);
        VAt = findViewById(R.id.showPriceF);
        test = findViewById(R.id.foodRecyTest);
        Change = findViewById(R.id.change);
        OrderAddress = findViewById(R.id.orderAddress);


        rootRef = FirebaseDatabase.getInstance().getReference();
        orderRef = rootRef.child("Restaurant").child(confirmOrder.getRestaurantDetails().getArea()).child(confirmOrder.getRestaurantDetails().getRestaurantID()).child("ActivOrder");
        Four = findViewById(R.id.four);

        mtollbarText = (TextView) findViewById(R.id.toolbar_text_base);
        mtollbarText.setText("ACTIVE");


        if (ActivityF.equals("finish")) {
            Four.setVisibility(View.INVISIBLE);
            mtollbarText.setText("Finish");


        }

        orderCharts = confirmOrder.getChartList();
        totalPrice = confirmOrder.getTottalPrice();
        includeVatPrice = confirmOrder.getIncludeVatTotalPrice();
        name = confirmOrder.getUser().getUserName();
        fon = confirmOrder.getUser().getUserNumber();
        email = confirmOrder.getUser().getUserMail();

        if (confirmOrder.getAddress() != null
                && !TextUtils.isEmpty(confirmOrder.getAddress())) {
            OrderAddress.setText(confirmOrder.getAddress());

        }

//        OrderNumber.setText("#"+Ordernum);

        Name.setText(name);
        Fon.setText(fon);
        Mail.setText(email);
        Total.setText(new DecimalFormat("##.##").format(Double.parseDouble(totalPrice)));
        VAt.setText(new DecimalFormat("##.##").format(Double.parseDouble(includeVatPrice)));


        adapterT = new TestAdapter(OrderFoodDetails.this, orderCharts);
        LinearLayoutManager llm = new LinearLayoutManager(OrderFoodDetails.this);
        llm.setOrientation(RecyclerView.VERTICAL);
        test.setLayoutManager(llm);
        test.setAdapter(adapterT);


        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c);

//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        getCurrentTime = sdf.format(c.getTime());

        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        date = parseDate(hour + ":" + minute);

    }

    public void Cancel(View view) {
    }

    public void pickup(View view) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading...");
        mDialog.show();

        if (ActivityF.equals("active")) {

            orderRef2 = rootRef.child("Restaurant").child(confirmOrder.getRestaurantDetails().getArea()).child(confirmOrder.getRestaurantDetails().getRestaurantID()).child("Finish").child(formattedDate);
            final String id = confirmOrder.getOrderID();
            final String userId = confirmOrder.getUser().getUserId();
            orderRef2.child(id).child("details").setValue(confirmOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        orderRef.child(id).removeValue();

                        final Map<String, Object> avatarUpdate = new HashMap<>();
                        avatarUpdate.put("status", "Complete");

                        rootRef.child("User").child(userId).child("OrderList").child(id).child("details").updateChildren(avatarUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDialog.dismiss();
                                Intent intent = new Intent(OrderFoodDetails.this, MainActivity.class);
                                intent.putExtra("id", "active");
                                startActivity(intent);
                                finish();
                                Toast.makeText(OrderFoodDetails.this, "Successful", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        mDialog.dismiss();
                        Toast.makeText(OrderFoodDetails.this, "Failed to add Doctor", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(OrderFoodDetails.this, "Failed : " + e, Toast.LENGTH_SHORT).show();
                }
            });


        }


    }

    private Date parseDate (String date){

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }
}
