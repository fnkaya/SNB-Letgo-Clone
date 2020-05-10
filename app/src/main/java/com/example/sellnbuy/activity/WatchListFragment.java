package com.example.sellnbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellnbuy.R;
import com.example.sellnbuy.activity.DetailActivity;
import com.example.sellnbuy.adapter.PostAdapter;
import com.example.sellnbuy.adapter.WatchListAdapter;
import com.example.sellnbuy.model.Favorite;
import com.example.sellnbuy.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class WatchListFragment extends Fragment implements WatchListAdapter.WatchListListener {

    private static final String TAG = "WatchListFragment";

    private RecyclerView recyclerView;
    private WatchListAdapter adapter;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_watch_list, container, false);
        recyclerView = root.findViewById(R.id.recycler_watch_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();

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
                .collection(Favorite.COLLECTION_NAME)
                .document(user.getUid())
                .collection(Post.COLLECTION_NAME);

        FirestoreRecyclerOptions<Favorite> options = new FirestoreRecyclerOptions.Builder<Favorite>()
                .setQuery(query, Favorite.class)
                .build();

        adapter = new WatchListAdapter(options, getContext(), this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void handleShowDetail(final String postId) {
        FirebaseFirestore.getInstance()
                .collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Post post = documentSnapshot.toObject(Post.class);
                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra(Post.COLLECTION_NAME, post);
                            intent.putExtra(Post.POST_ID, documentSnapshot.getReference().getId());
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void handleRemoveFavorite(String postId) {
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
                                                Toast.makeText(getContext(), R.string.post_removed_favorite, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                    }
                });
    }
}
