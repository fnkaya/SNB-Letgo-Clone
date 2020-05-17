package com.example.sellnbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellnbuy.R;
import com.example.sellnbuy.adapter.MessageAdapter;
import com.example.sellnbuy.model.Post;
import com.example.sellnbuy.model.Channel;
import com.example.sellnbuy.model.Message;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private FloatingActionButton fabSend;
    private EditText edtMessage;
    private Toolbar toolbar;

    private MessageAdapter adapter;
    private RecyclerView recyclerView;


    private FirebaseUser user;
    private String postId;
    private String receiverId;
    private String receiverName;
    private String senderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        fabSend = findViewById(R.id.fab_send);
        edtMessage = findViewById(R.id.editText_message);
        toolbar = findViewById(R.id.toolbar_message);
        recyclerView = findViewById(R.id.recycler_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        receiverId = intent.getStringExtra(Post.OWNER_ID);
        receiverName = intent.getStringExtra(Post.OWNER_NAME);
        postId = intent.getStringExtra(Post.POST_ID);
        senderId = user.getUid();

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtMessage.getText().toString();

                if (!text.isEmpty()) {
                    sendMessage(text);
                    edtMessage.setText("");
                }
                else
                    Toast.makeText(MessageActivity.this, R.string.cant_send_empty_message, Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
         readMessages();
         handlePostTitle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();

    }

    private void sendMessage(final String text) {
        final Channel channel = new Channel(senderId, receiverId, receiverName, postId);

        FirebaseFirestore.getInstance()
                .collection(Channel.COLLECTION_NAME)
                .document(senderId+receiverId+ postId)
                .set(channel);

        final Message message = new Message(senderId, text);

        FirebaseFirestore.getInstance()
                .collection(Channel.COLLECTION_NAME)
                .document(senderId+receiverId+ postId)
                .collection(Message.COLLECTION_NAME)
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        addReverseChannel(message);
                    }
                });
    }

    private void addReverseChannel(Message message){
        String senderName = user.getDisplayName();
        Channel channel = new Channel(receiverId, senderId, senderName, postId);

        FirebaseFirestore.getInstance()
                .collection(Channel.COLLECTION_NAME)
                .document(receiverId+senderId+ postId)
                .set(channel);

        FirebaseFirestore.getInstance()
                .collection(Channel.COLLECTION_NAME)
                .document(receiverId+senderId+ postId)
                .collection(Message.COLLECTION_NAME)
                .add(message);
    }

    private void readMessages() {
        Query query = FirebaseFirestore.getInstance()
                .collection(Channel.COLLECTION_NAME)
                .document(senderId+receiverId+ postId)
                .collection(Message.COLLECTION_NAME)
                .orderBy(Channel.TIME);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        adapter = new MessageAdapter(options, getApplicationContext());

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void handlePostTitle(){
        FirebaseFirestore.getInstance()
                .collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                            toolbar.setTitle(documentSnapshot.toObject(Post.class).getTitle());
                        else {
                            toolbar.setTitle(R.string.post_has_been_deleted);
                            edtMessage.setEnabled(false);
                            fabSend.setEnabled(false);
                        }
                    }
                });
    }
}
