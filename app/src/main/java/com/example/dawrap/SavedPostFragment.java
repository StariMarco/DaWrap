package com.example.dawrap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import Adapters.ProfilePostsAdapter;
import Models.Post;
import Models.User;
import Singletons.DataHelper;

public class SavedPostFragment extends Fragment
{
    RecyclerView _postListView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_saved_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        _postListView = view.findViewById(R.id.usr_saved_post_list);
        ArrayList<Post> userSavedPosts = DataHelper.getCurrentUser().getSavedPosts();

        if(userSavedPosts.size() > 0)
            // Load the saved post list view
            userPostListViewSetup(view, userSavedPosts);
        else
        {
            // If the user didn't save any post show the "not found" animation
            _postListView.setVisibility(View.GONE);
            view.findViewById(R.id.not_found_layout).setVisibility(View.VISIBLE);
        }
    }

    private void userPostListViewSetup(@NonNull View view, ArrayList<Post> posts)
    {
        ProfilePostsAdapter adapter = new ProfilePostsAdapter(posts);
        _postListView.setAdapter(adapter);
        _postListView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter.setOnItemClickListener(new ProfilePostsAdapter.OnItemClickListener()
        {
            @Override
            public void onImageClicked(int position)
            {
                Intent i = new Intent(getActivity(), PostCommentsActivity.class);
                i.putExtra("POST_ID", posts.get(position).PostId);
                startActivity(i);
            }
        });
    }
}
