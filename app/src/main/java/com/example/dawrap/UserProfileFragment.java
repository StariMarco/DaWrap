package com.example.dawrap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;

import java.util.ArrayList;

import Adapters.PostAdapter;
import Adapters.ProfilePostsAdapter;
import Models.Post;
import Models.User;
import Singletons.DataHelper;

public class UserProfileFragment extends Fragment
{
    private RecyclerView _postListView;
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

        userPostListViewSetup(view);

        ((CircleImageView)view.findViewById(R.id.usr_profile_image)).setOnItemSelectedClickListener(new ItemSelectedListener()
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

    private void userPostListViewSetup(@NonNull View view)
    {
        _postListView = view.findViewById(R.id.usr_post_list);

        User currentUser = DataHelper.getCurrentUser();
        ArrayList<Post> userPosts = new ArrayList<>();
        for(Post p : DataHelper.getPosts())
        {
            if(p.UserId == currentUser.UserId)
                userPosts.add(p);
        }

        ProfilePostsAdapter adapter = new ProfilePostsAdapter(userPosts);
        _postListView.setAdapter(adapter);
        _postListView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter.setOnItemClickListener(new ProfilePostsAdapter.OnItemClickListener()
        {
            @Override
            public void onImageClicked(int position)
            {
                Intent i = new Intent(getActivity(), PostCommentsActivity.class);
                i.putExtra("POST_DATA", userPosts.get(position));
                startActivity(i);
            }
        });
    }
}
