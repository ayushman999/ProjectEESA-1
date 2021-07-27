package com.example.projecteesa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projecteesa.Posts.Post;
import com.example.projecteesa.R;
import com.example.projecteesa.utils.AccountsUtil;
import com.example.projecteesa.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.zolad.zoominimageview.ZoomInImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    ArrayList<Post> posts;
    Context context;
    PostItemClicked listener;
    static ArrayList<String> savedPosts;

    public PostAdapter(ArrayList<Post> posts, Context context, PostItemClicked listener) {
        this.posts = posts;
        this.context = context;
        this.listener = listener;
        if (AccountsUtil.fetchData() != null) {
            savedPosts = AccountsUtil.fetchData().getSavedPost();
        }
    }

    @NonNull
    @NotNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_element, parent, false);
        PostHolder holder = new PostHolder(myView);
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> likes = posts.get(holder.getAdapterPosition()).getLikes();
                String postID = posts.get(holder.getAdapterPosition()).getPostId();
                String uid = FirebaseAuth.getInstance().getUid();
                if (likes.contains(uid)) {
                    likes.remove(uid);
                    listener.onLikeClicked(likes, postID);
                    holder.likeBtn.setImageResource(R.drawable.ic_like_border);
                    if (likes.size() < 2) {
                        holder.likes.setText(likes.size() + " like");
                    } else {
                        holder.likes.setText(likes.size() + " likes");
                    }
                } else {
                    likes.add(uid);
                    listener.onLikeClicked(likes, postID);
                    holder.likeBtn.setImageResource(R.drawable.ic_like);
                    if (likes.size() < 2) {
                        holder.likes.setText(likes.size() + " like");
                    } else {
                        holder.likes.setText(likes.size() + " likes");
                    }
                }
            }
        });
        holder.bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert AccountsUtil.fetchData() != null;
                String path = "AllPost/" + posts.get(holder.getAdapterPosition()).getPostId();
                if (savedPosts == null) {
                    savedPosts = new ArrayList<>();
                    savedPosts.add(path);
                    holder.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_black);
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                    listener.onBookmarkClicked(savedPosts, AccountsUtil.getUID());
                    return;
                }
                if (savedPosts.contains(path)) {
                    savedPosts.remove(path);
                    holder.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border);
                    Toast.makeText(context, "Unsaved!", Toast.LENGTH_SHORT).show();

                    listener.onBookmarkClicked(savedPosts, AccountsUtil.getUID());
                } else {
                    savedPosts.add(path);
                    holder.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_black);
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                    listener.onBookmarkClicked(savedPosts, AccountsUtil.getUID());
                }

            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postID = posts.get(holder.getAdapterPosition()).getPostId();
                listener.onCommentClicked(postID);
            }
        });
        holder.mainCard.setOnClickListener(v -> {
            String postID = posts.get(holder.getAdapterPosition()).getPostId();
            listener.onCommentClicked(postID);
        });
        holder.likes.setOnClickListener(v ->{
            String postID=posts.get(holder.getAdapterPosition()).getPostId();
            listener.onNumLikesClicked(postID);
        });
        return holder;
    }

    public void setData(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.PostHolder holder, int position) {
        holder.mainCard.setAnimation(AnimationUtils.loadAnimation(context, R.anim.post_animation));
        Post post = posts.get(position);
        ArrayList<String> likes = post.getLikes();
        ArrayList<String> saved;
        if (AccountsUtil.fetchData() != null)
            saved = AccountsUtil.fetchData().getSavedPost();
        else
            saved = new ArrayList<>();
        holder.postHeader.setText(post.getName());
        Glide.with(context).load(post.getImageUrl()).
                placeholder(R.drawable.ic_baseline_person_24).into(holder.postImg);
        if (likes.size() < 2) {
            holder.likes.setText(likes.size()+"" );
        } else {
            holder.likes.setText(likes.size()+"" );
        }
        holder.captionHeader.setText(post.getName() + ": ");
        holder.caption.setText(post.getCaption());
        if (post.getUserImg() != null && !(post.getUserImg().isEmpty()))
            Glide.with(context).load(post.getUserImg())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(holder.postProfileHeader);
        else holder.postProfileHeader.setImageResource(R.drawable.ic_baseline_person_24);
        if (post.getLikes().contains(AccountsUtil.getUID())) {
            holder.likeBtn.setImageResource(R.drawable.ic_like);
        } else {
            holder.likeBtn.setImageResource(R.drawable.ic_like_border);
        }
        if (saved != null && saved.contains("AllPost/" + post.getPostId())) {
            holder.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_black);
        } else {
            holder.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border);
        }
        holder.postHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOwnerProfileClicked(post.getUid());
            }
        });
        holder.postTime.setText(TimeUtils.getTime(post.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        ZoomInImageView postImg;
        ImageView  postProfileHeader, likeBtn, commentBtn, bookmarkBtn;
        TextView caption, likes, captionHeader, postHeader, postTime;
        CardView mainCard;
        RelativeLayout postHeaderLayout;

        public PostHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            postProfileHeader = itemView.findViewById(R.id.post_header_img);
            postHeader = itemView.findViewById(R.id.post_header);
            postImg = itemView.findViewById(R.id.post_image);
            likeBtn = itemView.findViewById(R.id.post_like_btn);
            commentBtn = itemView.findViewById(R.id.post_comment_btn);
            likes = itemView.findViewById(R.id.like_number_text);
            captionHeader = itemView.findViewById(R.id.caption_header);
            caption = itemView.findViewById(R.id.post_caption);
            mainCard = itemView.findViewById(R.id.mainCard);
            bookmarkBtn = itemView.findViewById(R.id.post_save_btn);
            postHeaderLayout = itemView.findViewById(R.id.post_header_layout);
            postTime = itemView.findViewById(R.id.post_time);
        }
    }
}


