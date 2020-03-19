package shafi.sbf.com.foodjinniadmin.Registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import shafi.sbf.com.foodjinniadmin.R;

public class AreaSelection extends AppCompatActivity {

    Spinner Area;

    String areaS = "null";

    private CardView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_selection);

        Area = findViewById(R.id.select_area);

        next = findViewById(R.id.nextB);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });

        ArrayAdapter Adapter = ArrayAdapter.createFromResource(AreaSelection.this, R.array.select_area, R.layout.spinner_item_select_model);
        Adapter.setDropDownViewResource(R.layout.spinner_item_select_model);
        Area.setAdapter(Adapter);

        Area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    areaS = Area.getItemAtPosition(i).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void nextPage() {

        if (areaS.equals("null")){
            Toast.makeText(this, "must select country......!", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(AreaSelection.this,SignUp.class);
            intent.putExtra("area",areaS);
            startActivity(intent);
            finish();

        }

    }
}
