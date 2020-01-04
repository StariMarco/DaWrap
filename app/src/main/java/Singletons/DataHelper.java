package Singletons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.alexzh.circleimageview.CircleImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Models.Post;
import Models.User;

public class DataHelper
{
    private static final String TAG = "DataHelper";
    public static DataHelper instance;

    public static FirebaseFirestore db;
    public static FirebaseStorage storage;

    private static User _currentUser;
    public static String _currentUserEmail;
    public static ArrayList<Post> _posts;
    private static ArrayList<User> _users;
    public static ArrayList<Post> _savedPosts;

    private static Map<String, Bitmap> _downloadedImages;

    public static void initInstance()
    {
        if(instance == null)
        {
            instance = new DataHelper();
            db = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
            _posts = new ArrayList<>();
            _users = new ArrayList<>();
            _savedPosts = new ArrayList<>();
            getUsersFromFirebase();
            _downloadedImages = new HashMap<>();
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
            if(u.userId.equals(id)) return u;
        }
        return null;
    }

    public static Post getPostById(String id)
    {
        for(Post p : _posts)
        {
            if(p.postId.equals(id)) return p;
        }
        return null;
    }

    // SET
    public static void setCurrentUser(User user)
    {
        _currentUser = user;
    }

    public static void downloadImageIntoView(ImageView view, String path, String tag, int resource)
    {
        downloadImageIntoPost(view, path, tag, resource, null);
    }

    public static void downloadImageIntoPost(ImageView view, String path, String tag, int resource, @Nullable View card)
    {
        if(isImageAlreadyDownloaded(path))
        {
            view.setImageBitmap(_downloadedImages.get(path));
            if(card != null) card.setVisibility(View.VISIBLE);
            return;
        }

        StorageReference storageReference = storage.getReference().child(path);
        storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            _downloadedImages.put(path, imageBitmap);
            view.setImageBitmap(imageBitmap);
            if(card != null) card.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Log.e(tag, "Error in downloading the image: ", e);
            view.setImageResource(resource);
        });
    }

    private static boolean isImageAlreadyDownloaded(String path)
    {
        return _downloadedImages.containsKey(path);
    }

    private static void getUsersFromFirebase()
    {
        db.collection("users").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot document : task.getResult())
                            _users.add(document.toObject(User.class));
                    }
                    else Log.e(TAG, "Error getting users", task.getException());
                });
    }

    private static void getSavedPostsFromFirebase()
    {
        db.collection("posts").whereIn("postId",_currentUser.savedPosts).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot document : task.getResult())
                            _savedPosts.add(document.toObject(Post.class));
                    }
                    else Log.e(TAG, "Error getting saved posts", task.getException());
                });
    }
}
