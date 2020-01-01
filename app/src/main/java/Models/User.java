package Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
    public String userId;
    public String username;
    public int profileImage;
    public String description;
    public ArrayList<String> follows;
    public ArrayList<String> followers;
    public ArrayList<Post> savedPosts;

    public User(){}

    public User(String userId, String username, int profileImage, String description)
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
        for(Post p : savedPosts)
        {
            if(postId.equals(p.postId))
                return true;
        }
        return false;
    }

    public void savePost(Post post)
    {
        savedPosts.add(post);
    }

    public void removePost(Post post)
    {
        savedPosts.remove(post);
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
    public ArrayList<Post> getSavedPosts()
    {
        return savedPosts;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUsername()
    {
        return username;
    }

    public int getProfileImage()
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
