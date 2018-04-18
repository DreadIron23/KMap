package sk.uniza.fri.janmokry.karnaughmap.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewPropertyAnimator;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number;
import sk.uniza.fri.janmokry.karnaughmap.fragment.LogicExpressionEditorFragment;
import sk.uniza.fri.janmokry.karnaughmap.helper.ImplicantEditor;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell;
import sk.uniza.fri.janmokry.karnaughmap.text.LowerCaseOverlineSpan;
import sk.uniza.fri.janmokry.karnaughmap.util.GraphicsUtil;

import static sk.uniza.fri.janmokry.karnaughmap.fragment.LogicExpressionEditorFragment.IMPLICANT_ANIMATION_DURATION;
import static sk.uniza.fri.janmokry.karnaughmap.fragment.LogicExpressionEditorFragment.SHOWCASE_IMPLICANT_ANIMATION_DURATION;

/**
 * View holding one segment of Logic Expression.
 *
 * Created by Janci on 16.4.2018.
 */

@SuppressLint("ViewConstructor")
public class ImplicantView extends android.support.v7.widget.AppCompatTextView {

    private ImplicantEditor mEditor;
    private boolean mIsSelected = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public ImplicantView(@NonNull Context context, @NonNull Number implicant,
                         LogicExpressionEditorFragment.OnImplicantClickedListener listener) {
        super(context);

        init(context, implicant, listener);
    }

    private void init(Context context, Number implicant,
                      LogicExpressionEditorFragment.OnImplicantClickedListener listener) {
        mEditor = new ImplicantEditor(implicant);

        updateText();
        setBackground(context.getResources().getDrawable(R.drawable.implicant_view_background));
        final int padding = GraphicsUtil.dpToPx(getResources(), 2);
        setPadding(padding * 2, padding, padding * 3, padding);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        setOnClickListener(view -> {
            listener.onImplicantClicked(this);
            if (mIsSelected) {
                unselect();
            } else {
                select();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(GraphicsUtil.dpToPx(getResources(), 2));
        }

        final FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setGravity(Gravity.CENTER_VERTICAL);
        layoutParams.setMargins(padding, padding, padding, padding * 2);
        setLayoutParams(layoutParams);
    }

    public ImplicantEditor getImplicantEditor() {
        return mEditor;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void unselect() {
        final ViewPropertyAnimator animator = animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(IMPLICANT_ANIMATION_DURATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator.translationZ(GraphicsUtil.dpToPx(getResources(), 0));
        }
        animator.start();
        mIsSelected = false;
    }

    public void select() {
        final ViewPropertyAnimator animator = animate()
                .scaleX(1.7f)
                .scaleY(1.7f)
                .setDuration(IMPLICANT_ANIMATION_DURATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator.translationZ(GraphicsUtil.dpToPx(getResources(), 6));
        }
        animator.start();
        mIsSelected = true;
    }

    public void animateShowcase(long delay) {
        mHandler.postDelayed(() -> {
            final ViewPropertyAnimator animate = animate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animate.translationZ(GraphicsUtil.dpToPx(getResources(), 6));
            }
            animate.scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(SHOWCASE_IMPLICANT_ANIMATION_DURATION)
                    .withEndAction(() -> {
                        final ViewPropertyAnimator animateBack = animate();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            animateBack.translationZ(GraphicsUtil.dpToPx(getResources(), 0));
                        }
                        animateBack.setDuration(SHOWCASE_IMPLICANT_ANIMATION_DURATION)
                                .scaleX(1)
                                .scaleY(1)
                                .start();
                    })
                    .start();
        }, delay);
    }

    private void updateText() {
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final ArrayList<Integer> numbers = mEditor.values();
        int bitCounter = 0;
        for (int bitIndex = numbers.size() - 1; bitIndex >= 0; bitIndex--) {
            if (bitCounter == 0) { // little hack for proper Paint setup for LowerCaseOverlineSpan
                builder.append(".");
                builder.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            final Integer bit = numbers.get(bitIndex);
            final int baseLength = builder.length();
            if (bit == KMapCell.VALUE_1 || bit == KMapCell.VALUE_0) {
                builder.append("x").append(String.valueOf(bitCounter));
                builder.setSpan(new RelativeSizeSpan(0.5f), baseLength + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(Typeface.BOLD, baseLength + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (bit == KMapCell.VALUE_0) {
                    builder.setSpan(new LowerCaseOverlineSpan(), baseLength, baseLength + "x".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            bitCounter++;
        }
        if (mEditor.isSetToZero()) {
            builder.append("0.");
            builder.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (mEditor.isSetToOne()) {
            builder.append("1.");
            builder.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(builder);
    }

    public void setXthBit(int xthIndex, Integer value) {
        mEditor.setXthBit(xthIndex, value);
        updateText();
    }

    public boolean isDuplicate(ImplicantView implicant) {
        final boolean implicantsEquals = mEditor.values().equals(implicant.getImplicantEditor().values());
        return implicantsEquals && (mEditor.isSetToZero() == implicant.getImplicantEditor().isSetToZero()) ||
                implicantsEquals && (mEditor.isSetToOne() == implicant.getImplicantEditor().isSetToOne());
    }

    public void setToOne() {
        mEditor.setToOne();
        updateText();
    }

    public void setToZero() {
        mEditor.setToZero();
        updateText();
    }
}
