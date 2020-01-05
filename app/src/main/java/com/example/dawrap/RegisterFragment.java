package com.example.dawrap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;

import Singletons.SystemHelper;

public class RegisterFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = "RegisterFragment";

    private TextInputEditText _usernameTxt, _emailTxt, _passwordTxt, _descriptionTxt;
    private View _usernameEntry, _emailEntry, _passwordEntry, _descriptionEntry;
    private ImageView _profileImg;
    private MaterialButton _registerBtn;
    private View _backBtn, _nextBtn, _buttonLayout;
    private boolean hide = true;

    private float _registerPathLength = 0, _registerButtonWidth = 0, _buttonLayoutWidth = 0;
    private float _backBtnWidth = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getViewReferences(view);
        setOnClickListeners();
        setEntriesTextChangedListeners();

        // Get some height and width values
        _buttonLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            _buttonLayoutWidth = _buttonLayout.getWidth();
        });

        _registerBtn.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            _registerButtonWidth = _registerBtn.getWidth();
            _registerPathLength = _buttonLayoutWidth - _registerButtonWidth;
        });

        _backBtn.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if(!hide) return;
            hide = false;
            _backBtnWidth = _backBtn.getWidth();
            _backBtn.animate().translationX(-_backBtnWidth)
                    .setDuration(0).setListener(null);
        });

        // Set default profile image
        Bitmap defaultProfileImageBtimap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_img_test);
        _profileImg.setImageBitmap(SystemHelper.getCircularBitmap(defaultProfileImageBtimap));
    }

    private void getViewReferences(@NonNull View view)
    {
        _usernameEntry = view.findViewById(R.id.username_entry_register);
        _emailEntry = view.findViewById(R.id.email_entry_register);
        _passwordEntry = view.findViewById(R.id.password_entry_register);
        _descriptionEntry = view.findViewById(R.id.description_entry_register);
        _usernameTxt = view.findViewById(R.id.txt_username_register);
        _emailTxt = view.findViewById(R.id.txt_email_register);
        _passwordTxt = view.findViewById(R.id.txt_password_register);
        _descriptionTxt = view.findViewById(R.id.txt_description_register);
        _profileImg = view.findViewById(R.id.profile_image_register);
        _registerBtn = view.findViewById(R.id.register_btn);
        _backBtn = view.findViewById(R.id.register_back_btn);
        _nextBtn = view.findViewById(R.id.register_next_btn);
        _buttonLayout = view.findViewById(R.id.register_button_layout);
    }

    private void setOnClickListeners()
    {
        // Set on click listeners
        _backBtn.setOnClickListener(this);
        _nextBtn.setOnClickListener(this);
    }

    private void setEntriesTextChangedListeners()
    {
        _usernameTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                isUserReady();
            }
        });
        _emailTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                isUserReady();
            }
        });
        _passwordTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                isUserReady();
            }
        });
        _descriptionTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                isUserReady();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.register_next_btn:
                changeSectionButtonsAnimation(_nextBtn, _backBtn, _registerPathLength, _backBtnWidth, 0);
                nextSectionAnimation();
                break;
            case R.id.register_back_btn:
                changeSectionButtonsAnimation(_backBtn, _nextBtn, 0, -_backBtnWidth, 0);
                prevSectionAnimation();
                break;
            default:
                break;
        }
    }

    private void isUserReady()
    {
        _registerBtn.setEnabled(false);
        _registerBtn.setTextColor(getResources().getColor(R.color.gray));
        // The entries are empty
        if(_usernameTxt.getText().toString().isEmpty() || _emailTxt.getText().toString().isEmpty() || _descriptionTxt.getText().toString().isEmpty() || _passwordTxt.getText().toString().isEmpty())
            return;

        // The password isn't long enough
        if(_passwordTxt.getText().toString().length() < 6)
            return;

        // The user is ready
        _registerBtn.setEnabled(true);
        _registerBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void nextSectionAnimation()
    {
        // Current views disappearing
        _usernameEntry.animate().alpha(0f).setDuration(100).setStartDelay(0).setListener(null);
        _emailEntry.animate().alpha(0f).setDuration(100).setStartDelay(30).setListener(null);
        _passwordEntry.animate().alpha(0f).setDuration(100).setStartDelay(60).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                _usernameEntry.setVisibility(View.GONE);
                _emailEntry.setVisibility(View.GONE);
                _passwordEntry.setVisibility(View.GONE);

                _profileImg.animate().alpha(0f).setDuration(0).setListener(null);
                _profileImg.setVisibility(View.VISIBLE);
                _descriptionEntry.animate().alpha(0f).setDuration(0).setListener(null);
                _descriptionEntry.setVisibility(View.VISIBLE);

                // Next views appearing
                _profileImg.animate().alpha(1f).setDuration(100).setStartDelay(0).setListener(null);
                _descriptionEntry.animate().alpha(1f).setDuration(100).setStartDelay(30).setListener(null);
            }
        });
    }

    private void prevSectionAnimation()
    {
        // Current views disappearing
        _profileImg.animate().alpha(0f).setDuration(100).setStartDelay(0).setListener(null);
        _descriptionEntry.animate().alpha(0f).setDuration(100).setStartDelay(30).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                _profileImg.setVisibility(View.GONE);
                _descriptionEntry.setVisibility(View.GONE);

                _usernameEntry.animate().alpha(0f).setDuration(0).setListener(null);
                _usernameEntry.setVisibility(View.VISIBLE);
                _emailEntry.animate().alpha(0f).setDuration(0).setListener(null);
                _emailEntry.setVisibility(View.VISIBLE);
                _passwordEntry.animate().alpha(0f).setDuration(0).setListener(null);
                _passwordEntry.setVisibility(View.VISIBLE);

                // Next views appearing
                _usernameEntry.animate().alpha(1f).setDuration(100).setStartDelay(0).setListener(null);
                _emailEntry.animate().alpha(1f).setDuration(100).setStartDelay(30).setListener(null);
                _passwordEntry.animate().alpha(1f).setDuration(100).setStartDelay(60).setListener(null);
            }
        });
    }

    private void changeSectionButtonsAnimation(View callerButton, View otherButton, float regBtnEndPoint, float callerBtnEndPoint, float otherBtnEndPoint)
    {
        callerButton.setEnabled(false);
        otherButton.setEnabled(true);

        // Disappear anim
        ObjectAnimator callerBtnAnim = ObjectAnimator.ofFloat(callerButton, View.TRANSLATION_X, callerBtnEndPoint);
        callerBtnAnim.setDuration(300);
        callerBtnAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        // Appear anim
        ObjectAnimator otherBtnAnim = ObjectAnimator.ofFloat(otherButton, View.TRANSLATION_X, otherBtnEndPoint);
        otherBtnAnim.setDuration(300);
        otherBtnAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        // Move anim
        ObjectAnimator registerBtnAnim = ObjectAnimator.ofFloat(_registerBtn, View.TRANSLATION_X, regBtnEndPoint);
        registerBtnAnim.setDuration(300);
        registerBtnAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet buttonTransitionSet = new AnimatorSet();
        buttonTransitionSet.play(callerBtnAnim).with(registerBtnAnim).with(otherBtnAnim);
        buttonTransitionSet.start();
    }
}
