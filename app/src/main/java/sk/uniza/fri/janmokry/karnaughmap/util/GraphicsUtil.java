package sk.uniza.fri.janmokry.karnaughmap.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;

/**
 * Graphic related utils
 */
public class GraphicsUtil {

    public static int dpToPx(@NonNull Resources res, int dp) {
        return (int) (dp * res.getDisplayMetrics().densityDpi / 160f);
    }

    public static int pxToDp(@NonNull Resources res, int px) {
        return (int) (px / res.getDisplayMetrics().densityDpi / 160f);
    }

    public static int fetchColor(@NonNull Context context, @AttrRes int attrId) {
        final TypedValue typedValue = new TypedValue();

        final TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{attrId});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    /**
     * @param coefficient 0.8 = 80%; 0.45 = 45% ect
     */
    public static @ColorInt int getLighterColor(@ColorInt int color, float coefficient) {

        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] *= coefficient;
        return Color.HSVToColor(hsv);
    }

    public static @ColorInt int setAlpha(@ColorInt int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
