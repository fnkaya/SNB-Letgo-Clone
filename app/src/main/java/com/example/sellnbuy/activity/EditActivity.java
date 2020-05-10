package com.example.sellnbuy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";

    private ImageView image;
    private EditText edtPrice, edtTitle, edtDescription;
    private Toolbar toolbar;

    private Post post;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        image = findViewById(R.id.image_edit);
        edtPrice = findViewById(R.id.editText_price_edit);
        edtTitle = findViewById(R.id.editText_title_edit);
        edtDescription = findViewById(R.id.editText_description_edit);
        Button btnEdit = findViewById(R.id.button_edit);
        toolbar = findViewById(R.id.toolbar_edit);

        Intent intent = getIntent();
        post = intent.getParcelableExtra(Post.COLLECTION_NAME);
        postId = intent.getStringExtra(Post.POST_ID);

        fillViews();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setPrice(edtPrice.getText().toString());
                post.setTitle(edtTitle.getText().toString());
                post.setDescription(edtDescription.getText().toString());

                FirebaseFirestore.getInstance()
                        .collection(Post.COLLECTION_NAME)
                        .document(postId)
                        .set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditActivity.this, R.string.post_updated, Toast.LENGTH_SHORT).show();
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
        });
    }

    private void fillViews(){
        Glide.with(this)
                .load(post.getImageUri())
                .into(image);
        toolbar.setTitle(post.getTitle());
        edtDescription.setText(post.getDescription());
        edtPrice.setText(post.getPrice());
        edtTitle.setText(post.getTitle());
    }
}
