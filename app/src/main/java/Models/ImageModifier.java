package Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageModifier
{
    public ImageModifier(){}

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

    private String getPathFromPhotosUri(Uri uriPhoto, Context context) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(context);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }
}
