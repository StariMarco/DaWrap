package com.example.dawrap;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
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

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        fullScreencall();

        setupBottomNavigation(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
//        fullScreencall();
        super.onResume();
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

    private void setupBackdrop(BottomNavigationView bottomNavigationView)
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
                        (BottomNavigationItemView) createPostItem,
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

            // Manage Bottom Navigation items click listeners
            switch (menuItem.getItemId())
            {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    if(backdropActive[0])
                    {
                        createPostView.performClick();
                        backdropActive[0] = false;
                    }
                    break;
                case R.id.nav_new_post:
                    break;
                case R.id.nav_user_profile:
                    selectedFragment = new UserProfileFragment();
                    if(backdropActive[0])
                    {
                        createPostView.performClick();
                        backdropActive[0] = false;
                    }

                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            // Return true = select item
            return true;
        }
    };

    public void fullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
