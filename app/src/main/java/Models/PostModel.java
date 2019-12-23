package Models;

import java.io.Serializable;
import java.util.ArrayList;

public class PostModel implements Serializable
{
    public int PostId;
    public int UserId;
    public String Username;
    public String Title;
    public String Description;
    public Integer ProfileImage;
    public Integer PostImage;
    public Integer Likes = 0;
    public Integer CommentCount = 0;

    public PostModel(int postId, String username, String title, String description, Integer image, Integer postImage, Integer likes)
    {
        this.PostId = postId;
        this.Username = username;
        this.Title = title;
        this.Description = description;
        this.ProfileImage = image;
        this.PostImage = postImage;
        this.Likes = likes;
    }
}
