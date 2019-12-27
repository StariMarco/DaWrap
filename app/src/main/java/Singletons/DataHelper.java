package Singletons;

import com.example.dawrap.R;

import java.util.ArrayList;

import Models.Comment;
import Models.Post;
import Models.User;

public class DataHelper
{
    public static DataHelper instance;

    private static User _currentUser;
    private static ArrayList<Post> _posts;
    private static ArrayList<User> _users;
    private static ArrayList<Integer> _likesId;

    public static void initInstance()
    {
        if(instance == null)
        {
            instance = new DataHelper();
            _posts = setPosts();
            _users = setUsers();
            _likesId = new ArrayList<>();
            _currentUser = new User(0, "Marco", R.drawable.profile_img_test, "Questa è una prova per una possibile descrizione di un profilo utente");
        }


    }

    public static DataHelper getInstance()
    {
        return instance;
    }

    private DataHelper(){}

    public static ArrayList<Post> getPosts()
    {
        return _posts;
    }

    public static ArrayList<User> getUsers()
    {
        return _users;
    }

    public static User getCurrentUser() {return _currentUser;}

    public static User getUserById(int id)
    {
        for(User u : _users)
        {
            if(u.UserId == id) return u;
        }
        return null;
    }

    public static Post getPostById(int id)
    {
        for(Post p : _posts)
        {
            if(p.PostId == id) return p;
        }
        return null;
    }

    public static boolean hasLikedPostWithId(int postId)
    {
        for(Integer likeId : _likesId)
        {
            if(likeId == postId)
                return true;
        }
        return false;
    }

    public static void addLike(int id)
    {
        if (!hasLikedPostWithId(id))
            _likesId.add(id);
    }

    public static void removeLike(Integer id)
    {
        if (hasLikedPostWithId(id))
            _likesId.remove(id);
    }

    private static ArrayList<Post> setPosts()
    {
        ArrayList<Post> list = new ArrayList<>();
        ArrayList<Comment> marcoComments = new ArrayList<>();
        marcoComments.add(new Comment(0, 0, "Hey ho perso le mutandine. Aiutami a ritrovarle!"));
        marcoComments.add(new Comment(1, 0, "You got me in the first half, not gonna lie"));
        marcoComments.add(new Comment(2, 0, "Love ya!"));
        marcoComments.add(new Comment(3, 0, "Comment Rossi"));
        marcoComments.add(new Comment(4, 0, "Nice pick"));
        marcoComments.add(new Comment(5, 0, "U dump bitch"));

        ArrayList<Comment> pippoComments = new ArrayList<>();
        pippoComments.add(new Comment(6, 1, "Frega niente!"));
        pippoComments.add(new Comment(7, 1, "He wouldn't get it"));

        ArrayList<Comment> paoloComments = new ArrayList<>();
        paoloComments.add(new Comment(8, 2, "Da wae"));

        ArrayList<Comment> rossiComments = new ArrayList<>();
        rossiComments.add(new Comment(9, 3, "Da wae"));

        ArrayList<Comment> mattiaComments = new ArrayList<>();
        mattiaComments.add(new Comment(10, 4, "Marcello what u doing?"));


        list.add(new Post(0, 0, "Titolo Marco", null, R.drawable.post_img_test_1, marcoComments));
        list.add(new Post(1, 1, "Titolo Pippo", "test_description\">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n" +
           "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, pippoComments));
        list.add(new Post(2, 2, "Titolo Paolo", null, R.drawable.post_img_test_2, paoloComments));
        list.add(new Post(3, 3, "Titolo Rossi", null, R.drawable.post_img_test_3, rossiComments));
        list.add(new Post(4, 4, "Titolo Mattia", "Descrizione Mattia", null, mattiaComments));
        list.add(new Post(5, 0, "Titolo Marco", null, R.drawable.post_img_test_2, new ArrayList<>()));
        list.add(new Post(6, 0, "Titolo Marco", null, R.drawable.post_img_test_3, new ArrayList<>()));
        list.add(new Post(7, 0, "Titolo Marco", null, R.drawable.post_img_test_1, new ArrayList<>()));
        list.add(new Post(8, 0, "Titolo Marco", null, R.drawable.post_img_test_3, new ArrayList<>()));
        list.add(new Post(9, 0, "Titolo Marco", null, R.drawable.post_img_test_2, new ArrayList<>()));
        list.add(new Post(10, 0, "Titolo Marco", null, R.drawable.post_img_test_3, new ArrayList<>()));
        list.add(new Post(11, 0, "Titolo Marco", null, R.drawable.post_img_test_1, new ArrayList<>()));
        list.add(new Post(12, 0, "Titolo Marco", null, R.drawable.post_img_test_2, new ArrayList<>()));
        list.add(new Post(14, 1, "Titolo Paolo", null, R.drawable.post_img_test_2, new ArrayList<>()));
        return list;
    }

    private static ArrayList<User> setUsers()
    {
        ArrayList<User> list = new ArrayList<>();
        list.add(new User(0, "Marco", R.drawable.profile_img_test, "Questa è una prova per una possibile descrizione di un profilo utente"));
        list.add(new User(1, "Pippo", R.drawable.profile_img_test, "Descrizione di Pippo"));
        list.add(new User(2, "Paolo", R.drawable.profile_img_test, "Descrizione di Paolo"));
        list.add(new User(3, "Rossi", R.drawable.profile_img_test, "Descrizione di Rossi"));
        list.add(new User(4, "Mattia", R.drawable.profile_img_test, "Descrizione di Mattia"));
        return list;
    }

}
