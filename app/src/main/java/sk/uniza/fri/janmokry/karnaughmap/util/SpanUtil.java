package sk.uniza.fri.janmokry.karnaughmap.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import sk.uniza.fri.janmokry.karnaughmap.text.LowerCaseOverlineSpan;

/**
 * Convenient functions for spans.
 *
 * Created by Janci on 14.4.2018.
 */

public class SpanUtil {

    /** @param variableName one character name length followed by number which will be spanned to
     * "subscript" */
    public static CharSequence spanVariableName(String variableName) {
        final SpannableString spannableString = new SpannableString(variableName);
        spannableString.setSpan(new RelativeSizeSpan(0.5f), 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(Typeface.BOLD, 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static CharSequence spanVariableNameNegated(String variableName) {
        final SpannableString spannableString = new SpannableString("." + variableName + ".");
        final int length = spannableString.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new LowerCaseOverlineSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new RelativeSizeSpan(0.5f), 2, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(Typeface.BOLD, 2, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static CharSequence spanIntegerToLowerCase(int number) {
        final SpannableString spannableString = new SpannableString("." + String.valueOf(number) + ".");
        final int length = spannableString.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.75f), 1, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
