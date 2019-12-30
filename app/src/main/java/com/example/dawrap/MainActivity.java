package com.example.dawrap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import Models.Post;
import Singletons.DataHelper;
import Models.NavigationIconClickListener;
import Singletons.SystemHelper;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    private LinearLayout _backdropContentView;
    private boolean[] _backdropShown = {false};
    private BottomNavigationView _bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init singletons
        DataHelper.initInstance();
        SystemHelper.initInstance();

        setupBottomNavigation(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Reset the backdrop if the user comes from another page
        if (_backdropShown[0])
            _bottomNavigationView.findViewById(R.id.nav_new_post).performClick();
    }

    private void setupBottomNavigation(Bundle savedInstanceState)
    {
        _bottomNavigationView = findViewById(R.id.bottom_navigation);
        _bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        // Set home fragment as default onCreate
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        setupNewPostItem();
    }

    private void setupNewPostItem()
    {
        // Get the item from the layout
        View newPostItemView= _bottomNavigationView.findViewById(R.id.nav_new_post);

        // Get the backdrop content
        _backdropContentView = findViewById(R.id.backdrop_content);

        // Create an event listener for set the click event handler when the view has been created
        newPostItemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                newPostItemView.setOnClickListener(new NavigationIconClickListener(
                        findViewById(R.id.fragment_container),
                        new AccelerateDecelerateInterpolator(),
                        getApplicationContext().getResources().getDrawable(R.drawable.ic_create_white_24dp),
                        getApplicationContext().getResources().getDrawable(R.drawable.ic_close_white_24dp),
                        _backdropContentView.getHeight(),
                        _bottomNavigationView,
                        _backdropShown));

                newPostItemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            Fragment selectedFragment = null;

            // return if the item is already selected
            if(_bottomNavigationView.getSelectedItemId() == menuItem.getItemId())
                return false;

            // Manage Bottom Navigation items click listeners
            switch (menuItem.getItemId())
            {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_user_profile:
                    selectedFragment = new UserProfileFragment();
                    break;
                default:
                    break;
            }

            // Make the content of the page return to the start position
            if (_backdropShown[0])
                _bottomNavigationView.findViewById(R.id.nav_new_post).performClick();
            // Page transition animation
            transitionToFragment(getApplicationContext(), selectedFragment, new AccelerateDecelerateInterpolator());

            // Return true = select item
            return true;
        }
    };

    private void transitionToFragment(Context context, final Fragment newFragment, @Nullable Interpolator interpolator)
    {
        // Get the view to animate
        View sheet = findViewById(R.id.fragment_container);

        float bounceDistance = SystemHelper.getPixelsFromDp(getResources(), 20);

        // Create the animator set to concatenate all animations
        AnimatorSet fragmentTransitionAnimatorSet = new AnimatorSet();

        // Create the animation
        ObjectAnimator initialBounce = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, bounceDistance);
        initialBounce.setDuration(200);
        initialBounce.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator finalBounce = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, 0);
        finalBounce.setDuration(200);
        finalBounce.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator transitionUp = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, -SystemHelper.getDisplayHeight(context));
        transitionUp.setDuration(300);
        if(interpolator != null)
            transitionUp.setInterpolator(interpolator);
        transitionUp.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // When the animation ends change the fragment inside the view
                super.onAnimationEnd(animation);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
            }
        });

        ObjectAnimator transitionDown = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, bounceDistance);
        transitionDown.setDuration(500);
        if(interpolator != null)
            transitionDown.setInterpolator(interpolator);

        // Concatenate and start animations
        fragmentTransitionAnimatorSet.play(transitionUp).after(initialBounce);
        fragmentTransitionAnimatorSet.play(transitionUp).before(transitionDown);
        fragmentTransitionAnimatorSet.play(finalBounce).after(transitionDown);
        fragmentTransitionAnimatorSet.start();
    }

    public void onCreateTextPostClick(View view)
    {
        startActivity(new Intent(MainActivity.this, CreateTextPost.class));
    }

    public void onImagePostClick(View view)
    {
        startActivity(new Intent(MainActivity.this, CreateImagePost.class));
    }
}
