package sk.uniza.fri.janmokry.karnaughmap.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;

/**
 * This span creates line over characters. Working good with lowercase. It doesn't work with size
 * altering spans.
 *
 * Created by Janci on 13.4.2018.
 */

public class LowerCaseOverlineSpan extends ReplacementSpan {

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        final float widthOfSpan = paint.measureText(text, start, end);
        final float ascent = paint.ascent() + y;
        final float lowerCaseAscent = ((y - ascent) * 0.25f) + ascent;
        final TextPaint textPaint = new TextPaint(paint);
        SpannableString subSequence = new SpannableString(text.subSequence(start, end));
        subSequence.removeSpan(this);
        final ForegroundColorSpan[] spans = subSequence.getSpans(0, subSequence.length(), ForegroundColorSpan.class);
        if (spans.length > 0) {
            paint.setColor(spans[0].getForegroundColor());
        }
        StaticLayout layout = new StaticLayout(subSequence, textPaint, (int) widthOfSpan * 2/*make enough space*/,
                Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        paint.setStrokeWidth(2);
        canvas.drawLine(x, lowerCaseAscent, x + widthOfSpan, lowerCaseAscent, paint);
        canvas.save();
        canvas.translate(x, top > 0 ? top : top + ((bottom - y) / 2f)); // hm this way it works
        layout.draw(canvas);
        canvas.restore();
    }
}
