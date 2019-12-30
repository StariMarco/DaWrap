package Singletons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dawrap.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import Models.Comment;
import Models.Post;
import Models.User;

public class DataHelper
{
    public static DataHelper instance;

    public static FirebaseFirestore db;
    public static FirebaseStorage storage;

    private static User _currentUser;
    private static ArrayList<Post> _posts;
    private static ArrayList<User> _users;

    private static ArrayList<Bitmap> _testImages;

    public static void initInstance()
    {
        if(instance == null)
        {
            instance = new DataHelper();
            db = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
        }


    }

    public static DataHelper getInstance()
    {
        return instance;
    }

    private DataHelper(){}

    // GET
    public static ArrayList<Post> getPosts()
    {
        return _posts;
    }

    public static ArrayList<User> getUsers()
    {
        return _users;
    }

    public static User getCurrentUser() {return _currentUser;}

    public static User getUserById(String id)
    {
        for(User u : _users)
        {
            if(u.UserId.equals(id)) return u;
        }
        return null;
    }

    public static Post getPostById(String id)
    {
        for(Post p : _posts)
        {
            if(p.PostId.equals(id)) return p;
        }
        return null;
    }

    // SET
    public static void setImages(ArrayList<Bitmap> list)
    {
        _testImages = list;
        _posts = setPosts();
        _users = setUsers();
        _currentUser = new User("0", "Marco", R.drawable.profile_img_test, "Questa è una prova per una possibile descrizione di un profilo utente");
    }

    private static ArrayList<Post> setPosts()
    {
        ArrayList<Post> list = new ArrayList<>();
        ArrayList<Comment> marcoComments = new ArrayList<>();
        marcoComments.add(new Comment("0", "0", "Hey ho perso le mutandine. Aiutami a ritrovarle!"));
        marcoComments.add(new Comment("1", "0", "You got me in the first half, not gonna lie"));
        marcoComments.add(new Comment("2", "0", "Love ya!"));
        marcoComments.add(new Comment("3", "0", "Comment Rossi"));
        marcoComments.add(new Comment("4", "0", "Nice pick"));
        marcoComments.add(new Comment("5", "0", "U dump bitch"));

        ArrayList<Comment> pippoComments = new ArrayList<>();
        pippoComments.add(new Comment("6", "1", "Frega niente!"));
        pippoComments.add(new Comment("7", "1", "He wouldn't get it"));

        ArrayList<Comment> paoloComments = new ArrayList<>();
        paoloComments.add(new Comment("8", "2", "Da wae"));

        ArrayList<Comment> rossiComments = new ArrayList<>();
        rossiComments.add(new Comment("9", "3", "Da wae"));

        ArrayList<Comment> mattiaComments = new ArrayList<>();
        mattiaComments.add(new Comment("10", "4", "Marcello what u doing?"));


        list.add(new Post("0", "0", "Titolo Marco", null, _testImages.get(0), new ArrayList<>(), marcoComments));
        list.add(new Post("1", "1", "Titolo Pippo", "test_description\">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n" +
           "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, new ArrayList<>(), pippoComments));
        list.add(new Post("2", "2", "Titolo Paolo", null, _testImages.get(1), new ArrayList<>(), paoloComments));
        list.add(new Post("3", "3", "Titolo Rossi", null, _testImages.get(2), new ArrayList<>(), rossiComments));
        list.add(new Post("4", "4", "Titolo Mattia", "Descrizione Mattia", null, new ArrayList<>(), mattiaComments));
        list.add(new Post("5", "0", "Titolo Marco", null, _testImages.get(1), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("6", "0", "Titolo Marco", null, _testImages.get(2), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("7", "0", "Titolo Marco", null, _testImages.get(0), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("8", "0", "Titolo Marco", null, _testImages.get(2), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("9", "0", "Titolo Marco", null, _testImages.get(1), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("10", "0", "Titolo Marco", null, _testImages.get(2), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("11", "0", "Titolo Marco", null, _testImages.get(0), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("12", "0", "Titolo Marco", null, _testImages.get(1), new ArrayList<>(), new ArrayList<>()));
        list.add(new Post("13", "1", "Titolo Paolo", null, _testImages.get(1), new ArrayList<>(), new ArrayList<>()));
        return list;
    }

    private static ArrayList<User> setUsers()
    {
        ArrayList<User> list = new ArrayList<>();
        list.add(new User("0", "Marco", R.drawable.profile_img_test, "Questa è una prova per una possibile descrizione di un profilo utente"));
        list.add(new User("1", "Pippo", R.drawable.profile_img_test, "Descrizione di Pippo"));
        list.add(new User("2", "Paolo", R.drawable.profile_img_test, "Descrizione di Paolo"));
        list.add(new User("3", "Rossi", R.drawable.profile_img_test, "Descrizione di Rossi"));
        list.add(new User("4", "Mattia", R.drawable.profile_img_test, "Descrizione di Mattia"));
        return list;
    }

}
