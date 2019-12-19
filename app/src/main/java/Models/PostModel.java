package Models;

public class PostModel
{
    public String Username;
    public String Title;
    public String Description;
    public Integer ProfileImage;
    public Integer PostImage;

    public PostModel(String username, String title, String description, Integer image, Integer postImage)
    {
        this.Username = username;
        this.Title = title;
        this.Description = description;
        this.ProfileImage = image;
        this.PostImage = postImage;
    }
}
