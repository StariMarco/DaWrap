package Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.example.dawrap.R;

import java.util.List;

import Models.Comment;
import Models.User;
import Singletons.DataHelper;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>
{
    public interface OnItemClickListener
    {
        void onLikeClick(TextView likeTxt, ImageButton btn, int position);
        void onProfileImageClick(int position);
        void onDeleteClick(int position);
    }

    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener) { mListener = listener; }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView ProfileImage;
        public TextView UsernameTextView;
        public TextView TextTextView;
        public TextView LikesTextView;
        public ImageButton LikeButton;
        public TextView DeleteBtn;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            // Get views from comment_layout.xml
            ProfileImage = itemView.findViewById(R.id.comment_profileImg);
            UsernameTextView = itemView.findViewById(R.id.lbl_commentUsername);
            TextTextView = itemView.findViewById(R.id.lbl_commentText);
            LikesTextView = itemView.findViewById(R.id.lbl_commentLikes);
            LikeButton = itemView.findViewById(R.id.btnCommentLike);
            DeleteBtn = itemView.findViewById(R.id.btn_delete_comment);

            // Click listener for comment like button
            LikeButton.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {
                        listener.onLikeClick(LikesTextView, LikeButton, position);
                    }
                }
            });

            // Click listener for profile image
            ProfileImage.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {
                        listener.onProfileImageClick(position);
                    }
                }
            });

            // Click listener for delete button
            DeleteBtn.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    private List<Comment> comments;

    public CommentAdapter(final List<Comment> comments)
    {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View commentView = inflater.inflate(R.layout.comment_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(commentView, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Get the comment to bind at this row
        Comment comment = comments.get(position);
        // Get the user who created this comment
        User user = DataHelper.getUserById(comment.UserId);

        User currentUser = DataHelper.getCurrentUser();
        if(user == null)
        {
            Log.e("CommentAdapter", "User not found");
            return;
        }

        // Set all properties
        String likes = comment.getLikesCount() + " \"like\"";

        holder.ProfileImage.setImageResource(user.ProfileImage);
        holder.LikesTextView.setText(likes);
        holder.TextTextView.setText(comment.Text);
        holder.UsernameTextView.setText(user.Username);

        if(comment.hasUserLikedThisComment(currentUser.UserId))
            holder.LikeButton.setImageResource(R.drawable.ic_favorite_white_24dp);

        holder.ProfileImage.setOnItemSelectedClickListener(new ItemSelectedListener()
        {
            @Override
            public void onSelected(View view)
            {
                return;
            }

            @Override
            public void onUnselected(View view)
            {
                return;
            }
        });

        if(currentUser.UserId.equals(comment.UserId))
            holder.DeleteBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount()
    {
        return comments.size();
    }
}
