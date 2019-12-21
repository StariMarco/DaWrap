package Models;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

/**
 * {@link android.view.View.OnClickListener} used to translate the product grid sheet downward on
 * the Y-axis when the navigation icon in the toolbar is pressed.
 */
public class NavigationIconClickListener implements View.OnClickListener {

    private final AnimatorSet animatorSet = new AnimatorSet();
    private Context context;
    private View sheet;
    private Interpolator interpolator;
    private int height;
    private boolean backdropShown = false;
    private Drawable openIcon;
    private Drawable closeIcon;
    private BottomNavigationItemView menuItem;
    private boolean[] backdropActive;


    NavigationIconClickListener(Context context, View sheet) {
        this(context, sheet, null);
    }

    public NavigationIconClickListener(Context context, View sheet, @Nullable Interpolator interpolator) {
        this(context, sheet, interpolator, null, null, null, null, null);
    }

    public NavigationIconClickListener(
            Context context, View sheet, @Nullable Interpolator interpolator,
            @Nullable Drawable openIcon, @Nullable Drawable closeIcon, @Nullable Integer mHeight, BottomNavigationItemView menuItem, boolean[] backdropActive) {
        this.context = context;
        this.sheet = sheet;
        this.interpolator = interpolator;
        this.openIcon = openIcon;
        this.closeIcon = closeIcon;
        this.menuItem = menuItem;
        this.backdropActive = backdropActive;

//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        height = display.getHeight();

        height = mHeight;
    }

    @Override
    public void onClick(View view) {
        backdropShown = !backdropShown;
        backdropActive[0] = !backdropActive[0];

        // Cancel the existing animations
        animatorSet.removeAllListeners();
        animatorSet.end();
        animatorSet.cancel();

        updateIcon();

        final int translateY = height;
//                context.getResources().getDimensionPixelSize(R.dimen.shr_product_grid_reveal_height);

        ObjectAnimator animator = ObjectAnimator.ofFloat(sheet, "translationY", backdropShown ? translateY : 0);
        animator.setDuration(500);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animatorSet.play(animator);
        animator.start();
    }

    @SuppressLint("RestrictedApi")
    private void updateIcon() {
        if (openIcon != null && closeIcon != null) {
            if (menuItem == null) {
                throw new IllegalArgumentException("menuItem is null");
            }
            if (backdropShown) {
                (menuItem).setIcon(closeIcon);
            } else {
                (menuItem).setIcon(openIcon);
            }
        }
    }
}
