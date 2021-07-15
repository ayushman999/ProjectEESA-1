package com.example.projecteesa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projecteesa.Posts.Comment;
import com.example.projecteesa.R;
import com.example.projecteesa.utils.TimeUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerCommentAdapter extends FirestoreRecyclerAdapter<Comment,RecyclerCommentAdapter.CommentHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     *
     */
    Context context;
    public RecyclerCommentAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Comment> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull RecyclerCommentAdapter.CommentHolder holder, int position, @NonNull @NotNull Comment model) {

        holder.cMsg.setText(model.getMessage());
        holder.userName.setText(model.getUserID());
        holder.cTime.setText(TimeUtils.getTime(model.getTime()));
        Glide.with(context).load(model.getImageURL()).into(holder.comment_pic);
    }

    @NonNull
    @NotNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View myView= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view,parent,false);
        RecyclerCommentAdapter.CommentHolder cHolder=new RecyclerCommentAdapter.CommentHolder(myView);
        return cHolder;
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        CircleImageView comment_pic;
        TextView userName;
        TextView cMsg, cTime;

        public CommentHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            comment_pic = itemView.findViewById(R.id.cimg);
            userName = itemView.findViewById(R.id.cuser);
            cMsg = itemView.findViewById(R.id.cMsg);
            cTime = itemView.findViewById(R.id.time);
        }
    }
}
