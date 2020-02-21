package com.example.dawrap;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.UUID;

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
    private TextView _labelCommentCount;
    private CommentAdapter _commentAdapter;
    private float _dyPostCard, _dyComments, _bottomPos = -1, _contentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        String id = getIntent().getStringExtra("POST_ID");
        _post = DataHelper.getPostById(id);

        // Delete comment setup
        dropdownMenuSetup();

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

        // Delete button
        if(DataHelper.getCurrentUser().userId.equals(_post.userId))
            findViewById(R.id.comment_menu).setVisibility(View.VISIBLE);

        // title
        ((TextView)findViewById(R.id.label_title)).setText(_post.title);

        // Profile image
        CircleImageView profileImgView = findViewById(R.id.image_profile);
        DataHelper.downloadImageIntoView(profileImgView, user.profileImage, TAG, R.drawable.profile_img_test);
        profileImgView.setOnClickListener(v -> {
            // Open user profile
            openUserProfile(user.userId);
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
        CircleImageView addCommentImgView = findViewById(R.id.add_comment_profile_image);
        DataHelper.downloadImageIntoView(addCommentImgView, DataHelper.getCurrentUser().profileImage, TAG, R.drawable.post_img_test_3);
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
        if(_post.hasUserLikedThisPost(DataHelper.getCurrentUser().userId))
            ((ImageButton)findViewById(R.id.like_button)).setImageResource(R.drawable.ic_favorite_black_24dp);

        ((TextView)findViewById(R.id.label_likes)).setText(String.valueOf(_post.likesCount()));
        // comments
        _labelCommentCount = findViewById(R.id.label_comments);
        _labelCommentCount.setText(String.valueOf(_post.commentsCount()));

        // savedPosts
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
                if(comment.hasUserLikedThisComment(currentUser.userId))
                {
                    // Remove like
                    comment.removeLike(currentUser.userId);
                    // Update the comment like in firestore
                    DocumentReference postRef = DataHelper.db.collection("posts").document(_post.postId);
                    postRef.update("comments", _post.comments)
                            .addOnSuccessListener(aVoid -> {
                                btn.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error in removing the comment like", e);
                            });
                }else
                {
                    // Like
                    comment.addLike(currentUser.userId);
                    // Update the comment like in firestore
                    DocumentReference postRef = DataHelper.db.collection("posts").document(_post.postId);
                    postRef.update("comments", _post.comments)
                            .addOnSuccessListener(aVoid -> {
                                btn.setImageResource(R.drawable.ic_favorite_white_24dp);
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error in adding the comment like", e);
                            });

                }
                String likes = postComments.get(position).likesCount() + " \"like\"";
                likeTxt.setText(likes);
            }

            @Override
            public void onProfileImageClick(int position)
            {
                openUserProfile(postComments.get(position).userId);
            }

            @Override
            public void onDeleteClick(int position)
            {
                Comment comment = postComments.get(position);
                // TODO: Fix crash

                AlertDialog.Builder builder = new AlertDialog.Builder(PostCommentsActivity.this);
                builder.setTitle("Delete comment");
                builder.setMessage("Do you want to delete this comment?");
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    // Update the item in firestore
                    DocumentReference postRef = DataHelper.db.collection("posts").document(_post.postId);
                    postRef.update("comments", FieldValue.arrayRemove(comment))
                            .addOnSuccessListener(aVoid -> {
                                _post.deleteComment(comment);
                                _labelCommentCount.setText(String.valueOf(_post.commentsCount()));
                                // Notify changes to the comment list view
                                _commentAdapter.notifyDataSetChanged();
                                _commentsListView.setAdapter(_commentAdapter);
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error in deleting the comment", e);
                            });

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
            if(_bottomPos == -1) _bottomPos = event.getRawY() + 80;

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

        if(_post.hasUserLikedThisPost(currentUser.userId))
        {
            // remove like
            btn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            _post.removeLike(currentUser.userId);
            DataHelper.db.collection("posts").document(_post.postId).update("likes", FieldValue.arrayRemove(currentUser.userId))
                    .addOnCompleteListener(task -> {
                        if(!task.isSuccessful())
                        {
                            Log.e(TAG, "Unable to remove the like", task.getException());
                            btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                            _post.addLike(currentUser.userId);
                            likeTxt.setText(String.valueOf(_post.likesCount()));
                        }
                    });
        }
        else
        {
            // like
            btn.setImageResource(R.drawable.ic_favorite_black_24dp);
            _post.addLike(currentUser.userId);
            DataHelper.db.collection("posts").document(_post.postId).update("likes", FieldValue.arrayUnion(currentUser.userId))
                    .addOnCompleteListener(task -> {
                        if(!task.isSuccessful())
                        {
                            Log.e(TAG, "Unable to remove the like", task.getException());
                            btn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            _post.removeLike(currentUser.userId);
                            likeTxt.setText(String.valueOf(_post.likesCount()));
                        }
                    });
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
            DataHelper.db.collection("users").document(DataHelper._currentUserEmail).update("savedPosts", FieldValue.arrayRemove(_post.postId))
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            currentUser.removePost(_post.postId);
                            DataHelper._savedPosts.remove(_post);
                        }
                        else
                        {
                            Log.e(TAG, "Unable to unsave the post", task.getException());
                            btn.setImageResource(R.drawable.ic_bookmark_black_24dp);
                            Toast.makeText(PostCommentsActivity.this, "Unable to unsave the post", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            // Save
            btn.setImageResource(R.drawable.ic_bookmark_black_24dp);
            DataHelper.db.collection("users").document(DataHelper._currentUserEmail).update("savedPosts", FieldValue.arrayUnion(_post.postId))
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            currentUser.savePost(_post.postId);
                            DataHelper._savedPosts.add(_post);
                        }
                        else
                        {
                            Log.e(TAG, "Unable to save the post", task.getException());
                            btn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            Toast.makeText(PostCommentsActivity.this, "Unable to save the post", Toast.LENGTH_SHORT).show();
                        }
                    });
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
        String uniqueId = UUID.randomUUID().toString();
        Comment newComment = new Comment(uniqueId, DataHelper.getCurrentUser().userId, text);

        // Update the item in firestore
        DocumentReference postRef = DataHelper.db.collection("posts").document(_post.postId);
        postRef.update("comments", FieldValue.arrayUnion(newComment))
                .addOnSuccessListener(aVoid -> {
                    // Add the comment to the post list
                    _post.comments.add(newComment);
                    _labelCommentCount.setText(String.valueOf(_post.commentsCount()));
                    // Notify changes to the comment list view
                    _commentAdapter.notifyDataSetChanged();
                    _commentsListView.setAdapter(_commentAdapter);
                    // Reset input text view
                    _commentTxt.setText("");
                    // Hide keyboard if opened
                    SystemHelper.hideSoftKeyboard(this);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading comments: ", e);
                });


    }

    private void dropdownMenuSetup()
    {
        // Menu setup
        ImageButton menuBtn = findViewById(R.id.comment_menu);
        PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), menuBtn);
        Menu menu = dropDownMenu.getMenu();

        // Set the dropdown menu items
        dropDownMenu.getMenuInflater().inflate(R.menu.comment_page_menu, menu);

        // Set the dropdown menu click listeners
        dropDownMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btn_delete_comment)
            {
                onDeletePostClick();
                return true;
            }
            return false;
        });

        // CLick listener to show the dropdown menu
        menuBtn.setOnClickListener(v -> {
            dropDownMenu.show();
        });
    }

    public void onDeletePostClick()
    {
        // Delete post in firebase
        AlertDialog.Builder builder = new AlertDialog.Builder(PostCommentsActivity.this);
        builder.setTitle("Delete Post");
        builder.setMessage("Do you want to delete this post?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            DataHelper.storage.getReference().child(_post.image).delete()
                    .addOnSuccessListener(aVoid -> {
                        DataHelper.db.collection("posts").document(_post.postId).delete()
                                .addOnSuccessListener(a -> {
                                    DataHelper._posts.remove(_post);
                                    super.onBackPressed();
                                }).addOnFailureListener(e -> {
                            Log.e(TAG, "Error in deleting the post", e);
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(PostCommentsActivity.this, "Error while deleting the image!", Toast.LENGTH_SHORT).show();
                    });

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
