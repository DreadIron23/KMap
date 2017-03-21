package sk.uniza.fri.janmokry.karnaughmap.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.ButterKnife;
import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.IView;
import eu.inloop.viewmodel.base.ViewModelBaseActivity;

public abstract class ProjectBaseActivity<T extends IView, R extends AbstractViewModel<T>> extends ViewModelBaseActivity<T, R> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        init(savedInstanceState);
        setModelView((T)this);
    }

    protected abstract int getLayoutResId();

    protected abstract void init(Bundle savedInstanceState);

    public void shortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
