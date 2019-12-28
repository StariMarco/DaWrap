package com.example.dawrap;

import android.content.Context;
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

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;

import java.util.ArrayList;

import Adapters.CommentAdapter;
import Models.Comment;
import Models.User;
import Singletons.DataHelper;
import Models.Post;

public class PostCommentsActivity extends AppCompatActivity implements View.OnTouchListener
{
    private Post _post;
    private View _postCard;
    private View _draggableView;
    private View _commentList;
    private float _dyPostCard, _dyComments, _bottomPos = -1, _contentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        int id = getIntent().getIntExtra("POST_ID", 0);
        _post = DataHelper.getPostById(id);

        // Setup the post card content
        cardSetup();

        // Make the card able to move in the Y axes
        postCardAnimationSetup();

        // Setup the comment List view
        commentsListViewSetup();
    }

    private void cardSetup()
    {
        User user = DataHelper.getUserById(_post.UserId);
        // Setup the post card content
        if(_post.Image != null)
            ((ImageView)findViewById(R.id.image_post)).setImageResource(_post.Image);

        // Title
        ((TextView)findViewById(R.id.label_title)).setText(_post.Title);

        // Profile image
        ((CircleImageView)findViewById(R.id.image_profile)).setImageResource(user.ProfileImage);
        ((CircleImageView)findViewById(R.id.image_profile)).setOnItemSelectedClickListener(new ItemSelectedListener()
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

        // Description
        if(!(_post.Description == null || _post.Description.isEmpty()))
            ((TextView)findViewById(R.id.label_description)).setText(_post.Description);

        // Likes
        if(_post.hasUserLikedThisPost(DataHelper.getCurrentUser().UserId))
            ((ImageButton)findViewById(R.id.like_button)).setImageResource(R.drawable.ic_favorite_black_24dp);

        ((TextView)findViewById(R.id.label_likes)).setText(String.valueOf(_post.getLikesCount()));
        // Comments
        ((TextView)findViewById(R.id.label_comments)).setText(String.valueOf(_post.CommentCount));

        // SavedPosts
        if(DataHelper.getCurrentUser().hasSavedThisPost(_post.PostId))
            ((ImageButton)findViewById(R.id.save_post_button)).setImageResource(R.drawable.ic_bookmark_black_24dp);
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
        ArrayList<Comment> postComments = _post.getComments();
//        ArrayList<Comment> comments = DataHelper.getComments();
//        for(Comment c : comments)
//        {
//            if(c.PostId == _post.PostId)
//                postComments.add(c);
//        }

        // Create the recycler view content
        CommentAdapter adapter = new CommentAdapter(postComments);
        RecyclerView commentsListView = findViewById(R.id.comments_listView);
        commentsListView.setAdapter(adapter);
        commentsListView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener()
        {
            @Override // event listener for like button in comment layout
            public void onLikeClick(TextView likeTxt, ImageButton btn, int position)
            {
                Comment comment = postComments.get(position);
                User currentUser = DataHelper.getCurrentUser();
                if(btn == null)
                {
                    Log.e("PostCommentActivity", "The comment like button or his tag is null");
                    return;
                }
                if(comment.hasUserLikedThisComment(currentUser.UserId))
                {
                    // remove like
                    btn.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    comment.removeLike(currentUser.UserId);
                }else
                {
                    // like
                    btn.setImageResource(R.drawable.ic_favorite_white_24dp);
                    comment.addLike(currentUser.UserId);
                }
                String likes = postComments.get(position).getLikesCount() + " \"like\"";
                likeTxt.setText(likes);
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

    public void onLikeBtnClick(View view)
    {
        ImageButton btn = (ImageButton) view;
        TextView likeTxt = findViewById(R.id.label_likes);
        User currentUser = DataHelper.getCurrentUser();

        if(_post.hasUserLikedThisPost(currentUser.UserId))
        {
            // remove like
            btn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            _post.removeLike(currentUser.UserId);
        }
        else
        {
            // like
            btn.setImageResource(R.drawable.ic_favorite_black_24dp);
            _post.addLike(currentUser.UserId);
        }
        likeTxt.setText(String.valueOf(_post.getLikesCount()));
    }

    public void onSavePostClick(View view)
    {
        User currentUser = DataHelper.getCurrentUser();
        ImageButton btn = (ImageButton) view;

        if(currentUser.hasSavedThisPost(_post.PostId))
        {
            // Remove from saved
            btn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
            currentUser.removePost(_post);
        }
        else
        {
            // Save
            btn.setImageResource(R.drawable.ic_bookmark_black_24dp);
            currentUser.savePost(_post);
        }
    }
}
