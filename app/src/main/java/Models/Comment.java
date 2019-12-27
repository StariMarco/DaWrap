package Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Comment implements Serializable
{
    public int CommentId;
    public int UserId;
    public String Text;
    public Date Date;
    public ArrayList<Integer> Likes;

    public Comment(int commentId, int userId, String text)
    {
        this.CommentId = commentId;
        this.UserId = userId;
        this.Text = text;
        this.Date = new Date();
        this.Likes = new ArrayList<>();
    }

    public boolean hasUserLikedThisComment(Integer userId)
    {
        for(Integer like : Likes)
        {
            if(like.equals(userId))
                return true;
        }
        return false;
    }

    public int getLikesCount(){return Likes.size();}

    public void addLike(Integer userId)
    {
        Likes.add(userId);
    }

    public void removeLike(Integer userId)
    {
        Likes.remove(userId);
    }
}
