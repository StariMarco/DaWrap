package Models;

import java.io.Serializable;
import java.util.ArrayList;

public class PostModel implements Serializable
{
    public String Username;
    public String Title;
    public String Description;
    public Integer ProfileImage;
    public Integer PostImage;
    public Integer Likes = 0;
    public ArrayList<Comment> Comments = new ArrayList<>();


    public PostModel(String username, String title, String description, Integer image, Integer postImage, Integer likes)
    {
        this.Username = username;
        this.Title = title;
        this.Description = description;
        this.ProfileImage = image;
        this.PostImage = postImage;
        this.Likes = likes;
    }

    public void addComment(Comment comment)
    {
        this.Comments.add(comment);
    }
}
