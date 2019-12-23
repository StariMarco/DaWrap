package com.example.dawrap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapters.CommentAdapter;
import Models.Comment;
import Models.DataHelper;
import Models.PostModel;

public class PostCommentsActivity extends AppCompatActivity implements View.OnTouchListener
{
    private PostModel post;
    private View postCard;
    private View draggableView;
    private View imageLayout;
    private View descriptionLayout;
    private Display display;
    private float dy, dyComments, bottomPos = -1, contentHeight;
    private Point displaySize;
    private View commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        post = (PostModel)getIntent().getSerializableExtra("POST_DATA");

        // Setup the post card content
        pageSetup();

        // Make the card able to move in the Y axes
        postCardAnimationSetup();

        // Comment List view setup
        commentsListViewSetup();
    }

    private void postCardAnimationSetup()
    {
        // Set on click listener on the post card to animate
        postCard = findViewById(R.id.post_layout);
        draggableView = findViewById(R.id.draggable_view);
        imageLayout = findViewById(R.id.image_layout);
        descriptionLayout = findViewById(R.id.label_description);
        commentList = findViewById(R.id.comment_list_layout);
        draggableView.setOnTouchListener(this);

        imageLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            // Get post content height
            if (post.Description == null)
            {
                contentHeight = imageLayout.getHeight();
                descriptionLayout.setVisibility(View.GONE);
            }
            else
            {
                contentHeight = descriptionLayout.getHeight();
                imageLayout.setVisibility(View.GONE);
            }
        });

        // Get display size (x, y)
        displaySize = new Point();
        display = getWindowManager().getDefaultDisplay();
        display.getSize(displaySize);
    }

    private void commentsListViewSetup()
    {
        RecyclerView commentsListView = findViewById(R.id.comments_listView);

        ArrayList<Comment> postComments = new ArrayList<>();
        ArrayList<Comment> comments = DataHelper.getComments();
        for(Comment c : comments)
        {
            if(c.PostId == post.PostId)
                postComments.add(c);
        }

        CommentAdapter adapter = new CommentAdapter(postComments, DataHelper.getUsers());
        commentsListView.setAdapter(adapter);
        commentsListView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener()
        {
            @Override // event listener for like button in comment layout
            public void onLikeClick(TextView likeTxt, ImageButton btn, int position)
            {
                if(btn.getTag().equals(R.drawable.ic_favorite_border_white_24dp))
                {
                    btn.setImageResource(R.drawable.ic_favorite_white_24dp);
                    String likes = ++postComments.get(position).Likes + " \"like\"";
                    likeTxt.setText(likes);
                    btn.setTag(R.drawable.ic_favorite_white_24dp);
                }else
                {
                    btn.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    String likes = --postComments.get(position).Likes + " \"like\"";
                    likeTxt.setText(likes);
                    btn.setTag(R.drawable.ic_favorite_border_white_24dp);
                }
            }
        });
    }

    private void pageSetup()
    {
        // Setup the post card content
        if(post.PostImage != null)
            ((ImageView)findViewById(R.id.image_post)).setImageResource(post.PostImage);

        ((TextView)findViewById(R.id.label_title)).setText(post.Title);
        ((ImageView)findViewById(R.id.image_profile)).setImageResource(post.ProfileImage);

        if(!(post.Description == null || post.Description.isEmpty()))
            ((TextView)findViewById(R.id.label_description)).setText(post.Description);

        ((TextView)findViewById(R.id.label_likes)).setText(String.valueOf(post.Likes));
        ((TextView)findViewById(R.id.label_comments)).setText(String.valueOf(post.CommentCount));


    }

    public void onBackBtnClicked(View view)
    {
        super.onBackPressed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(v.getId() == draggableView.getId())
        {
            if(bottomPos == -1) bottomPos = event.getRawY() + 1;
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    dy = postCard.getY() - event.getRawY();
                    dyComments = commentList.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
//                    Log.d("Pos", "Y: " + event.getRawY());
                    // Clamp movement to make the post content always visible
                    if(event.getRawY() > bottomPos || event.getRawY() < contentHeight) return false;

                    postCard.animate()
                            .y(event.getRawY() + dy)
                            .setDuration(0)
                            .start();

                    float dp = dpFromPx(getApplicationContext(), dy);

                    commentList.animate()
                            .y(event.getRawY() + dyComments)
                            .setDuration(0)
                            .start();

                    break;
            }
            return true;
        }
        return false;
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}
