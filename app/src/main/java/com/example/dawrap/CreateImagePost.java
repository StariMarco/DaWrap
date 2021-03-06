package com.example.dawrap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import Models.Comment;
import Models.ImageModifier;
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
            // Get the image
            if(data == null)
            {
                super.onBackPressed();
                return;
            }

            // Compress the image
            _imageBitmap = new ImageModifier().compressImage(data.getData(), this);
            if(_imageBitmap == null)
            {
                Toast.makeText(CreateImagePost.this, "Error while compressing the image!", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }

            // Show the compressed image
            _imageView.setImageBitmap(_imageBitmap);
            findViewById(R.id.change_image_btn).setVisibility(View.VISIBLE);
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
        // Compress the bitmap image into byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();

        // Get the image storage reference
        StorageReference postImagesRef = DataHelper.storage.getReference().child("postImages/" + _uniqueId);

        // Upload the image
        UploadTask uploadTask = postImagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "image uploaded successfully: " + taskSnapshot.getMetadata().getPath());
            // Firebase Firestore
            uploadPostToFirestore(_uniqueId, taskSnapshot.getMetadata().getPath());

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to upload image: ", e);
        }).addOnProgressListener(taskSnapshot -> {
            // Get upload progress
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            DecimalFormat df = new DecimalFormat("#0");
            String progressTxt = "Uploading " + df.format(progress) + "%";
            _uploadingText.setText(progressTxt);
        });
    }

    private void uploadPostToFirestore(String uniqueId, String path)
    {
        Post newPost = new Post();
        newPost.postId = uniqueId;
        newPost.userId = DataHelper.getCurrentUser().userId;
        newPost.title = _titleTxt.getText().toString();
        newPost.description = null;
        newPost.image = path;
        newPost.likes = new ArrayList<String>();
        newPost.comments = new ArrayList<Comment>();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        newPost.creationDate = df.format(new Date());

        DataHelper._posts.add(0, newPost);

        DataHelper.db.collection("posts").document(uniqueId).set(newPost)
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
