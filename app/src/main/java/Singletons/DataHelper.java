package Singletons;

import com.example.dawrap.R;

import java.util.ArrayList;

import Models.Comment;
import Models.PostModel;
import Models.User;

public class DataHelper
{
    public static DataHelper instance;

    private static ArrayList<PostModel> _posts;
    private static ArrayList<User> _users;
    private static ArrayList<Comment> _comments;

    public static void initInstance()
    {
        if(instance == null)
        {
            instance = new DataHelper();
            _posts = setPosts();
            _users = setUsers();
            _comments = setComments();
        }


    }

    public static DataHelper getInstance()
    {
        return instance;
    }

    private DataHelper(){}

    public static ArrayList<PostModel> getPosts()
    {
        return _posts;
    }

    public static ArrayList<User> getUsers()
    {
        return _users;
    }

    public static ArrayList<Comment> getComments()
    {
        return _comments;
    }

    private static ArrayList<PostModel> setPosts()
    {
        ArrayList<PostModel> list = new ArrayList<>();
        list.add(new PostModel(0, "Marco", "Titolo Marco", null, R.drawable.profile_img_test, R.drawable.post_img_test_1, 100));
        list.add(new PostModel(1, "Pippo", "Titolo Pippo", "test_description\">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n" +
           "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.profile_img_test, null, 5));
        list.add(new PostModel(2, "Paolo", "Titolo Paolo", null, R.drawable.profile_img_test, R.drawable.post_img_test_2, 47));
        list.add(new PostModel(3, "Rossi", "Titolo Rossi", null, R.drawable.profile_img_test, R.drawable.post_img_test_3, 907));
        list.add(new PostModel(4, "Mattia", "Titolo Mattia", "Descrizione Mattia", R.drawable.profile_img_test, null, 16));
        return list;
    }

    private static ArrayList<User> setUsers()
    {
        ArrayList<User> list = new ArrayList<>();
        list.add(new User(0, "Marco", R.drawable.profile_img_test));
        list.add(new User(1, "Pippo", R.drawable.profile_img_test));
        list.add(new User(2, "Paolo", R.drawable.profile_img_test));
        list.add(new User(3, "Rossi", R.drawable.profile_img_test));
        list.add(new User(4, "Mattia", R.drawable.profile_img_test));
        return list;
    }

    private static ArrayList<Comment> setComments()
    {
        ArrayList<Comment> list = new ArrayList<>();
        list.add(new Comment(0, 0, 4, "Marcello what u doing?", 4));
        list.add(new Comment(1, 1, 3, "Da wae", 7));
        list.add(new Comment(2, 2, 1, "Frega niente!", 1));
        list.add(new Comment(3, 3, 2, "Remember monday presentation bruh", 45));
        list.add(new Comment(4, 4, 2, "Wtf? what is this shitty app", 2));
        list.add(new Comment(5, 2, 0, "I bet u won't check my stories", 0));
        list.add(new Comment(6, 2, 1, "He wouldn't get it", 1));
        list.add(new Comment(7, 1, 0, "Hey ho perso le mutandine. Aiutami a ritrovarle!", 5));
        list.add(new Comment(8, 0, 0, "You got me in the first half, not gonna lie", 29));
        list.add(new Comment(9, 4, 0, "Love ya!", 56));
        list.add(new Comment(10, 1, 0, "Comment Rossi", 5));
        list.add(new Comment(11, 0, 0, "Nice pick", 29));
        list.add(new Comment(12, 4, 0, "U dump bitch", 56));
        return list;
    }
}
