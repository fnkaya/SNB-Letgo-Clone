package com.example.sellnbuy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Favorite;
import com.example.sellnbuy.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WatchListAdapter extends FirestoreRecyclerAdapter<Favorite, WatchListAdapter.ViewHolder> {

    private static final String TAG = "WatchListAdapter";

    private Context context;
    private WatchListListener watchListListener;

    public WatchListAdapter(@NonNull FirestoreRecyclerOptions<Favorite> options, Context context, WatchListListener watchListListener) {
        super(options);
        this.context = context;
        this.watchListListener = watchListListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Favorite model) {
        final String postId = model.getPostId();

        FirebaseFirestore.getInstance()
                .collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot.exists()){
                            Post post = snapshot.toObject(Post.class);
                            Glide.with(context)
                                    .load(post.getImageUri())
                                    .into(holder.imgPost);
                            holder.txtTitle.setText(post.getTitle());
                            holder.txtPrice.setText(post.getPrice() + "₺");
                            holder.txtDescription.setText(post.getDescription());
                        }
                        else{
                            holder.txtTitle.setText(R.string.post_has_been_deleted);
                            holder.txtTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
                            holder.txtPrice.setVisibility(View.GONE);
                            holder.txtDescription.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_watch_list, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPost, imgFavorite;
        TextView txtTitle, txtPrice, txtDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.image_post_wl);
            txtTitle = itemView.findViewById(R.id.text_title_wl);
            txtPrice = itemView.findViewById(R.id.text_price_wl);
            txtDescription = itemView.findViewById(R.id.text_description_wl);
            imgFavorite = itemView.findViewById(R.id.image_remove_wl);

            imgFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Remove Favorite")
                            .setMessage(R.string.remove_from_watchlist)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                                    Favorite favorite = snapshot.toObject(Favorite.class);
                                    watchListListener.handleRemoveFavorite(favorite.getPostId());
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
                    Favorite favorite = snapshot.toObject(Favorite.class);
                    watchListListener.handleShowDetail(favorite.getPostId());
                }
            });
        }
    }

    public interface WatchListListener {
        void handleShowDetail(String postId);
        void handleRemoveFavorite(String postId);
    }
}
