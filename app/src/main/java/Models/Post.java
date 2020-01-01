package Models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable
{
    public String postId;
    public String userId;
    public String title;
    public String description;
    public String image;
    public ArrayList<String> likes;
    public ArrayList<Comment> comments;
    public String creationDate;

    public Post(){}

    // Getters
    public ArrayList<String> getLikes(){return likes;}

    public ArrayList<Comment> getComments(){return comments;}

    public String getDescription() {return description;}

    public String getImage() {return image;}

    public String getPostId() {return postId;}

    public String getTitle() {return title;}

    public String getUserId() {return userId;}

    public int likesCount(){return likes.size();}

    public int commentsCount(){return comments.size();}

    public void addLike(String userId)
    {
        likes.add(userId);

        Log.d("Post", likes.toString());
    }

    public void removeLike(String userId)
    {
        likes.remove(userId);
        Log.d("Post", likes.toString());
    }

    public boolean hasUserLikedThisPost(String userId)
    {
        for(String like : likes)
        {
            if(like.equals(userId))
                return true;
        }
        return false;
    }

    public void addComment(Comment comment)
    {
        comments.add(comment);
    }

    public void deleteComment(Comment comment)
    {
        comments.remove(comment);
    }
}
