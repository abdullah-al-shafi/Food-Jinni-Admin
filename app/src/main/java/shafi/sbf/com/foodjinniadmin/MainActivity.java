package shafi.sbf.com.foodjinniadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import shafi.sbf.com.foodjinniadmin.Fragment.ActiveFragment;
import shafi.sbf.com.foodjinniadmin.Fragment.FinishFragment;
import shafi.sbf.com.foodjinniadmin.Registration.LogIn;
import shafi.sbf.com.foodjinniadmin.pojo.AreaPojo;
import shafi.sbf.com.foodjinniadmin.pojo.ConfirmOrder;
import shafi.sbf.com.foodjinniadmin.pojo.RestaurantDetails;


public class MainActivity extends NaviBase implements ActiveOrderAdapter.OnGetListener,FinishOrderAdapter.OnFinishyListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtollbarText=(TextView) findViewById(R.id.toolbar_text_base);
        mtollbarText.setText("Home");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);




        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ActiveFragment()).commit();
        }


    }






    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_Active:
                            selectedFragment = new ActiveFragment();
                            break;
                        case R.id.nav_Finished:
                            selectedFragment = new FinishFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public void onGet(ConfirmOrder confirmOrder, int position) {
        Intent intent = new Intent(MainActivity.this,OrderFoodDetails.class);
        intent.putExtra("id","active");
        intent.putExtra("valu",confirmOrder);
        intent.putExtra("no",String.valueOf(position));
        startActivity(intent);
    }

    @Override
    public void onFinish(ConfirmOrder confirmOrder, int position) {
        Intent intent = new Intent(MainActivity.this,OrderFoodDetails.class);
        intent.putExtra("id","finish");
        intent.putExtra("valu",confirmOrder);
        intent.putExtra("no",String.valueOf(position));
        startActivity(intent);
    }


//    @Override
//    public void onGet(ConfirmOrder confirmOrder,int position) {
//
//        Intent intent = new Intent(Home.this,OrderFoodList.class);
//        intent.putExtra("id","active");
//        intent.putExtra("valu",confirmOrder);
//        intent.putExtra("no",String.valueOf(position));
//        startActivity(intent);
//    }
//
//    @Override
//    public void onGettk(ConfirmOrder confirmOrder, int position) {
//
//        Intent intent = new Intent(Home.this,OrderFoodList.class);
//        intent.putExtra("id","kitchen");
//        intent.putExtra("valu",confirmOrder);
//        intent.putExtra("no",String.valueOf(position));
//        startActivity(intent);
//
//    }
//
//    @Override
//    public void onFinish(ConfirmOrder confirmOrder, int position) {
//
//        Intent intent = new Intent(Home.this,OrderFoodList.class);
//        intent.putExtra("id","Finish");
//        intent.putExtra("valu",confirmOrder);
//        intent.putExtra("no",String.valueOf(position));
//        startActivity(intent);
//
//    }
}