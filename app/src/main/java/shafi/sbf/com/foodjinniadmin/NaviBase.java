package shafi.sbf.com.foodjinniadmin;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import shafi.sbf.com.foodjinniadmin.Registration.LogIn;

public class NaviBase extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {


    private NavigationView navigationView;
    private DrawerLayout fullLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedNavItemId;
    TextView mtollbarText;
    private FirebaseAuth auth;


    int i = 0,j=0;


    @SuppressLint("InflateParams")
    @Override
    public void setContentView(@LayoutRes int layoutResID) {





        /**
         * This is going to be our actual root layout.
         */
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_navi_base, null);
        /**
         * {@link FrameLayout} to inflate the child's view. We could also use a {@link ViewStub}
         */
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.Activity_Contant);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        /**
         * Note that we don't pass the child's layoutId to the parent,
         * instead we pass it our inflated layout.
         */
        super.setContentView(fullLayout);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar = (Toolbar) findViewById(R.id.toolbar_base);
        }
        mtollbarText = (TextView) findViewById(R.id.toolbar_text_base);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

        View headerView = navigationView.getHeaderView(0);



        setUpNavView();

    }

    /**
     * Helper method that can be used by child classes to
     * specify that they don't want a {@link Toolbar}
     *
     * @return true
     */
    protected boolean useToolbar() {
        return true;
    }

    protected void setUpNavView() {
        navigationView.setNavigationItemSelectedListener(this);

        if (useDrawerToggle()) { // use the hamburger menu

            drawerToggle = new ActionBarDrawerToggle(this, fullLayout, toolbar,
                    R.string.nav_drawer_opened,
                    R.string.nav_drawer_closed);

            fullLayout.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        } else if (useToolbar() && getSupportActionBar() != null) {
            // Use home/back button instead
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }


    /**
     * Helper method to allow child classes to opt-out of having the
     * hamburger menu.
     *
     * @return
     */
    protected boolean useDrawerToggle() {
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        fullLayout.closeDrawer(GravityCompat.START);
        selectedNavItemId = menuItem.getItemId();

        return onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_foodList) {
            Intent intent = new Intent(this, FoodList.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.nav_AddFood) {
            Intent intent = new Intent(this, AddFood.class);
            intent.putExtra("up","new");
            intent.putExtra("cata","new");
            intent.putExtra("cataID","new");
            startActivity(intent);
            finish();

        }else if (id == R.id.nav_logOut) {
            auth.signOut();
            if(auth.getCurrentUser() == null)
            {
                startActivity(new Intent(this, LogIn.class));
                finish();
            }

        }

        /*else if (id == R.id.nav_call) {

         *//*MenuItem callA = findViewById(R.id.nav_call1);
            MenuItem callB = findViewById(R.id.nav_call2);

            if(j==0) {
                callA.setVisible(true);
                callB.setVisible(true);
                j=1;
            }
            if (j==1){
                callA.setVisible(true);
                callB.setVisible(true);
                j=0;
            }*//*


        }*/
/*        else if (id == R.id.nav_onlineA) {

            Intent intent = new Intent(this, LocationActivity.class);
            intent.putExtra("store", "Store A");
            startActivity(intent);

        }
        else if (id == R.id.nav_onlineB) {

            Intent intent = new Intent(this, LocationActivity.class);
            intent.putExtra("store", "Store B");
            startActivity(intent);


        }
        else if (id == R.id.nav_call1) {
            String uri = "tel:" + "+8801812583840".trim();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.

                }
            }
            startActivity(intent);

        }
        else if (id == R.id.nav_call2) {
            String uri = "tel:" + "+8801812583840".trim();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.


                }
            }
            startActivity(intent);

        }*/

        return super.onOptionsItemSelected(item);
    }
}
