package shafi.sbf.com.foodjinniadmin.Registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import shafi.sbf.com.foodjinniadmin.MainActivity;
import shafi.sbf.com.foodjinniadmin.R;
import shafi.sbf.com.foodjinniadmin.pojo.AreaPojo;
import shafi.sbf.com.foodjinniadmin.pojo.RestaurantDetails;

public class SignUp extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference areaRef;



    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private String restaurantID, photoLink, name, registrationNumber, type, phone, email, password, rePassword, pass;

    private Uri ImagUrl_main;

    private ImageView addImage;

    private EditText nameEdit, registerEdt, typeEdt, phoneEdt, emailEdt, passEdt, rePassEdt;

    private boolean isPermistionGranted = false;


    private CardView registration;

    private ProgressDialog mProgress;

    String area;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Init FireBase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();


        addImage = findViewById(R.id.addimagebtn);

        mProgress = new ProgressDialog(this);

        nameEdit = findViewById(R.id.restaurant_name);
        registerEdt = findViewById(R.id.registerNumber);
        typeEdt = findViewById(R.id.ra_type);
        phoneEdt = findViewById(R.id.phone_number);
        emailEdt = findViewById(R.id.email);
        passEdt = findViewById(R.id.password);
        rePassEdt = findViewById(R.id.re_password);

        area = getIntent().getStringExtra("area");

        registration = findViewById(R.id.foreign_doctor_register);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermistion();
                if (isPermistionGranted) {
                    openGallery();
                } else {
                    Toast.makeText(SignUp.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reg();
            }
        });

    }

    private void reg() {
        if (ImagUrl_main == null) {
            Toast.makeText(SignUp.this, "Select a picture for Restaurant", Toast.LENGTH_SHORT).show();
            return;
        }

        name = nameEdit.getText().toString();
        if (name.isEmpty()) {
            nameEdit.setError("Enter name");
            nameEdit.requestFocus();
            return;
        }

        registrationNumber = registerEdt.getText().toString();
        if (registrationNumber.isEmpty()) {
            registerEdt.setError("Enter Registration  Number");
            registerEdt.requestFocus();
            return;
        }
        type = typeEdt.getText().toString();
        if (type.isEmpty()) {
            typeEdt.setError("Enter Restaurant  Type");
            typeEdt.requestFocus();
            return;
        }
        phone = phoneEdt.getText().toString();
        if (phone.isEmpty()) {
            phoneEdt.setError("Enter phone");
            phoneEdt.requestFocus();
            return;
        }

        email = emailEdt.getText().toString();
        if (email.isEmpty()) {
            emailEdt.setError("Enter email");
            emailEdt.requestFocus();
            return;
        }

        password = passEdt.getText().toString();
        if (password.isEmpty()) {
            passEdt.setError("Enter password");
            passEdt.requestFocus();
            return;
        }

        rePassword = rePassEdt.getText().toString();
        if (rePassword.isEmpty()) {
            rePassEdt.setError("Enter password");
            rePassEdt.requestFocus();
            return;
        }

        if (!(rePassword.equals(password))) {
            Toast.makeText(SignUp.this, "Password does not match", Toast.LENGTH_SHORT).show();
            rePassEdt.setError("Enter same password");
            rePassEdt.requestFocus();
            return;
        }

        mProgress.setMessage("Loading....");
        mProgress.show();

        registerUser();
    }


    private void registerUser() {


        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mProgress.dismiss();
                                firebaseAuth = FirebaseAuth.getInstance();
                                rootRef = FirebaseDatabase.getInstance().getReference();

                                user = firebaseAuth.getCurrentUser();

                                userRef = rootRef.child("Restaurant").child(area).child(user.getUid());
                                areaRef = rootRef.child("Restaurant").child("Area").child(user.getUid());

                                restaurantID = String.valueOf(user.getUid());


                                userRegistration();
                            } else {
                                mProgress.dismiss();
                                Toast.makeText(SignUp.this, "Failed to register...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (Exception e) {
            mProgress.dismiss();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void userRegistration() {

        RestaurantDetails details = new RestaurantDetails(restaurantID, name, type, photoLink, phone, email, password,area);
        userRef.child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    AreaPojo areaPojo = new AreaPojo(area);
                    areaRef.setValue(areaPojo);

                    Toast.makeText(SignUp.this, "Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                    intent.putExtra("area", area);
                    mProgress.dismiss();
                    startActivity(intent);
                    finish();

                } else {
                    mProgress.dismiss();
                    Toast.makeText(SignUp.this, "Failed to registration", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE
                && data != null && data.getData() != null) {
            ImagUrl_main = data.getData();
            if (ImagUrl_main != null) {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading...");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("image/" + imageName);
                imageFolder.putFile(ImagUrl_main)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();

                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Toast.makeText(SignUp.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                        photoLink = uri.toString();

                                    }
                                });


                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Uploaded " + progress);
                            }
                        });
            }

            addImage.setImageURI(ImagUrl_main);
        }
    }

    private void checkPermistion() {
        if ((ActivityCompat
                .checkSelfPermission(SignUp.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat
                        .checkSelfPermission(SignUp.this
                                , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(SignUp.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, PERMISSION_CODE);

            openGallery();

        } else {
            isPermistionGranted = true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {

            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
            )) {
                isPermistionGranted = true;
            } else {
                checkPermistion();
            }
        }
    }
}
