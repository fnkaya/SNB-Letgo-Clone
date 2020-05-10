package com.example.sellnbuy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MyPostAdapter extends FirestoreRecyclerAdapter<Post, MyPostAdapter.ViewHolder> {

    private static final String TAG = "MyPostAdapter";

    private Context context;
    private MyPostListener myPostListener;

    public MyPostAdapter(@NonNull FirestoreRecyclerOptions<Post> options, Context context, MyPostListener myPostListener) {
        super(options);
        this.context = context;
        this.myPostListener = myPostListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post model) {
        Glide.with(context)
                .load(model.getImageUri())
                .into(holder.imgPost);
        holder.txtTitle.setText(model.getTitle());
        holder.txtPrice.setText(model.getPrice());
        CharSequence csTime = DateFormat.format("MMM d, yyyy h:mm a", model.getTime().toDate());
        holder.txtTime.setText(csTime);
        if (model.getIsAvailable()) {
            holder.imgSelling.setVisibility(View.VISIBLE);
            holder.imgSold.setVisibility(View.GONE);
        }
        else {
            holder.imgSold.setVisibility(View.VISIBLE);
            holder.imgSelling.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_mypost, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPost, imgSold, imgSelling, imgDelete;
        TextView txtTitle, txtPrice, txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.image_post_wl);
            txtTitle = itemView.findViewById(R.id.text_title_wl);
            txtPrice = itemView.findViewById(R.id.text_price_wl);
            txtTime = itemView.findViewById(R.id.text_description_wl);
            imgSold = itemView.findViewById(R.id.image_on_sold);
            imgSelling = itemView.findViewById(R.id.image_on_selling);
            imgDelete = itemView.findViewById(R.id.image_remove_wl);

            imgSelling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.change_post_state)
                            .setMessage(R.string.changing_post_state)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    imgSold.setVisibility(View.VISIBLE);
//                                    imgSelling.setVisibility(View.GONE);
                                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                                    myPostListener.handleStateChanged(snapshot);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            imgSold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.change_post_state)
                            .setMessage(R.string.post_publish_again)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    imgSelling.setVisibility(View.VISIBLE);
//                                    imgSold.setVisibility(View.GONE);
                                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                                    myPostListener.handleStateChanged(snapshot);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.deleting_post)
                            .setMessage(R.string.deletin_post_confirm)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                                    myPostListener.handleDeletePost(snapshot);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    myPostListener.handleEditPost(snapshot);
                }
            });
        }
    }

    public interface MyPostListener {
        void handleStateChanged(DocumentSnapshot snapshot);
        void handleEditPost(DocumentSnapshot snapshot);
        void handleDeletePost(DocumentSnapshot snapshot);
    }
}
