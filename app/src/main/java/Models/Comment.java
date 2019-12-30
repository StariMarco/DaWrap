package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Comment implements Serializable
{
    public String CommentId;
    public String UserId;
    public String Text;
    public Date Date;
    public ArrayList<String> Likes;

    public Comment(String commentId, String userId, String text)
    {
        this.CommentId = commentId;
        this.UserId = userId;
        this.Text = text;
        this.Date = new Date();
        this.Likes = new ArrayList<>();
    }

    public boolean hasUserLikedThisComment(String userId)
    {
        for(String like : Likes)
        {
            if(like.equals(userId))
                return true;
        }
        return false;
    }

    public int getLikesCount(){return Likes.size();}

    public void addLike(String userId)
    {
        Likes.add(userId);
    }

    public void removeLike(String userId)
    {
        Likes.remove(userId);
    }
}
