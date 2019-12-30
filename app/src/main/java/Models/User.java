package Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
    public String UserId;
    public String Username;
    public int ProfileImage;
    public String Description;
    public ArrayList<String> Follows;
    public ArrayList<String> Followers;
    public ArrayList<Post> SavedPosts;

    public User(String userId, String username, int profileImage, String description)
    {
        this.UserId = userId;
        this.Username = username;
        this.ProfileImage = profileImage;
        this.Description = description;

        SavedPosts =  new ArrayList<>();
        Followers = new ArrayList<>();
        Follows = new ArrayList<>();
    }

    public boolean hasSavedThisPost(String postId)
    {
        for(Post p : SavedPosts)
        {
            if(postId.equals(p.postId))
                return true;
        }
        return false;
    }

    public void savePost(Post post)
    {
        SavedPosts.add(post);
    }

    public void removePost(Post post)
    {
        SavedPosts.remove(post);
    }

    public ArrayList<Post> getSavedPosts()
    {
        return SavedPosts;
    }

    public int getFollowerCount(){return Followers.size(); }

    public boolean followsUserWithId(String userId)
    {
        for (String id : Follows)
        {
            if(userId.equals(id))
                return true;
        }
        return false;
    }

    public void follow(User user)
    {
        Follows.add(user.UserId);
        user.Followers.add(UserId);
    }

    public void unfollow(User user)
    {
        Follows.remove(user.UserId);
        user.Followers.remove(UserId);
    }
}
