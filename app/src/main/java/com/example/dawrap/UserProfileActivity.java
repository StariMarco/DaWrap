package com.example.dawrap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;

import java.util.ArrayList;

import Adapters.ProfilePostsAdapter;
import Models.Post;
import Models.User;
import Singletons.DataHelper;

public class UserProfileActivity extends AppCompatActivity
{
    private User _user;
    private ArrayList<Post> _posts;
    private RecyclerView _postListView;

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
        int id = getIntent().getIntExtra("USER_ID", 0);
        _user = DataHelper.getUserById(id);
        _posts = new ArrayList<>();
        for(Post p : DataHelper.getPosts())
        {
            if(p.UserId == id)
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
                i.putExtra("POST_ID", _posts.get(position).PostId);
                startActivity(i);
            }
        });
    }

    private void userDataSetup()
    {
        ((CircleImageView) findViewById(R.id.usr_profile_image)).setImageResource(_user.ProfileImage);
        ((TextView) findViewById(R.id.usr_username_txt)).setText(_user.Username);
        String txtPostCount = _posts.size() + " post";
        ((TextView) findViewById(R.id.usr_post_count_txt)).setText(txtPostCount);
        ((TextView) findViewById(R.id.usr_description_txt)).setText(_user.Description);
    }

    public void onBackBtnClicked(View view)
    {
        super.onBackPressed();
    }
}
