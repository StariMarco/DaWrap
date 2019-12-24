package com.example.dawrap;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Adapters.CommentAdapter;
import Models.Comment;
import Singletons.DataHelper;
import Models.PostModel;

public class PostCommentsActivity extends AppCompatActivity implements View.OnTouchListener
{
    private PostModel _post;
    private View _postCard;
    private View _draggableView;
    private View _commentList;
    private float _dyPostCard, _dyComments, _bottomPos = -1, _contentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        _post = (PostModel)getIntent().getSerializableExtra("POST_DATA");

        // Setup the post card content
        cardSetup();

        // Make the card able to move in the Y axes
        postCardAnimationSetup();

        // Setup the comment List view
        commentsListViewSetup();
    }

    private void cardSetup()
    {
        // Setup the post card content
        if(_post.PostImage != null)
            ((ImageView)findViewById(R.id.image_post)).setImageResource(_post.PostImage);

        ((TextView)findViewById(R.id.label_title)).setText(_post.Title);
        ((ImageView)findViewById(R.id.image_profile)).setImageResource(_post.ProfileImage);

        if(!(_post.Description == null || _post.Description.isEmpty()))
            ((TextView)findViewById(R.id.label_description)).setText(_post.Description);

        ((TextView)findViewById(R.id.label_likes)).setText(String.valueOf(_post.Likes));
        ((TextView)findViewById(R.id.label_comments)).setText(String.valueOf(_post.CommentCount));
    }

    private void postCardAnimationSetup()
    {
        View imageLayout = findViewById(R.id.image_layout);
        View descriptionLayout = findViewById(R.id.label_description);
        _postCard = findViewById(R.id.post_layout);
        _commentList = findViewById(R.id.comment_list_layout);

        // Set on click listener on the post card to animate
        _draggableView = findViewById(R.id.draggable_view_touch_sensor);
        _draggableView.setOnTouchListener(this);

        // Get post content height
        imageLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (_post.Description == null)
            {
                _contentHeight = imageLayout.getHeight();
                descriptionLayout.setVisibility(View.GONE);
            }
            else
            {
                _contentHeight = descriptionLayout.getHeight();
                imageLayout.setVisibility(View.GONE);
            }
        });
    }

    private void commentsListViewSetup()
    {
        // Get and filter the comments related to this post
        ArrayList<Comment> postComments = new ArrayList<>();
        ArrayList<Comment> comments = DataHelper.getComments();
        for(Comment c : comments)
        {
            if(c.PostId == _post.PostId)
                postComments.add(c);
        }

        // Create the recycler view content
        CommentAdapter adapter = new CommentAdapter(postComments, DataHelper.getUsers());
        RecyclerView commentsListView = findViewById(R.id.comments_listView);
        commentsListView.setAdapter(adapter);
        commentsListView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener()
        {
            @Override // event listener for like button in comment layout
            public void onLikeClick(TextView likeTxt, ImageButton btn, int position)
            {
                if(btn == null || btn.getTag() == null)
                {
                    Log.e("PostCommentActivity", "The comment like button or his tag is null");
                    return;
                }
                if(btn.getTag().equals("false"))
                {
                    // like
                    btn.setImageResource(R.drawable.ic_favorite_white_24dp);
                    String likes = ++postComments.get(position).Likes + " \"like\"";
                    likeTxt.setText(likes);
                    btn.setTag("true");
                }else
                {
                    // remove like
                    btn.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    String likes = --postComments.get(position).Likes + " \"like\"";
                    likeTxt.setText(likes);
                    btn.setTag("false");
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if(view.getId() == _draggableView.getId())
        {
            // Get the max Y position
            if(_bottomPos == -1) _bottomPos = event.getRawY() + 1;

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    _dyPostCard = _postCard.getY() - event.getRawY();
                    _dyComments = _commentList.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Clamp movement to make the post content always visible
                    if(event.getRawY() > _bottomPos || event.getRawY() < _contentHeight || event.getRawY() < 500) return false;

                    _postCard.animate()
                            .y(event.getRawY() + _dyPostCard)
                            .setDuration(0)
                            .start();

                    _commentList.animate()
                            .y(event.getRawY() + _dyComments)
                            .setDuration(0)
                            .start();

                    break;
            }
            return true;
        }
        return false;
    }

    public void onBackBtnClicked(View view)
    {
        super.onBackPressed();
    }
}
