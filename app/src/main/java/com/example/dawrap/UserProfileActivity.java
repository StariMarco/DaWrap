package com.example.dawrap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import Adapters.ProfilePostsAdapter;
import Models.Post;
import Models.User;
import Singletons.DataHelper;

public class UserProfileActivity extends AppCompatActivity
{
    private static final String TAG = "UserProfileActivity";

    private User _user;
    private ArrayList<Post> _posts;
    private RecyclerView _postListView;
    private TextView _followersText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        postListViewSetup();

        userDataSetup();
    }

    private void postListViewSetup()
    {
        // Get the user reference and his posts
        String id = getIntent().getStringExtra("USER_ID");
        _user = DataHelper.getUserById(id);
        _posts = new ArrayList<>();
        for(Post p : DataHelper.getPosts())
        {
            if(p.userId.equals(id))
                _posts.add(p);
        }

        // Setup the post list view
        _postListView = findViewById(R.id.usr_post_list);
        ProfilePostsAdapter adapter = new ProfilePostsAdapter(_posts);
        _postListView.setAdapter(adapter);
        _postListView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter.setOnItemClickListener(new ProfilePostsAdapter.OnItemClickListener()
        {
            @Override
            public void onImageClicked(int position)
            {
                Intent i = new Intent(UserProfileActivity.this, PostCommentsActivity.class);
                i.putExtra("POST_ID", _posts.get(position).postId);
                startActivity(i);
            }
        });
    }

    private void userDataSetup()
    {
        // Profile image
        CircleImageView profileImage = findViewById(R.id.usr_profile_image);
        DataHelper.downloadImageIntoView(profileImage, _user.profileImage, TAG, R.drawable.profile_img_test);
        profileImage.setOnItemSelectedClickListener(new ItemSelectedListener()
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
        // username text
        ((TextView) findViewById(R.id.usr_username_txt)).setText(_user.username);
        // followers text
        String txtFollowerCount = _user.followersCount() + " followers";
        _followersText = findViewById(R.id.usr_followers_txt);
        _followersText.setText(txtFollowerCount);
        // description text
        ((TextView) findViewById(R.id.usr_description_txt)).setText(_user.description);

        // Follow button
        MaterialButton followerBtn = findViewById(R.id.follow_btn);
        if(DataHelper.getCurrentUser().userId.equals(_user.userId))
        {
            followerBtn.setEnabled(false);
            followerBtn.setText("Your Profile");
        }
        else if(DataHelper.getCurrentUser().followsUserWithId(_user.userId))
        {
            followerBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            followerBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            followerBtn.setText("Unfollow");
        }
    }

    public void onBackBtnClicked(View view)
    {
        super.onBackPressed();
    }

    public void onFollowClick(View view)
    {
        MaterialButton btn = (MaterialButton) view;
        User currentUser = DataHelper.getCurrentUser();
        if(currentUser.followsUserWithId(_user.userId))
        {
            // Unfollow
            btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            btn.setText("Follow");
            currentUser.unfollow(_user);
        }
        else
        {
            // Follow
            btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            btn.setText("Unfollow");
            currentUser.follow(_user);
        }

        String txtFollowerCount = _user.followersCount() + " followers";
        _followersText.setText(txtFollowerCount);
    }
}
