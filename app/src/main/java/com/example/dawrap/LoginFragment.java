package com.example.dawrap;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment
{
    private static final String TAG = "LoginFragment";

    private TextInputEditText _emailTxt, _passwordTxt;
    private MaterialButton _loginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        _emailTxt = view.findViewById(R.id.txt_email_login);
        _passwordTxt = view.findViewById(R.id.txt_password_login);
        _loginBtn = view.findViewById(R.id.login_button);
        _loginBtn.setEnabled(false);

        // Set text changed listener
        _emailTxt.addTextChangedListener(new TextWatcher()
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
                isUserReady();
            }
        });

        _passwordTxt.addTextChangedListener(new TextWatcher()
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
                isUserReady();
            }
        });
    }

    private void isUserReady()
    {
        _loginBtn.setEnabled(false);
        _loginBtn.setTextColor(getResources().getColor(R.color.gray));
        // The entries are empty
        if(_emailTxt.getText().toString().isEmpty() || _passwordTxt.getText().toString().isEmpty())
            return;

        // The password isn't long enough
        if(_passwordTxt.getText().toString().length() < 6)
            return;

        // The user is ready
        _loginBtn.setEnabled(true);
        _loginBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}
