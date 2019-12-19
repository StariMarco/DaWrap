package CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.dawrap.R;

public class GradientTextView extends TextView
{
    public GradientTextView(Context context)
    {
        super(context);
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        //Setting the gradient if layout is changed
        if(changed && getMaxLines() == 10 && getLineCount() > 10)
        {
            getPaint().setShader(new LinearGradient(0, getHeight()/2, 0, getHeight(),
                    ContextCompat.getColor(getContext(), R.color.colorTextSecondary),
                    ContextCompat.getColor(getContext(), R.color.lightGray),
                    Shader.TileMode.CLAMP));
        }
        else {
            getPaint().setShader(new LinearGradient(0, getHeight()/2, 0, getHeight(),
                    ContextCompat.getColor(getContext(), R.color.colorTextSecondary),
                    ContextCompat.getColor(getContext(), R.color.colorTextSecondary),
                    Shader.TileMode.CLAMP));
        }
    }
}
