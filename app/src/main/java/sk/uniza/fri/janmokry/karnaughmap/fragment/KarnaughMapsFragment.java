package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.activity.LogicExpressionEditorActivity;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.event.KMapVariableCountChangeEvent;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.view.KMapItem;
import sk.uniza.fri.janmokry.karnaughmap.view.KMapView;
import sk.uniza.fri.janmokry.karnaughmap.view.TwoDScrollView;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.KarnaughMapsViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IKarnaughMapsView;

/**
 * View logic
 */
public class KarnaughMapsFragment extends ProjectBaseFragment<IKarnaughMapsView, KarnaughMapsViewModel> implements IKarnaughMapsView {

    private static final int MAX_ALLOWED_KMAPS = 8;
    private static final String KMAP_BASE_TITLE = "y";
    private static final long SCROLLING_DURATION = 500L;

    public static KarnaughMapsFragment newInstance() {
        return new KarnaughMapsFragment();
    }

    public interface OnKMapConfigurationComputationTriggerListener {

        void onKMapConfigurationComputationTrigger(KMapCollection collection);
    }

    @BindView(R.id.scroll_view)
    protected TwoDScrollView mTwoDScrollView;

    @BindView(R.id.kmap_item_container)
    protected LinearLayout mKMapItemContainer;

    @BindView(R.id.add_kmap)
    protected FloatingActionButton mAddKMapButton;

    @BindView(R.id.no_data_message)
    protected TextView mNoDataMessage;

    private List<KMapItem> mKMapItems = new ArrayList<>();

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Observer<Solution> mSolutionObserver = solution -> {
        if (solution != null) {
            for (KMapItem kMapItem : mKMapItems) {
                final KMapView kMap = kMapItem.getKMap();
                if (kMap.getTitle().equals(solution.getTitle())) {
                    kMap.getKMapCollection().setSolution(solution);
                    kMap.invalidate();
                    kMapItem.onComputationDone(solution);
                }
            }
        }
    };

    private final OnKMapConfigurationComputationTriggerListener mConfigurationComputationTriggerListener = collection -> {
        getViewModel().onKMapConfigurationComputationTrigger(collection);
        for (KMapItem item : mKMapItems) {
            if (item.getKMap().getKMapCollection() == collection) {
                item.onComputationKickOf();
                break;
            }
        }
    };

    private KMapItem.OnBinClickedListener mOnBinClickedListener = clickedView -> {
        new AlertDialog.Builder(getContext(), R.style.DialogStyle)
                .setTitle(R.string.project_screen_delete_dialog_title)
                .setMessage(R.string.project_screen_delete_dialog_message)
                .setPositiveButton(R.string.project_screen_delete_dialog_delete, (dialog, whichButton) -> {
                    removeKMapItem(clickedView);
                    renameKMapsAccordingToOrder(clickedView);
                })
                .setNegativeButton(R.string.project_screen_delete_dialog_discard, null)
                .show();
    };

    private KMapItem.OnVariableCountChangeListener mOnVariableCountChangeListener = () -> {
        SL.get(EventBusService.class).post(new KMapVariableCountChangeEvent());
    };

    private KMapItem.OnLogicExpressionClickedListener mOnLogicExpressionClickedListener =
            (solution, numberOfVariables) ->
                    getActivity().startActivity(LogicExpressionEditorActivity
                            .newIntent(getContext(), solution, numberOfVariables));

    private void removeKMapItem(KMapItem itemToRemove) {
        mKMapItemContainer.removeView(itemToRemove);
        mKMapItems.remove(itemToRemove);
        itemToRemove.onRemoval();

        setAddKMapButtonVisibility();
        setNoDataMessageVisibility();

        showToast(getResources().getString(
                R.string.project_screen_toast_deleted_kmap,
                itemToRemove.getKMap().getKMapCollection().getTitle()));

        getViewModel().onKMapRemoval(itemToRemove.getKMap().getKMapCollection());
    }

    /** This renaming is quite dumb but this is what we have for now */
    private void renameKMapsAccordingToOrder(KMapItem removedItem) {
        int counter = 0;
        final List<String> oldTitles = new ArrayList<>();
        final List<String> newTitles = new ArrayList<>();
        for (KMapItem kMapItem : mKMapItems) {
            final String oldTitle = kMapItem.getKMap().getTitle();
            final String newTitle = KMAP_BASE_TITLE + counter++;
            kMapItem.setTitle(newTitle);
            kMapItem.invalidate();

            oldTitles.add(oldTitle);
            newTitles.add(newTitle);
        }

        getViewModel().onKMapsTitleChange(oldTitles, newTitles, removedItem.getKMap().getKMapCollection());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_karnaugh_maps;
    }

    @Override
    protected void init() {
        setHasOptionsMenu(true);
        getProjectViewModel().mTruthTableCollection.observe(this, truthTableCollection -> {

            if (truthTableCollection != null) {
                mKMapItemContainer.removeAllViews();
                mKMapItems.clear();
                for (KMapCollection kMapCollection : truthTableCollection.getKMapCollections()) {
                    final KMapItem itemView = new KMapItem(getContext());
                    itemView.setOnBinClickedListener(mOnBinClickedListener);
                    itemView.setOnVariableCountChangeListener(mOnVariableCountChangeListener);
                    itemView.setOnLogicExpressionClickedListener(mOnLogicExpressionClickedListener);
                    final KMapView kMap = KMapView.onLoad(getContext(), kMapCollection,
                            mConfigurationComputationTriggerListener);
                    itemView.addKMap(kMap);

                    mKMapItemContainer.addView(itemView);
                    mKMapItems.add(itemView);

                    getViewModel().registerKMapForConfigurationCalculations(kMap.getTitle(), this, mSolutionObserver);
                }

                setNoDataMessageVisibility();
                setAddKMapButtonVisibility();

                getViewModel().onTruthTableCollectionUpdate(truthTableCollection);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        // saving state
        ArrayList<KMapCollection> kKMapCollections = new ArrayList<>();
        for (KMapItem kMapItem : mKMapItems) {
            kKMapCollections.add(kMapItem.getKMap().onSave());
        }

        getProjectViewModel().onKarnaughMapsSave(kKMapCollections);
    }

    @OnClick(R.id.add_kmap)
    public void onAddKMapButtonClicked() {
        final String kMapTitle = KMAP_BASE_TITLE + mKMapItems.size();

        KMapItem itemView = new KMapItem(getContext());
        itemView.setOnBinClickedListener(mOnBinClickedListener);
        itemView.setOnVariableCountChangeListener(mOnVariableCountChangeListener);
        itemView.setOnLogicExpressionClickedListener(mOnLogicExpressionClickedListener);
        KMapView kMap = new KMapView(getContext(), mConfigurationComputationTriggerListener);
        kMap.setTitle(kMapTitle);
        itemView.addKMap(kMap);
        getViewModel().registerKMapForConfigurationCalculations(kMapTitle, this, mSolutionObserver);

        mKMapItemContainer.addView(itemView);
        mKMapItems.add(itemView);

        mHandler.post(() -> { // scroll to newly created map
            ObjectAnimator.ofInt(mTwoDScrollView, "scrollY", (int) itemView.getY())
                    .setDuration(SCROLLING_DURATION).start();
            ObjectAnimator.ofInt(mTwoDScrollView, "scrollX", (int) itemView.getX())
                    .setDuration(SCROLLING_DURATION).start();
        });

        setAddKMapButtonVisibility();
        setNoDataMessageVisibility();

        getViewModel().onKMapAddition(kMap.getKMapCollection());
    }

    private void setAddKMapButtonVisibility() {
        mAddKMapButton.setVisibility(mKMapItems.size() == MAX_ALLOWED_KMAPS ? View.GONE : View.VISIBLE);
    }

    private void setNoDataMessageVisibility() {
        mNoDataMessage.setVisibility(mKMapItems.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public ProjectViewModel getProjectViewModel() {
        return ((ProjectFragment) getParentFragment()).getViewModel();
    }

    @Override
    public void applyNewValuesToKMap(@NonNull Solution editedSolution) {
        for (KMapItem kMapItem : mKMapItems) {
            final KMapView kMap = kMapItem.getKMap();
            if (kMap.getTitle().equals(editedSolution.getTitle())) {
                kMap.getKMapCollection().applyChangesFromEditedSolution(editedSolution);
                kMap.invalidate();
                // compute configurations for new values
                getViewModel().onKMapConfigurationComputationTrigger(kMap.getKMapCollection());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                getViewModel().onActionExportProject();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
