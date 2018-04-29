package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.IView;
import eu.inloop.viewmodel.base.ViewModelBaseFragment;
import sk.uniza.fri.janmokry.karnaughmap.activity.ProjectBaseActivity;

public abstract class ProjectBaseFragment<T extends IView, R extends AbstractViewModel<T>> extends ViewModelBaseFragment<T, R> {

    protected Unbinder mUnbinder;
    protected View mRootView;
    protected ProjectBaseActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutResId(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setModelView((T) this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ProjectBaseActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract void init();

    public void showToast(String text) {
        final Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        final TextView textView = toast.getView().findViewById(android.R.id.message);
        if (textView != null) textView.setGravity(Gravity.CENTER_HORIZONTAL);
        toast.show();
    }
}
