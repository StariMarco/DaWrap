package com.example.dawrap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Models.Comment;
import Models.Post;
import Singletons.DataHelper;
import Singletons.SystemHelper;

public class CreateImagePost extends AppCompatActivity
{
    private static final String TAG = "CreateImagePost";

    private final int TITLE_MAX_CHARS = 200;
    private final int PICK_IMAGE = 1;
    private Bitmap _imageBitmap;
    private ImageView _imageView;
    private TextInputEditText _titleTxt;
    private MaterialButton _postBtn;
    private String _uniqueId;
    private View _loadingCard;
    private TextView _uploadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_image_post);

        _uploadingText = findViewById(R.id.image_uploading_text);
        _imageView = findViewById(R.id.image_view);
        _postBtn = findViewById(R.id.image_post_button);
        _titleTxt = findViewById(R.id.image_input_title);
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

        pickImage();
    }

    @SuppressLint("IntentReset")
    private void pickImage()
    {
        // Pick the image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        // Allow the user to choose which gallery app use to pick the image
        Intent chooserIntent = Intent.createChooser(intent, "Select image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE)
        {
            try
            {
                // Get the image
                if(data == null)
                {
                    super.onBackPressed();
                    return;
                }
                InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                _imageBitmap = BitmapFactory.decodeStream(imageStream);
                _imageView.setImageBitmap(_imageBitmap);
                findViewById(R.id.change_image_btn).setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void checkForCompletition()
    {
        if(SystemHelper.isInputReady(_titleTxt.getText(), TITLE_MAX_CHARS))
            _postBtn.setEnabled(true);
        else
            // Not ready
            _postBtn.setEnabled(false);
    }

    public void onBackBtnCLick(View view)
    {
        super.onBackPressed();
    }

    public void onImagePostClick(View view)
    {
        // Create the post
        _uniqueId = UUID.randomUUID().toString();
//        Post newPost = new Post(_uniqueId, DataHelper.getCurrentUser().userId, _titleTxt.getText().toString(), null, "", new ArrayList<>(), new ArrayList<>());
//        DataHelper.getPosts().add(0, newPost);

        // Firebase Storage
        uploadImageToFirestore();

        // Hide keyboard and show loading animation
        SystemHelper.hideSoftKeyboard(this);
        _loadingCard = findViewById(R.id.image_uploading_card);
        _loadingCard.setVisibility(View.VISIBLE);
        // Disable user interactions
        _postBtn.setEnabled(false);
        _titleTxt.setEnabled(false);
    }

    private void uploadImageToFirestore()
    {
        StorageReference storageReference = DataHelper.storage.getReference();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference postImagesRef = storageReference.child("postImages/" + _uniqueId);
        UploadTask uploadTask = postImagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "image uploaded successfully: " + taskSnapshot.getMetadata().getPath());
            // Firebase Firestore
            uploadPostToFirstore(_uniqueId, taskSnapshot.getMetadata().getPath());

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to upload image: ", e);
        }).addOnProgressListener(taskSnapshot -> {
            // Get upload progress
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            DecimalFormat df = new DecimalFormat("#0");
            String progressTxt = "Uploading " + df.format(progress) + "%";
            _uploadingText.setText(progressTxt);
            Log.d(TAG, "Progress: " + progressTxt);
        });
    }

    private void uploadPostToFirstore(String uniqueId, String path)
    {
        Map<String, Object> post = new HashMap<>();
        post.put("postId", uniqueId);
        post.put("userId", DataHelper.getCurrentUser().UserId);
        post.put("title", _titleTxt.getText().toString());
        post.put("description", null);
        post.put("image", path);
        post.put("likes", new ArrayList<String>());
        post.put("comments", new ArrayList<Comment>());

        DataHelper.db.collection("posts").document(uniqueId).set(post)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post document added: " + uniqueId);
                    _loadingCard.setVisibility(View.GONE);
                    super.onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error adding document", e);
                    _loadingCard.setVisibility(View.GONE);
                    super.onBackPressed();
                });
    }

    public void onChangeImageClick(View view)
    {
        pickImage();
    }
}
