package Models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable
{
    public ArrayList<Comment> comments;
    public String description;
    public String image;
    public ArrayList<String> likes;
    public String postId;
    public String title;
    public String userId;

    public Post(){}

    public Post(String postId, String userId, String title, String description, String postImage, ArrayList<String> likes, ArrayList<Comment> comments)
    {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.image = postImage;
        this.likes = likes;
        this.comments = comments;
    }

    // Getters
    public ArrayList<String> getLikes(){return likes;}

    public ArrayList<Comment> getComments(){return comments;}

    public String getDescription() {return description;}

    public String getImage() {return image;}

    public String getPostId() {return postId;}

    public String getTitle() {return title;}

    public String getUserId() {return userId;}

//    public int getLikesCount(){return likes.size();}
//
//    public int getCommentsCount(){return comments.size();}

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
