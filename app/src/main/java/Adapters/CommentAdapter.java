package Adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.example.dawrap.R;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import Models.Comment;
import Models.User;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>
{
    public interface OnItemClickListener
    {
        void onLikeClick(TextView likeTxt, ImageButton btn, int position);
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

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            ProfileImage = itemView.findViewById(R.id.comment_profileImg);
            UsernameTextView = itemView.findViewById(R.id.lbl_commentUsername);
            TextTextView = itemView.findViewById(R.id.lbl_commentText);
            LikesTextView = itemView.findViewById(R.id.lbl_commentLikes);
            LikeButton = itemView.findViewById(R.id.btnCommentLike);

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
        }
    }

    private List<Comment> comments;
    private List<User> users;

    public CommentAdapter(final List<Comment> comments, List<User> users)
    {
        this.comments = comments;
        this.users = users;
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
        Comment comment = comments.get(position);
        User user;
        user = getUser(comment.UserId);

        if(user == null)
        {
            Log.e("CommentAdapter", "User not found");
            return;
        }

        String likes = comment.Likes + " \"like\"";

        holder.ProfileImage.setImageResource(user.ProfileImage);
        holder.LikesTextView.setText(likes);
        holder.TextTextView.setText(comment.Text);
        holder.UsernameTextView.setText(user.Username);
        holder.LikeButton.setTag(R.drawable.ic_favorite_border_white_24dp);
    }

    @Nullable
    private User getUser(int userId)
    {
        User user = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            user = users.stream().filter(u -> u.UserId == userId).findAny().orElse(null);
        }
        else
        {
            for (User u : users)
            {
                if(u.UserId == userId)
                    user = u;
            }
        }
        return user;
    }

    @Override
    public int getItemCount()
    {
        return comments.size();
    }
}
