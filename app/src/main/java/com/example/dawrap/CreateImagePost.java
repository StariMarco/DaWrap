package com.example.dawrap;

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
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import Models.Post;
import Singletons.DataHelper;
import Singletons.SystemHelper;

public class CreateImagePost extends AppCompatActivity
{

    private final int TITLE_MAX_CHARS = 200;
    private final int PICK_IMAGE = 1;
    private Bitmap _imageBitmap;
    private ImageView _imageView;
    private TextInputEditText _titleTxt;
    private MaterialButton _postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_image_post);

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
        Intent chooserIntent = Intent.createChooser(intent, "Select Image");
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
        Post newPost = new Post("16", DataHelper.getCurrentUser().UserId, _titleTxt.getText().toString(), null, _imageBitmap, new ArrayList<>());
        DataHelper.getPosts().add(0, newPost);
        super.onBackPressed();
    }

    public void onChangeImageClick(View view)
    {
        pickImage();
    }
}
