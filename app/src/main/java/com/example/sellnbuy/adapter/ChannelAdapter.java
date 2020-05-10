package com.example.sellnbuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Channel;
import com.example.sellnbuy.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChannelAdapter extends FirestoreRecyclerAdapter<Channel, ChannelAdapter.ViewHolder> {

        private static final String TAG = "ChannelAdapter";

        private Context context;

        private ChannelListener channelListener;

        public ChannelAdapter(@NonNull FirestoreRecyclerOptions<Channel> options, Context context, ChannelListener channelListener) {
                super(options);
                this.context = context;
                this.channelListener = channelListener;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_list, parent, false);

                return new ViewHolder(view);
        }


        @Override
        protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Channel model) {
                FirebaseFirestore.getInstance()
                        .collection(Post.COLLECTION_NAME)
                        .document(model.getPostId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                                Post post = documentSnapshot.toObject(Post.class);
                                                Glide.with(context)
                                                        .load(post.getImageUri())
                                                        .into(holder.img);
                                                holder.txtTitle.setText(post.getTitle());
                                                if (post.getIsAvailable()){
                                                        holder.txtState.setText(R.string.on_selling);
                                                        holder.txtState.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                                                }
                                                else{
                                                        holder.txtState.setText(R.string.on_sold);
                                                        holder.txtState.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                                }
                                        }
                                        else{
                                                holder.txtTitle.setText(R.string.post_has_been_deleted);
                                                holder.txtTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                                holder.txtState.setVisibility(View.GONE);
                                        }
                                }
                        });
                holder.txtFrom.setText(model.getReceiverName());
        }


        class ViewHolder extends RecyclerView.ViewHolder {

                ImageView img, imgDelete;
                TextView txtTitle, txtFrom, txtState;

                ViewHolder(@NonNull View itemView) {
                        super(itemView);

                        img = itemView.findViewById(R.id.image_post);
                        imgDelete = itemView.findViewById(R.id.image_delete_chat);
                        txtTitle = itemView.findViewById(R.id.text_post_title);
                        txtFrom = itemView.findViewById(R.id.text_from);
                        txtState = itemView.findViewById(R.id.text_post_state);

                        itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                                        channelListener.handleGetMessages(snapshot);
                                }
                        });

                        imgDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                                        channelListener.handleDeleteChat(snapshot);
                                }
                        });
                }


        }

        public interface ChannelListener {
                void handleGetMessages(DocumentSnapshot snapshot);
                void handleDeleteChat(DocumentSnapshot snapshot);
        }
}
