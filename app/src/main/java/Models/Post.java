package Models;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable
{
    public String PostId;
    public String UserId;
    public String Title;
    public String Description;
    public Bitmap Image;
    public ArrayList<String> Likes;
    public Integer CommentCount = 0;
    public ArrayList<Comment> Comments;

    public Post(String postId, String userId, String title, String description, Bitmap postImage, ArrayList<Comment> comments)
    {
        this.PostId = postId;
        this.UserId = userId;
        this.Title = title;
        this.Description = description;
        this.Image = postImage;
        this.Likes = new ArrayList<>();
        this.Comments = comments;
    }

    public void addLike(String userId)
    {
        Likes.add(userId);

        Log.d("Post", Likes.toString());
    }

    public void removeLike(String userId)
    {
        Likes.remove(userId);
        Log.d("Post", Likes.toString());
    }

    public int getLikesCount(){return Likes.size();}

    public ArrayList<Comment> getComments(){return Comments;}

    public boolean hasUserLikedThisPost(String userId)
    {
        for(String like : Likes)
        {
            if(like.equals(userId))
                return true;
        }
        return false;
    }

    public void addComment(Comment comment)
    {
        Comments.add(comment);
    }

    public void deleteComment(Comment comment)
    {
        Comments.remove(comment);
    }
}
