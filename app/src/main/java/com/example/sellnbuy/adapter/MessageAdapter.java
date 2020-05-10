package com.example.sellnbuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder> {

        private static final int MSG_TYPE_LEFT = 0;
        private static final int MSG_TYPE_RIGHT = 1;

        private Context context;
        private FirebaseUser user;

        public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, Context context) {
                super(options);
                user = FirebaseAuth.getInstance().getCurrentUser();
                this.context = context;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if (viewType == MSG_TYPE_RIGHT)
                        view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
                else
                        view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
                return new ViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message model) {
                holder.show_message.setText(model.getText());
        }


        public class ViewHolder extends RecyclerView.ViewHolder{

                TextView show_message;

                public ViewHolder(@NonNull View itemView) {
                        super(itemView);

                        show_message = itemView.findViewById(R.id.show_message);
                }
        }

        @Override
        public int getItemViewType(int position) {
                if (getSnapshots().getSnapshot(position).toObject(Message.class).getFrom().equals(user.getUid()))
                        return MSG_TYPE_RIGHT;
                else
                        return MSG_TYPE_LEFT;
        }


}
