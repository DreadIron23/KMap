package sk.uniza.fri.janmokry.karnaughmap.kmap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import sk.uniza.fri.janmokry.karnaughmap.R;

/**
 * Individual {@link KMapCell} graphic representation of KMap configuration.
 *
 * Created by Janci on 7.4.2018.
 */

public enum ConfigurationShapes {

    SINGLE              (false, false, false, false, R.drawable.configuration_shape_single),
    LEFT                (true , false, false, false, R.drawable.configuration_shape_left),
    TOP                 (false, true , false, false, R.drawable.configuration_shape_top),
    RIGHT               (false, false, true , false, R.drawable.configuration_shape_right),
    BOTTOM              (false, false, false, true , R.drawable.configuration_shape_bottom),
    LEFT_TOP            (true , true , false, false, R.drawable.configuration_shape_left_top),
    TOP_RIGHT           (false, true , true , false, R.drawable.configuration_shape_top_right),
    RIGHT_BOTTOM        (false, false, true , true , R.drawable.configuration_shape_right_bottom),
    BOTTOM_LEFT         (true , false, false, true , R.drawable.configuration_shape_bottom_left),
    LEFT_TOP_RIGHT      (true , true , true , false, R.drawable.configuration_shape_left_top_right),
    TOP_RIGHT_BOTTOM    (false, true , true , true , R.drawable.configuration_shape_top_right_bottom),
    RIGHT_BOTTOM_LEFT   (true , false, true , true , R.drawable.configuration_shape_right_bottom_left),
    BOTTOM_LEFT_TOP     (true , true , false, true , R.drawable.configuration_shape_bottom_left_top),
    ALL                 (true , true , true , true , -1),
    LEFT_RIGHT          (true , false, true , false, R.drawable.configuration_shape_left_right),
    TOP_BOTTOM          (false, true , false, true , R.drawable.configuration_shape_tob_bottom);

    private final boolean isLeftNeighbourSet;
    private final boolean isTopNeighbourSet;
    private final boolean isRightNeighbourSet;
    private final boolean isBottomNeighbourSet;
    private final @DrawableRes int mDrawableRes;

    private Drawable mDrawable;

    ConfigurationShapes(boolean leftSet, boolean topSet,
                        boolean rightSet, boolean bottomSet, @DrawableRes int drawable) {
        isLeftNeighbourSet = leftSet;
        isTopNeighbourSet = topSet;
        isRightNeighbourSet = rightSet;
        isBottomNeighbourSet = bottomSet;
        mDrawableRes = drawable;
    }

    public static ConfigurationShapes getType(boolean leftSet, boolean topSet,
                                              boolean rightSet, boolean bottomSet) {
        for (ConfigurationShapes shape : ConfigurationShapes.values()) {
            if (shape.isLeftNeighbourSet == leftSet &&
                    shape.isTopNeighbourSet == topSet &&
                    shape.isRightNeighbourSet == rightSet &&
                    shape.isBottomNeighbourSet == bottomSet) {
                return shape;
            }
        }

        throw new IllegalArgumentException(
                String.format("Unknown ConfigurationShapes type: %b %b %b %b",
                        leftSet, topSet, rightSet, bottomSet));
    }

    /** @return null if it is {@link ConfigurationShapes#ALL} */
    public @Nullable Drawable getDrawable(Context context) {
        if (this == ALL) {
            return null;
        }
        if (mDrawable == null) {
            mDrawable = context.getResources().getDrawable(mDrawableRes);
        }
        return mDrawable;
    }
}
