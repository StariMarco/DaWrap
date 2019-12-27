package Models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable
{
    public int PostId;
    public int UserId;
    public String Title;
    public String Description;
    public Integer Image;
    public ArrayList<Integer> Likes;
    public Integer CommentCount = 0;
    public ArrayList<Comment> Comments;

    public Post(int postId, int userId, String title, String description, Integer postImage, ArrayList<Comment> comments)
    {
        this.PostId = postId;
        this.UserId = userId;
        this.Title = title;
        this.Description = description;
        this.Image = postImage;
        this.Likes = new ArrayList<>();
        this.Comments = comments;
    }

    public void addLike(Integer userId)
    {
        Likes.add(userId);

        Log.d("Post", Likes.toString());
    }

    public void removeLike(Integer userId)
    {
        Likes.remove(userId);
        Log.d("Post", Likes.toString());
    }

    public int getLikesCount(){return Likes.size();}

    public ArrayList<Comment> getComments(){return Comments;}

    public boolean hasUserLikedThisPost(Integer userId)
    {
        for(Integer like : Likes)
        {
            if(like.equals(userId))
                return true;
        }
        return false;
    }
}
