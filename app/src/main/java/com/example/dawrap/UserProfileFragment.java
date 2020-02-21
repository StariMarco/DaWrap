package com.example.dawrap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import Models.User;
import Singletons.DataHelper;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = "UserProfileFragment";

    private Button _myPostsBtn, _savedPostsBtn;
    private Integer _currentFragmentId = R.id.my_posts_btn;
    private View _fragmentContainer, _myPostsUnderline, _savedPostsUnderline;
    private float _containerHeight;
    private TextView _txtUsername;
    private TextView _txtDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        userDataSetup(view);

        fragmentButtonsSetup(view, savedInstanceState);

        dropdownMenuSetup(view);

        _txtUsername = view.findViewById(R.id.usr_lbl_username);
        _txtDescription = view.findViewById(R.id.usr_description_txt);
    }

    private void userDataSetup(@NonNull View view)
    {
        User currentUser = DataHelper.getCurrentUser();

        // username text
        ((TextView)view.findViewById(R.id.usr_lbl_username)).setText(currentUser.username);

        // description
        ((TextView)view.findViewById(R.id.usr_description_txt)).setText(currentUser.description);

        // followers text
        String followersTxt = currentUser.followersCount() + " followers";
        ((TextView)view.findViewById(R.id.usr_lbl_followers)).setText(followersTxt);

        // Profile image
        CircleImageView profileImage = view.findViewById(R.id.usr_profile_image);
        DataHelper.downloadImageIntoView(profileImage, currentUser.profileImage, TAG, R.drawable.profile_img_test);
        profileImage.setOnItemSelectedClickListener(new ItemSelectedListener()
        {
            @Override
            public void onSelected(View view)
            {
                return;
            }

            @Override
            public void onUnselected(View view)
            {
                return;
            }
        });
    }

    private void fragmentButtonsSetup(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        // Set home fragment as default onCreate
        if(savedInstanceState == null)
        {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.post_fragment_container, new MyPostFragment()).commit();
        }

        // Get the fragment container of the list views and his height
        _fragmentContainer = view.findViewById(R.id.post_fragment_container);
        _fragmentContainer.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            _containerHeight = _fragmentContainer.getHeight();
        });

        // Get buttons and set this entity as their onCLickListener
        _myPostsBtn = view.findViewById(R.id.my_posts_btn);
        _savedPostsBtn = view.findViewById(R.id.saved_posts_btn);
        _myPostsBtn.setOnClickListener(this);
        _savedPostsBtn.setOnClickListener(this);

        // Get buttons underlines
        _myPostsUnderline = view.findViewById(R.id.my_posts_underline);
        _savedPostsUnderline = view.findViewById(R.id.saved_posts_underline);
    }

    private void dropdownMenuSetup(@NonNull View view)
    {
        // Menu setup
        ImageButton menuBtn = view.findViewById(R.id.usr_menu);
        PopupMenu dropDownMenu = new PopupMenu(view.getContext(), menuBtn);
        Menu menu = dropDownMenu.getMenu();

        // Set the dropdown menu items
        dropDownMenu.getMenuInflater().inflate(R.menu.user_profile_menu, menu);

        // Set the dropdown menu click listeners
        dropDownMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.usr_edit_profile)
            {
                Log.d("UserProfileFragment", "Edit Profile");

                // Start activity -> Edit user
                User currentUser = DataHelper.getCurrentUser();
                Intent intent = new Intent(getActivity(), EditUserActivity.class);
                intent.putExtra("USER", currentUser);
                startActivityForResult(intent, 1);
                return true;
            }
            else if (item.getItemId() == R.id.usr_logout)
            {
                FirebaseAuth.getInstance().signOut();
                getActivity().onBackPressed();
                return true;
            }
            return false;
        });

        // CLick listener to show the dropdown menu
        menuBtn.setOnClickListener(v -> {
            dropDownMenu.show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            User user = (User) data.getSerializableExtra("RESULT");
            if(user != null)
            {
                DataHelper.getCurrentUser().updateUser(user, getContext());
                _txtUsername.setText(user.getUsername());
                _txtDescription.setText(user.getDescription());
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        // Event Handler for fragment buttons
        if (_currentFragmentId.equals(v.getId()))
            return;

        _currentFragmentId = v.getId();
        switch (v.getId())
        {
            case R.id.my_posts_btn: // Posts
                swapViewAnimation(new MyPostFragment(), _savedPostsUnderline, _myPostsUnderline);
                break;
            case R.id.saved_posts_btn: // Saved Posts
                swapViewAnimation(new SavedPostFragment(), _myPostsUnderline, _savedPostsUnderline);
                break;
            default:
                break;
        }
    }

    private void swapViewAnimation(Fragment nextFragment, View prevUnderline, View nextUnderline)
    {
        AnimatorSet animatorSet = new AnimatorSet();

        // RECYCLERVIEW ANIMATION
        ObjectAnimator transitionDown = ObjectAnimator.ofFloat(_fragmentContainer, View.TRANSLATION_Y, _containerHeight + 150);
        transitionDown.setDuration(300);
        transitionDown.setInterpolator(new AccelerateDecelerateInterpolator());

        // When the animation ends change the fragment inside the view
        transitionDown.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.post_fragment_container, nextFragment).commit();
            }
        });

        ObjectAnimator transitionUp = ObjectAnimator.ofFloat(_fragmentContainer, View.TRANSLATION_Y, 0);
        transitionUp.setDuration(300);
        transitionUp.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.play(transitionDown).before(transitionUp);

        // BUTTONS UNDERLINE ANIMATION
        AnimatorSet underlineAnimatorSet = new AnimatorSet();
        ObjectAnimator disappear = ObjectAnimator.ofFloat(prevUnderline, View.SCALE_X, 0);
        disappear.setDuration(200);
        disappear.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator appear = ObjectAnimator.ofFloat(nextUnderline, View.SCALE_X, 1);
        appear.setDuration(200);
        appear.setInterpolator(new AccelerateDecelerateInterpolator());

        // START ANIMATIONS
        underlineAnimatorSet.play(disappear).before(appear);
        animatorSet.start();
        underlineAnimatorSet.start();
    }
}
