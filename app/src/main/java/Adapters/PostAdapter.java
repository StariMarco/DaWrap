package Adapters;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.example.dawrap.R;

import java.util.List;

import Models.Post;
import Models.User;
import Singletons.DataHelper;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>
{
    public interface OnItemClickListener
    {
        void onLikeClick(ImageButton btn, TextView likeTxt, int position);
        void onCommentClick(int position);
        void onProfileImageClick(int position);
    }

    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener) { mListener = listener; }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView UsenameTextView;
        public TextView TitleTextView;
        public TextView DescriptionTextView;
        public CircleImageView ProfileImageView;
        public ImageView PostImageView;
        public TextView LikesTextView;
        public ImageButton LikeBtn;
        public TextView CommentsTextView;
        public ImageButton CommentBtn;

        public ViewHolder(View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            // Get views from post_layout.xml
            UsenameTextView = itemView.findViewById(R.id.label_username);
            TitleTextView = itemView.findViewById(R.id.label_title);
            DescriptionTextView = itemView.findViewById(R.id.label_description);
            ProfileImageView = itemView.findViewById(R.id.image_profile);
            PostImageView = itemView.findViewById(R.id.image_post);
            LikesTextView = itemView.findViewById(R.id.lbl_likes);
            LikeBtn = itemView.findViewById(R.id.btn_like);
            CommentsTextView = itemView.findViewById(R.id.lbl_comment_count);
            CommentBtn = itemView.findViewById(R.id.btn_comment);

            // Click listener for like button
            LikeBtn.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                        listener.onLikeClick((ImageButton) v, LikesTextView, position);
                }
            });

            // Click listener for comment button
            CommentBtn.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                        listener.onCommentClick(position);
                }
            });

            ProfileImageView.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                        listener.onProfileImageClick(position);
                }
            });
        }
    }

    private List<Post> posts;

    public PostAdapter(List<Post> posts)
    {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View postView = inflater.inflate(R.layout.post_layout, parent, false);

        // Add click event listener
        postView.findViewById(R.id.label_description).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextView txt = ((TextView) v);
                txt.setMaxLines((txt.getMaxLines() > 10) ? 10 : 100);
            }
        });

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(postView, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder viewHolder, int position)
    {
        // Get the post and user to bind at this row
        Post post = DataHelper.getPosts().get(position);
        User user = DataHelper.getUserById(post.UserId);
        User currentUser = DataHelper.getCurrentUser();

        if(user == null)
        {
            Log.e("PostAdapter", "Error! User not found");
            return;
        }

        // Set all properties
        viewHolder.UsenameTextView.setText(user.Username);
        viewHolder.TitleTextView.setText(post.Title);
        viewHolder.ProfileImageView.setImageResource(user.ProfileImage);

        if(post.Image == null)
            viewHolder.PostImageView.setVisibility(View.GONE);
        else
        {
            viewHolder.PostImageView.setVisibility(View.VISIBLE);
            viewHolder.PostImageView.setImageResource(post.Image);
        }

        if(post.Description == null || post.Description.isEmpty())
            viewHolder.DescriptionTextView.setVisibility(View.GONE);
        else
        {
            viewHolder.DescriptionTextView.setVisibility(View.VISIBLE);
            viewHolder.DescriptionTextView.setText(post.Description);
        }

        if(post.hasUserLikedThisPost(currentUser.UserId))
            viewHolder.LikeBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
        else
            viewHolder.LikeBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        viewHolder.LikesTextView.setText(String.valueOf(post.getLikesCount()));
        viewHolder.CommentsTextView.setText(String.valueOf(post.CommentCount));

        viewHolder.ProfileImageView.setOnItemSelectedClickListener(new ItemSelectedListener()
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
    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }
}
