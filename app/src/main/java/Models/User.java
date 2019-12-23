package Models;

public class User
{
    public int UserId;
    public String Username;
    public int ProfileImage;

    public User(int userId, String username, int profileImage)
    {
        this.UserId = userId;
        this.Username = username;
        this.ProfileImage = profileImage;
    }
}
