package com.example.dawrap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import Models.Comment;
import Models.Post;
import Singletons.DataHelper;
import Singletons.SystemHelper;

public class CreateTextPost extends AppCompatActivity
{
    private static final String TAG = "CreateTextPost";

    private TextInputEditText _titleTxt, _descriptionTxt;
    private MaterialButton _postBtn;
    private final int TITLE_MAX_CHARS = 20, DESCRIPTION_MAX_CHARS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_text_post);

        _postBtn = findViewById(R.id.text_post_button);
        _titleTxt = findViewById(R.id.text_input_title);
        _descriptionTxt = findViewById(R.id.text_input_description);

        _titleTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                checkForCompletition();
            }
        });
        _descriptionTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                checkForCompletition();
            }
        });
    }

    private void checkForCompletition()
    {
        if(SystemHelper.isInputReady(_titleTxt.getText(), TITLE_MAX_CHARS) && SystemHelper.isInputReady(_descriptionTxt.getText(), DESCRIPTION_MAX_CHARS))
            // Ready
            _postBtn.setEnabled(true);
        else
            // Not ready
            _postBtn.setEnabled(false);
    }

    public void onBackBtnCLick(View view)
    {
        super.onBackPressed();
    }

    public void onTextPostClick(View view)
    {
        // Create the post
        String uniqueId = UUID.randomUUID().toString();
        Post newPost = new Post();
        newPost.postId = uniqueId;
        newPost.userId = DataHelper.getCurrentUser().userId;
        newPost.title = _titleTxt.getText().toString();
        newPost.description = _descriptionTxt.getText().toString();
        newPost.image = null;
        newPost.likes = new ArrayList<String>();
        newPost.comments = new ArrayList<Comment>();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        newPost.creationDate = df.format(new Date());

        DataHelper._posts.add(0, newPost);

        // Firebase Firestore
//        Map<String, Object> post = new HashMap<>();
//        post.put("postId", uniqueId);
//        post.put("userId", DataHelper.getCurrentUser().userId);
//        post.put("title", _titleTxt.getText().toString());
//        post.put("description", _descriptionTxt.getText().toString());
//        post.put("image", null);
//        post.put("likes", new ArrayList<String>());
//        post.put("comments", new ArrayList<Comment>());

        DataHelper.db.collection("posts").document(uniqueId).set(newPost)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post document added: " + uniqueId);
                    super.onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding document", e);
                    super.onBackPressed();
                });
    }
}
