package com.example.sellnbuy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.sellnbuy.adapter.ChannelAdapter;
import com.example.sellnbuy.model.Channel;
import com.example.sellnbuy.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment implements ChannelAdapter.ChannelListener {

    private static final String TAG = "ChatFragment";

    private RecyclerView recyclerView;
    private ChannelAdapter adapter;

    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = root.findViewById(R.id.recycler_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();

        listChannel();

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

    private void listChannel(){
        Query query = FirebaseFirestore.getInstance()
                .collection(Channel.COLLECTION_NAME)
                .whereEqualTo(Channel.SENDER_ID, user.getUid());
//                .orderBy(Channel.TIME, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Channel> options = new FirestoreRecyclerOptions.Builder<Channel>()
                .setQuery(query, Channel.class)
                .build();

        adapter = new ChannelAdapter(options, getContext(),this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void handleGetMessages(DocumentSnapshot snapshot) {
        Channel channel = snapshot.toObject(Channel.class);
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra(Post.OWNER_ID, channel.getReceiverId());
        intent.putExtra(Post.OWNER_NAME, channel.getReceiverName());
        intent.putExtra(Post.POST_ID, channel.getPostId());
        startActivity(intent);
    }

    @Override
    public void handleDeleteChat(final DocumentSnapshot snapshot) {
        new AlertDialog.Builder(getContext())
                .setMessage("Messages will be deleted. Do you confirm?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        snapshot.getReference()
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Chat Silindi", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .show();

    }


}

