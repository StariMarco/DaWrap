package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dawrap.R;

import java.util.List;

import Models.Post;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder>
{
    public interface OnItemClickListener
    {
        void onImageClicked(int position);
    }

    private ProfilePostsAdapter.OnItemClickListener mListener;
    public void setOnItemClickListener(ProfilePostsAdapter.OnItemClickListener listener) { mListener = listener; }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageButton Image;
        public TextView DescriptionTxt;

        public ViewHolder(View itemView, final ProfilePostsAdapter.OnItemClickListener listener)
        {
            super(itemView);
            // Get views from post_layout.xml
            Image = itemView.findViewById(R.id.usr_post_image);
            DescriptionTxt = itemView.findViewById(R.id.usr_description_txt);

            // Click listener for comment button
            Image.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                        listener.onImageClicked(position);
                }
            });

            DescriptionTxt.setOnClickListener(v -> {
                if(listener != null)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                        listener.onImageClicked(position);
                }
            });
        }
    }

    private List<Post> posts;

    public ProfilePostsAdapter(List<Post> posts)
    {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ProfilePostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View postView = inflater.inflate(R.layout.user_post_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(postView, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProfilePostsAdapter.ViewHolder viewHolder, int position)
    {
        // Get the post and user to bind at this row
        Post post = posts.get(position);

        // Set all properties
        if(post.Image != null)
        {
            viewHolder.Image.setImageBitmap(post.Image);
            viewHolder.DescriptionTxt.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.Image.setVisibility(View.GONE);
            viewHolder.DescriptionTxt.setText(post.Description);
        }
    }
    @Override
    public int getItemCount()
    {
        return posts.size();
    }
}
