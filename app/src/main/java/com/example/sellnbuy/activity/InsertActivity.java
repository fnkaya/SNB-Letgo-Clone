package com.example.sellnbuy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static android.view.View.GONE;

public class InsertActivity extends AppCompatActivity {

    private static final String TAG = "InsertActivity";

    private ImageView imgPost;
    private Spinner spnCategory, spnCity, spnCondition;
    private EditText edtTitle, edtPrice, edtDescription;
    private Switch switchFree;
    private Button btnLocation, btnPost;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    private FirebaseAuth mAuth;
    private StorageTask uploadTask;

    private Bitmap bitmap;
    private static final int TAKE_IMAGE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        init();
        listenActions();
    }



    private void init() {
        imgPost = findViewById(R.id.image_post);
        spnCategory = findViewById(R.id.spinner_category);
        edtTitle = findViewById(R.id.editText_title);
        edtPrice = findViewById(R.id.editText_price);
        switchFree = findViewById(R.id.switch_free);
        spnCondition = findViewById(R.id.spinner_condition);
        edtDescription = findViewById(R.id.editText_description);
        spnCity = findViewById(R.id.spinner_city);
        btnLocation = findViewById(R.id.button_location);
        progressBar = findViewById(R.id.progressBar_insert);
        btnPost = findViewById(R.id.button_insert);
        toolbar = findViewById(R.id.toolbar_insert);
        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(categoryAdapter);
        ArrayAdapter conditionAdapter = ArrayAdapter.createFromResource(this, R.array.condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCondition.setAdapter(conditionAdapter);
        ArrayAdapter cityAdapter = ArrayAdapter.createFromResource(this, R.array.city, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCity.setAdapter(cityAdapter);
    }



    private void listenActions() {
        switchFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtPrice.setText("");
                    edtPrice.setVisibility(GONE);

                } else
                    edtPrice.setVisibility(View.VISIBLE);
            }
        });

        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePostImage();
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InsertActivity.this, MapsActivity.class));
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadTask != null && uploadTask.isInProgress())
                    Toast.makeText(InsertActivity.this, R.string.upload_in_progress, Toast.LENGTH_SHORT).show();
                else {
                    if (validate() && bitmap != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        uploadPostImage();
                    } else
                        Toast.makeText(InsertActivity.this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private boolean validate() {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String price = edtPrice.getText().toString();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && (switchFree.isChecked() || !TextUtils.isEmpty(price)))
            return true;
        return false;
    }



    private void handlePostImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imgPost.setImageBitmap(bitmap);
        }
    }

    private void uploadPostImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child(Post.POST_IMAGES)
                .child(System.currentTimeMillis() + Post.POST_IMAGE_FORMAT);
        uploadTask = reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Post post = createObject(String.valueOf(uri));
                                        addPost(post);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }

    private Post createObject(String imageUri) {
        String ownerId = mAuth.getCurrentUser().getUid();
        String ownerName = mAuth.getCurrentUser().getDisplayName();
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String price = edtPrice.getText().toString();
        String condition = spnCondition.getSelectedItem().toString();
        String category = spnCategory.getSelectedItem().toString();
        String city = spnCity.getSelectedItem().toString();

        return new Post(ownerId, ownerName, title, description, price, condition, category, city, imageUri);
    }

    private void addPost(Post post) {
        FirebaseFirestore.getInstance()
                .collection(Post.COLLECTION_NAME)
                .add(post)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(InsertActivity.this, R.string.post_added_successfully, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }
}
