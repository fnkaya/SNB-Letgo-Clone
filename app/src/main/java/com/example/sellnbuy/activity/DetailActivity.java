package com.example.sellnbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Favorite;
import com.example.sellnbuy.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private ImageView imagePost, imageFavoriteWhite, imageFavoriteRed;
    private TextView txtDescription, txtPrice, txtCondition, txtTitle, txtCity;
    private Button btnSendMessage;

    private FirebaseUser user;
    private Post post;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        user = FirebaseAuth.getInstance().getCurrentUser();
        imagePost = findViewById(R.id.image_post);
        imageFavoriteWhite = findViewById(R.id.image_favorite_white);
        imageFavoriteRed = findViewById(R.id.image_favorite_red);
        txtDescription = findViewById(R.id.text_description_detail);
        txtPrice = findViewById(R.id.text_price_detail);
        txtCondition = findViewById(R.id.text_condition_detail);
        txtTitle = findViewById(R.id.text_title_detail);
        txtCity = findViewById(R.id.text_city_detail);
        btnSendMessage = findViewById(R.id.button_send_message);

        Intent intent = getIntent();
        post = intent.getParcelableExtra(Post.COLLECTION_NAME);
        postId = intent.getStringExtra(Post.POST_ID);

        fillViews();

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               handleChatOrEdit();
            }
        });

        imageFavoriteWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavorite();
            }
        });

        imageFavoriteRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFavorite();
            }
        });
    }

    private void fillViews(){
        Glide.with(this)
                .load(post.getImageUri())
                .into(imagePost);
        txtDescription.setText(post.getDescription());
        txtPrice.setText(String.format("%s₺", post.getPrice()));
        txtCondition.setText(post.getCondition());
        txtTitle.setText(post.getTitle());
        txtCity.setText(post.getCity());

        if (user.getUid().equals(post.getOwnerId())) {
            btnSendMessage.setText(R.string.edit_your_post);
            imageFavoriteRed.setVisibility(View.GONE);
            imageFavoriteWhite.setVisibility(View.GONE);
        }
        else{
            btnSendMessage.setText(R.string.send_message);
            handleFavoriteImage();
        }
    }

    private void handleChatOrEdit(){
        if (user.getUid().equals(post.getOwnerId())){
            Intent intent = new Intent(DetailActivity.this, EditActivity.class);
            intent.putExtra(Post.COLLECTION_NAME, post);
            intent.putExtra(Post.POST_ID, postId);
            startActivity(intent);
        }
        else{
            Intent intentMessage = new Intent(DetailActivity.this, ChatActivity.class);
            intentMessage.putExtra(Post.OWNER_ID, post.getOwnerId());
            intentMessage.putExtra(Post.OWNER_NAME, post.getOwnerName());
            intentMessage.putExtra(Post.POST_ID, postId);
            startActivity(intentMessage);
        }
    }

    private void addFavorite(){
        Favorite favorite = new Favorite(postId, user.getUid());

        FirebaseFirestore.getInstance()
                .collection(Favorite.COLLECTION_NAME)
                .document(user.getUid())
                .collection(Post.COLLECTION_NAME)
                .add(favorite)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        Toast.makeText(DetailActivity.this, R.string.post_added_favorite, Toast.LENGTH_SHORT).show();
                        imageFavoriteWhite.setVisibility(View.GONE);
                        imageFavoriteRed.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void removeFavorite(){
        FirebaseFirestore.getInstance()
                .collection(Favorite.COLLECTION_NAME)
                .document(user.getUid())
                .collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.POST_ID, postId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList)
                            if (snapshot.exists())
                                snapshot.getReference().delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(DetailActivity.this, R.string.post_removed_favorite, Toast.LENGTH_SHORT).show();
                                            imageFavoriteWhite.setVisibility(View.VISIBLE);
                                            imageFavoriteRed.setVisibility(View.GONE);
                                        }
                                    });
                    }
                });
    }

    private void handleFavoriteImage(){
        FirebaseFirestore.getInstance()
                .collection(Favorite.COLLECTION_NAME)
                .document(user.getUid())
                .collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.POST_ID, postId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()){
                            imageFavoriteWhite.setVisibility(View.VISIBLE);
                            imageFavoriteRed.setVisibility(View.GONE);
                        }else{
                            imageFavoriteWhite.setVisibility(View.GONE);
                            imageFavoriteRed.setVisibility(View.VISIBLE);
                        }

                    }
                });
    }
}
