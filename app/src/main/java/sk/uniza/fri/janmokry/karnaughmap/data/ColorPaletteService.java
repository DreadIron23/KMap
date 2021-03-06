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

    private final @ColorInt int[] mPalette8;
    private final @ColorInt int[] mPalette20;

    public ColorPaletteService(Context context) {
        mPalette8 = context.getResources().getIntArray(R.array.palette_of_8_colors);
        mPalette20 = context.getResources().getIntArray(R.array.palette_of_20_colors);
    }

    public int[] providePaletteFor(int numberOfItems) {
        if (numberOfItems <= mPalette8.length) {
            return mPalette8;
        }
        return mPalette20;
    }
}
