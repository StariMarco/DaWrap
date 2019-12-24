package Models;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

import com.example.dawrap.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// This class is used to translate the fragment_container sheet downward on
// the Y-axis when the "newPost" icon in the bottom NavBar is pressed.
public class NavigationIconClickListener implements View.OnClickListener {

    private View _sheet;
    private Interpolator _interpolator;
    private Drawable _openIcon;
    private Drawable _closeIcon;
    private BottomNavigationView _bottomNavigationView;
    private boolean[] _backdropShown;
    private int _backdropContentHeight;

    public NavigationIconClickListener(View sheet, @Nullable Interpolator interpolator, @Nullable Drawable openIcon,
                                       @Nullable Drawable closeIcon, @Nullable Integer mHeight,
                                       BottomNavigationView bottomNavigationView, boolean[] backdropActive)
    {
        _sheet = sheet;
        _interpolator = interpolator;
        _openIcon = openIcon;
        _closeIcon = closeIcon;
        _bottomNavigationView = bottomNavigationView;
        _backdropShown = backdropActive;

        _backdropContentHeight = mHeight;
    }

    @Override
    public void onClick(View view)
    {
        _backdropShown[0] = !_backdropShown[0];

        AnimatorSet animatorSet = new AnimatorSet();

        updateIcon();

        ObjectAnimator animator = ObjectAnimator.ofFloat(_sheet, View.TRANSLATION_Y, _backdropShown[0] ? -_backdropContentHeight : 0);
        animator.setDuration(300);
        animator.setInterpolator(_interpolator);
        animatorSet.play(animator);
        animator.start();
    }

    @SuppressLint("RestrictedApi")
    private void updateIcon()
    {
        if (_openIcon != null && _closeIcon != null && _bottomNavigationView != null)
        {
            // Get the new post item
            BottomNavigationItemView menuItem = _bottomNavigationView.findViewById(R.id.nav_new_post);

            // Update the icon
            menuItem.setIcon((_backdropShown[0]) ? _closeIcon : _openIcon);
        }
    }
}
