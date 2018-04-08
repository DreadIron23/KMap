package sk.uniza.fri.janmokry.karnaughmap.data;

import android.content.Context;
import android.support.annotation.ColorInt;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Service providing color palette of maximum contrast colors for given number of items.
 *
 * Created by Janci on 7.4.2018.
 */

public class ColorPaletteService implements SL.IService {

    private final @ColorInt int[] mPalette9;
    private final @ColorInt int[] mPalette20;

    public ColorPaletteService(Context context) {
        mPalette9 = context.getResources().getIntArray(R.array.palette_of_9_colors);
        mPalette20 = context.getResources().getIntArray(R.array.palette_of_20_colors);
    }

    public int[] providePaletteFor(int numberOfItems) {
        if (numberOfItems <= mPalette9.length) {
            return mPalette9;
        }
        return mPalette20;
    }
}
