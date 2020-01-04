package com.example.dawrap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import Models.Post;
import Models.User;
import Singletons.DataHelper;
import Singletons.SystemHelper;

public class UserAuthenticationActivity extends AppCompatActivity
{
    private static final String TAG = "UserAuthentication";

    private Integer _currentFragment;
    private View _loginUnderline, _registerUnderline;
    private final int PICK_IMAGE = 1;
    private Bitmap _profileImage = null;
    private ImageView _profileImageView;
    public TextView _loadingTxt;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);

        mAuth = FirebaseAuth.getInstance();
        // Init singletons
        DataHelper.initInstance();
        SystemHelper.initInstance();

        _loginUnderline = findViewById(R.id.login_underline);
        _registerUnderline = findViewById(R.id.register_underline);

        // Set login fragment as default onCreate
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new LoginFragment()).commit();
            _currentFragment = R.id.btn_fragment_login;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            DataHelper._currentUserEmail = currentUser.getEmail();
            loginUser(currentUser);
        }
    }

    private void loginUser(FirebaseUser currentUser)
    {
        DataHelper.db.collection("users").document(currentUser.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists())
                            Log.e(TAG, "cannot find the user ", task.getException());

                        User user = document.toObject(User.class);
                        findViewById(R.id.login_button).setEnabled(true);
                        signIn(user);
                    }
                    else
                    {
                        findViewById(R.id.login_button).setEnabled(true);
                        Log.e(TAG, "Error while getting the user reference ", task.getException());
                    }
                });
    }

    private void signIn(User currentUser)
    {
        Intent i = new Intent(UserAuthenticationActivity.this, MainActivity.class);
        i.putExtra("CURRENT_USER", currentUser);
        startActivity(i);
    }

    public void onChangeFragmentClick(View view)
    {
        if(_currentFragment.equals(view.getId()))
            return;

        _currentFragment = view.getId();
        if(view.getId() == R.id.btn_fragment_login)
            swapViewAnimation(new LoginFragment(), _registerUnderline, _loginUnderline);
        else
            swapViewAnimation(new RegisterFragment(), _loginUnderline, _registerUnderline);
    }

    private void swapViewAnimation(Fragment nextFragment, View prevUnderline, View nextUnderline)
    {
        // RECYCLERVIEW ANIMATION
        AnimatorSet animatorSet = new AnimatorSet();

        // BUTTONS UNDERLINE ANIMATION
        AnimatorSet underlineAnimatorSet = new AnimatorSet();
        ObjectAnimator disappear = ObjectAnimator.ofFloat(prevUnderline, View.SCALE_X, 0);
        disappear.setDuration(200);
        disappear.setInterpolator(new AccelerateDecelerateInterpolator());
        disappear.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                super.onAnimationStart(animation);
                getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, nextFragment).commit();

            }
        });

        ObjectAnimator appear = ObjectAnimator.ofFloat(nextUnderline, View.SCALE_X, 0.8f);
        appear.setDuration(200);
        appear.setInterpolator(new AccelerateDecelerateInterpolator());

        // START ANIMATIONS
        underlineAnimatorSet.play(disappear).before(appear);
        animatorSet.start();
        underlineAnimatorSet.start();
    }

    public void onRegisterClick(View view)
    {
        String username = ((TextView)findViewById(R.id.txt_username_register)).getText().toString();
        String email = ((TextView)findViewById(R.id.txt_email_register)).getText().toString();
        String password = ((TextView)findViewById(R.id.txt_password_register)).getText().toString();
        String description = ((TextView)findViewById(R.id.txt_description_register)).getText().toString();
        findViewById(R.id.register_btn).setEnabled(false);
        findViewById(R.id.register_next_btn).setEnabled(false);
        findViewById(R.id.register_back_btn).setEnabled(false);

        if(_profileImage != null)
        {
            // Check if the email already exists
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        boolean isNewEmail = task.getResult().getSignInMethods().isEmpty();
                        if(isNewEmail)
                        {
                            _loadingTxt = findViewById(R.id.image_uploading_text);
                            // image id
                            String imageId = UUID.randomUUID().toString();
                            // Get the image
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            _profileImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                            byte[] data = baos.toByteArray();

                            // Upload profile image
                            StorageReference storageReference = DataHelper.storage.getReference();
                            StorageReference postImagesRef = storageReference.child("profileImages/" + imageId);
                            UploadTask uploadTask = postImagesRef.putBytes(data);
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                Log.d(TAG, "image uploaded successfully: " + taskSnapshot.getMetadata().getPath());
                                createUser(username, email, password, description, taskSnapshot.getMetadata().getPath());
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to upload image: ", e);
                            }).addOnProgressListener(taskSnapshot -> {
                                // Get upload progress
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                DecimalFormat df = new DecimalFormat("#0");
                                String progressTxt = "Loading " + df.format(progress) + "%";
                                _loadingTxt.setText(progressTxt);
                            });
                        }
                        else
                        {
                            endRegisterAnimation();
                            Toast.makeText(UserAuthenticationActivity.this, "The email already exists!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        endRegisterAnimation();
                        Toast.makeText(UserAuthenticationActivity.this, "Error while checking the email!", Toast.LENGTH_SHORT).show();
                    });

        }
        else
        {
            createUser(username, email, password, description, "profileImages/default.png");
        }
        // Animation
        View registerContainer = findViewById(R.id.register_container);
        registerContainer.animate().alpha(0f).setDuration(100).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                findViewById(R.id.register_animation_layout).setVisibility(View.VISIBLE);
            }
        });
    }

    private void endRegisterAnimation()
    {
        findViewById(R.id.register_btn).setEnabled(true);
        findViewById(R.id.register_next_btn).setEnabled(true);
        findViewById(R.id.register_back_btn).setEnabled(true);
        // End animation
        View registerContainer = findViewById(R.id.register_container);
        registerContainer.animate().alpha(1f).setDuration(100).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                findViewById(R.id.register_animation_layout).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void createUser(String username, String email, String password, String description, String imagePath)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful())
                    {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        DataHelper._currentUserEmail = currentUser.getEmail();

                        // Create the new user
                        User user = new User();
                        user.userId = currentUser.getUid();
                        user.username = username;
                        user.description = description;
                        user.profileImage = imagePath;
                        user.follows = new ArrayList<String>();
                        user.followers = new ArrayList<String>();
                        user.savedPosts = new ArrayList<String>();

                        // Add the user to the database
                        DataHelper.db.collection("users").document(email).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "User document added: " + user.userId);
                                    findViewById(R.id.register_animation_layout).setVisibility(View.INVISIBLE);
                                    // Sign in user
                                    signIn(user);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error adding user", e);
                                    findViewById(R.id.register_animation_layout).setVisibility(View.INVISIBLE);
                                });
                    }
                    else
                    {
                        Log.e(TAG, "Error creating the user", task.getException());
                        Toast.makeText(UserAuthenticationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        endRegisterAnimation();
                    }
                });
    }

    public void onLoginClick(View view)
    {
        String email = ((TextView)findViewById(R.id.txt_email_login)).getText().toString();
        String password = ((TextView)findViewById(R.id.txt_password_login)).getText().toString();

        findViewById(R.id.login_button).setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful())
                    {
                        // Sign in the user
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if(currentUser == null)
                        {
                            Log.e(TAG, "Error getting the user", task.getException());
                            Toast.makeText(UserAuthenticationActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }

                        DataHelper._currentUserEmail = currentUser.getEmail();
                        loginUser(currentUser);
                    }
                    else
                    {
                        Log.e(TAG, "Error login the user", task.getException());
                        Toast.makeText(UserAuthenticationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.login_button).setEnabled(true);
                    }
                });
    }

    public void onPickProfileImageClick(View view)
    {
        _profileImageView = (ImageView) view;
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
                    return;
                }
                InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                _profileImage = SystemHelper.getCircularBitmap(BitmapFactory.decodeStream(imageStream));
                _profileImageView.setImageBitmap(_profileImage);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}
