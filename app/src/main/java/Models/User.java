package Models;

import java.io.Serializable;

public class User implements Serializable
{
    public int UserId;
    public String Username;
    public int ProfileImage;
    public String Description;

    public User(int userId, String username, int profileImage, String description)
    {
        this.UserId = userId;
        this.Username = username;
        this.ProfileImage = profileImage;
        this.Description = description;
    }
}
