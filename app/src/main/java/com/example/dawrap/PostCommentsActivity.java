package com.example.dawrap;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
import Models.Post;
import Models.User;
import Singletons.DataHelper;
import Singletons.SystemHelper;

public class PostCommentsActivity extends AppCompatActivity implements View.OnTouchListener
{
    private static final String TAG = "PostCommentsActivity";

    private Post _post;
    private View _postCard;
    private View _draggableView;
    private View _commentList;
    private RecyclerView _commentsListView;
    private EditText _commentTxt;
    private CommentAdapter _commentAdapter;
    private float _dyPostCard, _dyComments, _bottomPos = -1, _contentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        String id = getIntent().getStringExtra("POST_ID");
        _post = DataHelper.getPostById(id);

        // Setup the post card content
        cardSetup();

        // Make the card able to move in the Y axes
        postCardAnimationSetup();

        // Setup the comment List view
        commentsListViewSetup();

        // Setup the section for writing a comment
        _commentTxt = findViewById(R.id.comment_edit_text);
    }

    private void cardSetup()
    {
        User user = DataHelper.getUserById(_post.userId);
        // Setup the post card content
        if(_post.image != null)
        {
            // Set image from db here
            ImageView postImageView = findViewById(R.id.image_post);
            DataHelper.downloadImageIntoView(postImageView, _post.image, TAG, R.drawable.post_img_test_3);
        }

        // title
        ((TextView)findViewById(R.id.label_title)).setText(_post.title);

        // Profile image
        CircleImageView profileImgView = findViewById(R.id.image_profile);
        profileImgView.setImageResource(user.ProfileImage);
        profileImgView.setOnClickListener(v -> {
            // Open user profile
            openUserProfile(user.UserId);
        });
        profileImgView.setOnItemSelectedClickListener(new ItemSelectedListener()
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

        // Add comment profile image
        CircleImageView addCommentImgView = (CircleImageView) findViewById(R.id.add_comment_profile_image);
        addCommentImgView.setImageResource(DataHelper.getCurrentUser().ProfileImage);
        addCommentImgView.setOnItemSelectedClickListener(new ItemSelectedListener()
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

        // description
        if(!(_post.description == null || _post.description.isEmpty()))
            ((TextView)findViewById(R.id.label_description)).setText(_post.description);

        // likes
        if(_post.hasUserLikedThisPost(DataHelper.getCurrentUser().UserId))
            ((ImageButton)findViewById(R.id.like_button)).setImageResource(R.drawable.ic_favorite_black_24dp);

        ((TextView)findViewById(R.id.label_likes)).setText(String.valueOf(_post.getLikes().size()));
        // comments
        ((TextView)findViewById(R.id.label_comments)).setText(String.valueOf(_post.getComments().size()));

        // SavedPosts
        if(DataHelper.getCurrentUser().hasSavedThisPost(_post.postId))
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
            if (_post.description == null)
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

        // Create the recycler view content
        _commentAdapter = new CommentAdapter(postComments);
        _commentsListView = findViewById(R.id.comments_listView);
        _commentsListView.setAdapter(_commentAdapter);
        _commentsListView.setLayoutManager(new LinearLayoutManager(this));

        _commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener()
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

            @Override
            public void onProfileImageClick(int position)
            {
                openUserProfile(postComments.get(position).UserId);
            }

            @Override
            public void onDeleteClick(int position)
            {
                Comment comment = postComments.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(PostCommentsActivity.this);
                builder.setTitle("Delete comment");
                builder.setMessage("Do you want to delete this comment?");
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    _post.deleteComment(comment);
                    // Notify changes to the comment list view
                    _commentAdapter.notifyDataSetChanged();
                    _commentsListView.setAdapter(_commentAdapter);
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();

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
        likeTxt.setText(String.valueOf(_post.getLikes().size()));
    }

    public void onSavePostClick(View view)
    {
        User currentUser = DataHelper.getCurrentUser();
        ImageButton btn = (ImageButton) view;

        if(currentUser.hasSavedThisPost(_post.postId))
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

    private void openUserProfile(String userId)
    {
        Intent i = new Intent(PostCommentsActivity.this, UserProfileActivity.class);
        i.putExtra("USER_ID", userId);
        startActivity(i);
    }

    public void onSendCommentClick(View view)
    {
        String text = _commentTxt.getText().toString();
        if(text.isEmpty())
            return;
        // Create new comment
        Comment newComment = new Comment("13", DataHelper.getCurrentUser().UserId, text);
        // Add the comment to the post list
        _post.addComment(newComment);
        // Notify changes to the comment list view
        _commentAdapter.notifyDataSetChanged();
        _commentsListView.setAdapter(_commentAdapter);
        // Reset input text view
        _commentTxt.setText("");
        // Hide keyboard if opened
        SystemHelper.hideSoftKeyboard(this);
    }
}
