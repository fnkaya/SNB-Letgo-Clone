package com.example.sellnbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellnbuy.R;
import com.example.sellnbuy.activity.EditActivity;
import com.example.sellnbuy.adapter.MyPostAdapter;
import com.example.sellnbuy.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyPostFragment extends Fragment implements MyPostAdapter.MyPostListener {

    private static final String TAG = "MyPostFragment";

    private RecyclerView recyclerView;
    private MyPostAdapter adapter;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mypost, container, false);

        recyclerView = root.findViewById(R.id.recycler_mypost);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initRecyclerView();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null)
            adapter.stopListening();
    }

    private void initRecyclerView(){
        Query query = FirebaseFirestore.getInstance()
                .collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.OWNER_ID, user.getUid());
//                .orderBy(Post.TIME, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new MyPostAdapter(options, getContext(), this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void handleStateChanged(final DocumentSnapshot snapshot) {
        snapshot.getReference().update(Post.IS_AVAILABLE, !snapshot.getBoolean(Post.IS_AVAILABLE))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), R.string.post_updated, Toast.LENGTH_SHORT).show();
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    @Override
    public void handleEditPost(final DocumentSnapshot snapshot) {
        Post post = snapshot.toObject(Post.class);
        Intent intent = new Intent(getActivity(), EditActivity.class);
        intent.putExtra(Post.COLLECTION_NAME, post);
        intent.putExtra(Post.POST_ID, snapshot.getReference().getId());
        startActivity(intent);
    }

    @Override
    public void handleDeletePost(DocumentSnapshot snapshot) {
        snapshot.getReference().delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), R.string.post_deleted, Toast.LENGTH_SHORT).show();
//                        initRecyclerView();
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
