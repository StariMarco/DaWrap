# DaWrap

is a social network that allows people to satisfy their desire to get like by sharing their feelings, adventures and moments with their friends but most importantly with strangers.

## Features and component used inside the app

- [x] Activity
- [ ] Service
- [ ] Broadcast Receiver
- [ ] Content Provider
- [x] Intent: To pass data between activities
- [x] Fragments: 1. To move smoothly through home, create post and user profile 2. To move smoothly through the posts and the saved posts of the users
- [ ] Async Task
- [ ] Threads
- [ ] SQLite database engine
- [x] Firebase Cloud Firestore
- [x] Internet connectivity: to exchange data with the cloud service (firestore)
- [ ] Geo-location
- [ ] Localization
- [x] Multiple device layout and resolution support: smartphones and tablets
- [x] Hardware features: WiFi and camera
- [ ] Google Mobile Services
- [x] Web Application/Web Service interaction: 1. Firebase Auth 2. Cloud Firestore
- [x] Third party libraries: 1. Recyclerview: androidx.recyclerview:recyclerview:1.1.0 2. CiclerImageView: com.alexzh:circleimageview:1.2.0 3. Lottie: com.airbnb.android:lottie:3.3.1 4. Google Material Design: com.google.android.material:material:1.2.0-alpha02
- [x] Other embedded or involved technologies: 1. Image compression

## Key Features

L'applicazione è studiata per essere utilizzata da tutti in modo facile ed intuittivo, inoltre le animazioni e le scelte di design attuate consentono un'esperienza più fluida. L'implementazione di servizi cloud e di autenticazione di Firebase permettono agli utenti di accedere al proprio account in totale sicurezza e di utilizzarlo su più dispositivi in maniera efficiente.
Infine l'algoritmo di compressione delle immagini permette di memorizzare migliaia di post anche utilizzando solo il piano gratuito di Firebase.

# App structure
![GitHub Logo](/Usecase_diagram.png)

## Code Fragments

### Function to compress an image when the user selects it from the gallery

```
public Bitmap compressImage(Uri uriPhoto, Context context)
    {
        Bitmap result = null;
        // Get the image file
        File sourceFile = new File(getPathFromPhotosUri(uriPhoto, context));

        // Options to only get the 'out' fields allowing us to query the bitmap without having to allocate memory for its pixels
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        FileInputStream fis;
        try
        {
            fis = new FileInputStream(sourceFile);
            BitmapFactory.decodeStream(fis, null, options);
            fis.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;

        if(options.outHeight > IMAGE_MAX_SIZE || options.outWidth > IMAGE_MAX_SIZE)
        {
            // Get the scale size of the image
            // Math.ceil => rounds the provided value (4.3 => 5.0)
            // Math.log => natural logarithm (base e) (log(0.5) => -0,693147)
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double)Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
        }
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inSampleSize = scale;
        try
        {
            fis = new FileInputStream(sourceFile);
            result = BitmapFactory.decodeStream(fis, null, options1);
            fis.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
```

### Animate transition between fragments in MainActivity

```
private void transitionToFragment(Context context, final Fragment newFragment, @Nullable Interpolator interpolator)
    {
        // Get the view to animate
        View sheet = findViewById(R.id.fragment_container);

        float bounceDistance = SystemHelper.getPixelsFromDp(getResources(), 20);

        // Create the animator set to concatenate all animations
        AnimatorSet fragmentTransitionAnimatorSet = new AnimatorSet();

        // Create the animation
        ObjectAnimator initialBounce = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, bounceDistance);
        initialBounce.setDuration(200);
        initialBounce.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator finalBounce = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, 0);
        finalBounce.setDuration(200);
        finalBounce.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator transitionUp = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, -SystemHelper.getDisplayHeight(context));
        transitionUp.setDuration(300);
        if(interpolator != null)
            transitionUp.setInterpolator(interpolator);
        transitionUp.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // When the animation ends change the fragment inside the view
                super.onAnimationEnd(animation);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
            }
        });

        ObjectAnimator transitionDown = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y, bounceDistance);
        transitionDown.setDuration(500);
        if(interpolator != null)
            transitionDown.setInterpolator(interpolator);

        // Concatenate and start animations
        fragmentTransitionAnimatorSet.play(transitionUp).after(initialBounce);
        fragmentTransitionAnimatorSet.play(transitionUp).before(transitionDown);
        fragmentTransitionAnimatorSet.play(finalBounce).after(transitionDown);
        fragmentTransitionAnimatorSet.start();
    }
```

# Development

- Target API level: 29
- Minimum API level: 16
- IDE: Android Studio
- Man-hours:

## Problems and difficulties

- In the Post comment activity, allowing the user to slide the image up and down to see more comments
- Dealing with some bugs coming from the CiclerImageView
- Reduce the size of the data to save

## Reported bugs

## Further development

This project can be improved by:

- Allowing the user to create video posts
- Implementing stories (like instagram)
- Implementing tags to group posts into topics
- More profile personalization
- Creating a chat message to share posts with other users
- Create an iOS version

## Self rating

Since this was a school project i can give me 4 stars out of 5 because, for time constraints, I ignored many aspects and many possible bugs that in a real app would not be possible to ignore.

# References

www.google.it
