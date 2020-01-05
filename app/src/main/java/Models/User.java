package Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Singletons.DataHelper;

public class User implements Serializable
{
    public String userId;
    public String username;
    public String profileImage;
    public String description;
    public ArrayList<String> follows;
    public ArrayList<String> followers;
    public ArrayList<String> savedPosts;

    public User(){}

    public User(String userId, String username, String profileImage, String description)
    {
        this.userId = userId;
        this.username = username;
        this.profileImage = profileImage;
        this.description = description;

        savedPosts =  new ArrayList<>();
        followers = new ArrayList<>();
        follows = new ArrayList<>();
    }

    public boolean hasSavedThisPost(String postId)
    {
        for(String id : savedPosts)
        {
            if(postId.equals(id))
                return true;
        }
        return false;
    }

    public void savePost(String postId)
    {
        savedPosts.add(postId);
    }

    public void removePost(String postId)
    {
        savedPosts.remove(postId);
    }

    public int followersCount(){return followers.size(); }

    public boolean followsUserWithId(String userId)
    {
        for (String id : follows)
        {
            if(userId.equals(id))
                return true;
        }
        return false;
    }

    public void follow(User user)
    {
        follows.add(user.userId);
        user.followers.add(userId);
        DataHelper.db.collection("users").document(DataHelper._currentUserEmail).update("follows", FieldValue.arrayUnion(user.userId))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        DataHelper.db.collection("users").whereEqualTo("userId", user.userId).get()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot document : task1.getResult())
                                        {
                                            DataHelper.db.collection("users").document(document.getId()).update("followers", FieldValue.arrayUnion(userId))
                                                    .addOnCompleteListener(task2 -> {
                                                        if(!task2.isSuccessful())
                                                        {
                                                            Log.e("User", "Error while following this user!", task.getException());
                                                            user.followers.remove(userId);
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Log.e("User", "Error while following this user!", task.getException());
                        follows.remove(user.userId);
                    }
                });
    }

    public void unfollow(User user)
    {
        follows.remove(user.userId);
        user.followers.remove(userId);
        DataHelper.db.collection("users").document(DataHelper._currentUserEmail).update("follows", FieldValue.arrayRemove(user.userId))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        DataHelper.db.collection("users").whereEqualTo("userId", user.userId).get()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot document : task1.getResult())
                                        {
                                            DataHelper.db.collection("users").document(document.getId()).update("followers", FieldValue.arrayRemove(userId))
                                                    .addOnCompleteListener(task2 -> {
                                                        if(!task2.isSuccessful())
                                                        {
                                                            Log.e("User", "Error while unfollowing this user!", task.getException());
                                                            user.followers.add(userId);
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Log.e("User", "Error while unfollowing this user!", task.getException());
                        follows.add(user.userId);
                    }
                });
    }

    // GET
    public int savedPostsCount()
    {
        return savedPosts.size();
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUsername()
    {
        return username;
    }

    public String getProfileImage()
    {
        return profileImage;
    }

    public String getDescription()
    {
        return description;
    }

    public ArrayList<String> getFollows()
    {
        return follows;
    }

    public ArrayList<String> getFollowers()
    {
        return followers;
    }
}
