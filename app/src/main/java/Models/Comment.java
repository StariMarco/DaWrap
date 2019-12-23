package Models;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable
{
    public int CommentId;
    public int UserId;
    public int PostId;
    public String Text;
    public Date Date;
    public int Likes = 0;

    public Comment(int commentId, int userId, int postId, String text, int likes)
    {
        this.CommentId = commentId;
        this.UserId = userId;
        this.PostId = postId;
        this.Text = text;
        this.Date = new Date();
        this.Likes = likes;
    }
}
