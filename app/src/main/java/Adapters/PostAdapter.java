package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dawrap.R;

import java.util.List;

import Models.PostModel;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>
{
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView UsenameTextView;
        public TextView TitleTextView;
        public TextView DescriptionTextView;
        public ImageView ProfileImageView;
        public ImageView PostImageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            UsenameTextView = (TextView) itemView.findViewById(R.id.lblUsername);
            TitleTextView = (TextView) itemView.findViewById(R.id.lblTitle);
            DescriptionTextView = (TextView) itemView.findViewById(R.id.lblDescription);
            ProfileImageView = (ImageView) itemView.findViewById(R.id.profileImg);
            PostImageView = (ImageView) itemView.findViewById(R.id.postImage);
        }
    }

    private List<PostModel> posts;

    public PostAdapter(List<PostModel> posts)
    {
        this.posts = posts;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View postView = inflater.inflate(R.layout.post_layout, parent, false);

        // Add click event listener
            postView.findViewById(R.id.lblDescription).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    System.out.println("yee");
                    TextView txt = ((TextView) v);
                    if (txt.getMaxLines() > 10)
                        txt.setMaxLines(10);
                    else
                        txt.setMaxLines(100);
                }
            });

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder viewHolder, int position)
    {
        PostModel post = posts.get(position);

        TextView txtUsername = viewHolder.UsenameTextView;
        txtUsername.setText(post.Username);
        TextView txtTitle = viewHolder.TitleTextView;
        txtTitle.setText(post.Title);

        if(post.Description == null || post.Description.isEmpty())
        {
            viewHolder.DescriptionTextView.setVisibility(View.GONE);

        }
        else
        {
            viewHolder.DescriptionTextView.setVisibility(View.VISIBLE);
            TextView txtDescription = viewHolder.DescriptionTextView;
            txtDescription.setText(post.Description);
        }

        if(post.ProfileImage == null)
        {
            viewHolder.PostImageView.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.PostImageView.setVisibility(View.VISIBLE);
            ImageView profileImg = viewHolder.ProfileImageView;
            profileImg.setImageResource(post.ProfileImage);
        }

        if(post.PostImage != null)
        {
            ImageView postImg = viewHolder.PostImageView;
            postImg.setImageResource(post.PostImage);
        }
    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }
}
