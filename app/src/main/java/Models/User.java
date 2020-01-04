package Models;

import java.io.Serializable;
import java.util.ArrayList;

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
    }

    public void unfollow(User user)
    {
        follows.remove(user.userId);
        user.followers.remove(userId);
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
