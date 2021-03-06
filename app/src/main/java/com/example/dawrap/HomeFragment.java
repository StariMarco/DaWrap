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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import Adapters.PostAdapter;
import Models.Post;
import Models.User;
import Singletons.DataHelper;

public class HomeFragment extends Fragment
{
    private static final String TAG = "HomeFragment";

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

        if(DataHelper._posts.size() > 0)
        {
            postListViewSetup(view);
            return;
        }
        DataHelper.db.collection("posts")
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        User currentUser = DataHelper.getCurrentUser();
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            Post p = document.toObject(Post.class);
                            DataHelper._posts.add(p);
                            if(currentUser.hasSavedThisPost(p.postId))
                                DataHelper._savedPosts.add(p);
                        }
                        postListViewSetup(view);
                    }
                    else Log.e(TAG, "Error getting documents: ", task.getException());
                });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(_adapter == null && _postListView == null) return;

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
                    if(post.hasUserLikedThisPost(currentUser.userId))
                    {
                        // remove like
                        btn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        post.removeLike(currentUser.userId);
                        DataHelper.db.collection("posts").document(post.postId).update("likes", FieldValue.arrayRemove(currentUser.userId))
                                .addOnCompleteListener(task -> {
                                    if(!task.isSuccessful())
                                    {
                                        Log.e(TAG, "Unable to remove the like", task.getException());
                                        btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                                        post.addLike(currentUser.userId);
                                        likeTxt.setText(String.valueOf(post.likesCount()));
                                    }
                                });
                    }
                    else
                    {
                        // like
                        btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                        post.addLike(currentUser.userId);
                        DataHelper.db.collection("posts").document(post.postId).update("likes", FieldValue.arrayUnion(currentUser.userId))
                                .addOnCompleteListener(task -> {
                                    if(!task.isSuccessful())
                                    {
                                        Log.e(TAG, "Unable to like the post", task.getException());
                                        btn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                        post.removeLike(currentUser.userId);
                                        likeTxt.setText(String.valueOf(post.likesCount()));
                                    }
                                });
                    }
                }catch (Exception e)
                {
                    Log.e("HomeFragment", "Error in onLikeClick");
                }
                likeTxt.setText(String.valueOf(post.likesCount()));
            }

            @Override
            public void onSavePostClick(ImageButton btn, int position)
            {
                Post post = DataHelper.getPosts().get(position);
                User currentUser = DataHelper.getCurrentUser();

                if(currentUser.hasSavedThisPost(post.postId))
                {
                    // Remove from saved
                    btn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    DataHelper.db.collection("users").document(DataHelper._currentUserEmail).update("savedPosts", FieldValue.arrayRemove(post.postId))
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful())
                                {
                                    currentUser.removePost(post.postId);
                                    DataHelper._savedPosts.remove(post);
                                }
                                else
                                {
                                    Log.e(TAG, "Unable to unsave the post", task.getException());
                                    btn.setImageResource(R.drawable.ic_bookmark_black_24dp);
                                    Toast.makeText(getActivity(), "Unable to unsave the post", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {
                    // Save
                    btn.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    DataHelper.db.collection("users").document(DataHelper._currentUserEmail).update("savedPosts", FieldValue.arrayUnion(post.postId))
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful())
                                {
                                    currentUser.savePost(post.postId);
                                    DataHelper._savedPosts.add(post);
                                }
                                else
                                {
                                    Log.e(TAG, "Unable to save the post", task.getException());
                                    btn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                                    Toast.makeText(getActivity(), "Unable to save the post", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override // event listener for view comment button in post layout
            public void onCommentClick(int position)
            {
                Intent i = new Intent(getActivity(), PostCommentsActivity.class);
                i.putExtra("POST_ID", DataHelper.getPosts().get(position).postId);
                startActivity(i);
            }

            @Override
            public void onProfileImageClick(int position)
            {
                Post post = DataHelper.getPosts().get(position);
                User user = DataHelper.getUserById(post.userId);

                Intent i = new Intent(getActivity(), UserProfileActivity.class);
                i.putExtra("USER_ID", user.userId);
                startActivity(i);
            }
        });
    }
}
