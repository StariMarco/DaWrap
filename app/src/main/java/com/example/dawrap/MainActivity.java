package com.example.dawrap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Property;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import Models.NavigationIconClickListener;

public class MainActivity extends AppCompatActivity
{
    private LinearLayout mView;
    private int height = 0;
    private boolean[] backdropActive = {false};
    private View createPostView;

    AnimatorSet fragmentTransitionAnimatorSet = new AnimatorSet();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavigation(savedInstanceState);
    }

    private void setupBottomNavigation(Bundle savedInstanceState)
    {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Seth home fragment as default onCreate
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        setupBackdrop(bottomNav);
    }

    private void setupBackdrop(final BottomNavigationView bottomNavigationView)
    {
        final View createPostItem = bottomNavigationView.findViewById(R.id.nav_new_post);
        mView = findViewById(R.id.backdrop_content);
        createPostView = createPostItem;

        createPostItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                height = mView.getHeight();
                createPostItem.setOnClickListener(new NavigationIconClickListener(getApplicationContext(),
                        findViewById(R.id.fragment_container),
                        new AccelerateDecelerateInterpolator(),
                        getApplicationContext().getResources().getDrawable(R.drawable.ic_create_white_24dp),
                        getApplicationContext().getResources().getDrawable(R.drawable.ic_close_white_24dp),
                        -height,
                        (BottomNavigationView) bottomNavigationView,
                        backdropActive));

                createPostItem.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            Fragment selectedFragment = null;
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

            if(backdropActive[0])
                return false;

            // Manage Bottom Navigation items click listeners
            switch (menuItem.getItemId())
            {
                case R.id.nav_home:
                    if(bottomNav.getSelectedItemId() != R.id.nav_home)
                        selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_new_post:
                    break;
                case R.id.nav_user_profile:
                    if(bottomNav.getSelectedItemId() != R.id.nav_user_profile)
                        selectedFragment = new UserProfileFragment();
                    break;
            }

            // Page transition
            if(selectedFragment != null)
            {
                transitionToFragment(getApplicationContext(), selectedFragment, new AccelerateDecelerateInterpolator());
            }

            // Return true = select item
            return true;
        }
    };

    private void transitionToFragment(Context context, final Fragment newFragment, @Nullable Interpolator interpolator)
    {
        View sheet = findViewById(R.id.fragment_container);
        int displayHeight = getDisplayHeight(context);

        // dp to pixels
        float bounceOffset= getPixelsFromDp(20);

        // Cancel the existing animations
        fragmentTransitionAnimatorSet.removeAllListeners();
        fragmentTransitionAnimatorSet.end();
        fragmentTransitionAnimatorSet.cancel();

        // Create the animation
        ObjectAnimator startBounce = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, bounceOffset);
        startBounce.setDuration(200);
        startBounce.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator endBounce = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, 0);
        endBounce.setDuration(200);
        endBounce.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, -displayHeight);
        fadeOut.setDuration(500);
        if(interpolator != null)
            fadeOut.setInterpolator(interpolator);
        fadeOut.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
            }
        });

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, bounceOffset);
        fadeIn.setDuration(500);
        if(interpolator != null)
            fadeIn.setInterpolator(interpolator);

        fragmentTransitionAnimatorSet.play(fadeOut).after(startBounce);
        fragmentTransitionAnimatorSet.play(fadeOut).before(fadeIn);
        fragmentTransitionAnimatorSet.play(endBounce).after(fadeIn);
        fragmentTransitionAnimatorSet.start();
    }

    private float getPixelsFromDp(float dp)
    {
        Resources r = getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    private int getDisplayHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }

    private void resetBackdrop()
    {
        // Make the content of the page return to the start position
        if (backdropActive[0])
        {
            createPostView.performClick();
        }
    }
}
