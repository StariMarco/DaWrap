package com.example.dawrap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Adapters.PostAdapter;
import Models.Post;
import Models.User;
import Singletons.DataHelper;

public class HomeFragment extends Fragment
{
    private RecyclerView _postListView;
    private PostAdapter _adapter;

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

        setBackgroundShape(view);

        postListViewSetup(view);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        _adapter.notifyDataSetChanged();
        _postListView.setAdapter(_adapter);
    }

    private void setBackgroundShape(@NonNull View view)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            view.findViewById(R.id.post_list_background).setBackgroundResource(R.drawable.post_backgroung_shape);
            view.findViewById(R.id.post_list_view).setBackgroundResource(R.drawable.post_backgroung_shape);
        }
    }

    private void postListViewSetup(View view)
    {
        _postListView = view.findViewById(R.id.post_list_view);

        _adapter = new PostAdapter(DataHelper.getPosts());
        _postListView.setAdapter(_adapter);
        _postListView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        _adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener()
        {
            @Override // event listener for like button in post layout
            public void onLikeClick(ImageButton btn, TextView likeTxt, int position)
            {
                Post post = DataHelper.getPosts().get(position);
                User currentUser = DataHelper.getCurrentUser();
                if(post == null || btn == null)
                {
                    Log.e("HomeFragment", "Error! post or like button not found");
                    return;
                }
                try
                {
                    if(post.hasUserLikedThisPost(currentUser.UserId))
                    {
                        // remove like
                        btn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        post.removeLike(currentUser.UserId);

                    }
                    else
                    {
                        // like
                        btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                        post.addLike(currentUser.UserId);
                    }
                }catch (Exception e)
                {
                    Log.e("HomeFragment", "Error in onLikeClick");
                }
                likeTxt.setText(String.valueOf(post.getLikesCount()));
            }

            @Override // event listener for view comment button in post layout
            public void onCommentClick(int position)
            {
                Intent i = new Intent(getActivity(), PostCommentsActivity.class);
                i.putExtra("POST_ID", DataHelper.getPosts().get(position).PostId);
                startActivity(i);
            }

            @Override
            public void onProfileImageClick(int position)
            {
                Post post = DataHelper.getPosts().get(position);
                User user = DataHelper.getUserById(post.UserId);

                Intent i = new Intent(getActivity(), UserProfileActivity.class);
                i.putExtra("USER_ID", user.UserId);
                startActivity(i);
            }
        });
    }
}
