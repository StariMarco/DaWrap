package Models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Comment implements Serializable
{
    public String commentId;
    public String userId;
    public String text;
    public ArrayList<String> likes;
    public String creationDate;

    public Comment(){}

    public Comment(String commentId, String userId, String text)
    {
        this.commentId = commentId;
        this.userId = userId;
        this.text = text;
        this.likes = new ArrayList<>();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.creationDate = df.format(new Date());
    }

    public boolean hasUserLikedThisComment(String userId)
    {
        for(String like : likes)
        {
            if(like.equals(userId))
                return true;
        }
        return false;
    }

    public int likesCount(){return likes.size();}

    public void addLike(String userId)
    {
        likes.add(userId);
    }

    public void removeLike(String userId)
    {
        likes.remove(userId);
    }
}
