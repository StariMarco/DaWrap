package com.example.dawrap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import CustomViews.GradientTextView;
import Models.PostModel;

public class PostCommentsActivity extends AppCompatActivity
{
    private PostModel post;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        post = (PostModel)getIntent().getSerializableExtra("POST_DATA");
        pageSetup();
    }

    private void pageSetup()
    {
        if(post.PostImage != null)
            ((ImageView)findViewById(R.id.image_post)).setImageResource(post.PostImage);
        else
            findViewById(R.id.image_post).setVisibility(View.GONE);

        ((TextView)findViewById(R.id.label_title)).setText(post.Title);
        ((ImageView)findViewById(R.id.image_profile)).setImageResource(post.ProfileImage);

        if(post.Description == null || post.Description.isEmpty())
            findViewById(R.id.label_description).setVisibility(View.GONE);
        else
            ((TextView)findViewById(R.id.label_description)).setText(post.Description);

        ((TextView)findViewById(R.id.label_likes)).setText(String.valueOf(post.Likes));
        ((TextView)findViewById(R.id.label_comments)).setText(String.valueOf(post.Comments.size()));


    }

    public void onBackBtnClicked(View view)
    {
        super.onBackPressed();
    }
}
