package com.example.dawrap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import Models.User;

public class EditUserActivity extends AppCompatActivity
{
    private TextInputEditText _txtUsername;
    private TextInputEditText _txtDescription;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        _txtUsername = findViewById(R.id.txt_username_edit);
        _txtDescription = findViewById(R.id.txt_description_edit);

        user = (User) getIntent().getSerializableExtra("USER");
        _txtUsername.setText(user.username);
        _txtDescription.setText(user.description);

    }

    public void onBackBtnClick(View view)
    {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onSaveChangesClick(View view)
    {
        user.username = _txtUsername.getText().toString();
        user.description = _txtDescription.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("RESULT", user);
        setResult(RESULT_OK, intent);
        finish();
    }
}
