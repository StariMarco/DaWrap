package com.example.dawrap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Adapters.PostAdapter;
import Models.DataHelper;

public class HomeFragment extends Fragment
{
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

        shapeBackground(view);

        postListViewSetup(view);
    }

    private void shapeBackground(@NonNull View view)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            view.findViewById(R.id.postBackground).setBackgroundResource(R.drawable.post_backgroung_shape);
            view.findViewById(R.id.postListView).setBackgroundResource(R.drawable.post_backgroung_shape);
        }
    }

    private void postListViewSetup(View view)
    {
        RecyclerView postListView = view.findViewById(R.id.postListView);
//        System.out.println("id: " + postListView.getId());

        PostAdapter adapter = new PostAdapter(DataHelper.getPosts());
        postListView.setAdapter(adapter);
        postListView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener()
        {
            @Override // event listener for view comment button in post layout
            public void onCommentClick(int position)
            {
                Intent i = new Intent(getActivity(), PostCommentsActivity.class);
                i.putExtra("POST_DATA", DataHelper.getPosts().get(position));
                startActivity(i);
            }
        });
    }
}
