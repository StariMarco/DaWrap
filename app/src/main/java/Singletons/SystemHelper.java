package Singletons;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class SystemHelper
{
    public static SystemHelper instance;

    public static void initInstance()
    {
        if(instance == null)
        {
            instance = new SystemHelper();
        }


    }

    public static SystemHelper getInstance()
    {
        return instance;
    }

    private SystemHelper(){}

    public static float getPixelsFromDp(Resources r, float dp)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static float getDpfromPixels(final Context context, Resources r, final float px)
    {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int getDisplayHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }
}
