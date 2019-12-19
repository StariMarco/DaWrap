package com.example.dawrap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Adapters.PostAdapter;
import Models.PostModel;

public class HomeFragment extends Fragment
{
    ArrayList<PostModel> posts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        postListViewSetup(view);
    }

    private void postListViewSetup(View view)
    {
        RecyclerView postListView = view.findViewById(R.id.postListView);
        System.out.println("id: " + postListView.getId());
        posts.add(new PostModel("Marco", "Titolo Marco", null, R.drawable.profile_img_test, R.drawable.post_img_test_1));
        posts.add(new PostModel("Pippo", "Titolo Pippo", getString(R.string.test_description), R.drawable.profile_img_test, null));
        posts.add(new PostModel("Paolo", "Titolo Paolo", null, R.drawable.profile_img_test, R.drawable.post_img_test_2));
        posts.add(new PostModel("Rossi", "Titolo Rossi", null, R.drawable.profile_img_test, R.drawable.post_img_test_3));
        posts.add(new PostModel("Mattia", "Titolo Mattia", "Descrizione Mattia", R.drawable.profile_img_test, null));

        PostAdapter adapter = new PostAdapter(posts);
        postListView.setAdapter(adapter);
        postListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }
}
