package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.view.KMapItem;
import sk.uniza.fri.janmokry.karnaughmap.view.KMapView;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.KarnaughMapsViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IKarnaughMapsView;

/**
 * View logic
 */
public class KarnaughMapsFragment extends ProjectBaseFragment<IKarnaughMapsView, KarnaughMapsViewModel> implements IKarnaughMapsView {

    private static final int MAX_ALLOWED_KMAPS = 8;
    private static final String KMAP_BASE_TITLE = "y";

    public static KarnaughMapsFragment newInstance() {
        return new KarnaughMapsFragment();
    }

    @BindView(R.id.kmap_item_container)
    protected LinearLayout mKMapItemContainer;

    @BindView(R.id.add_kmap)
    protected FloatingActionButton mAddKMapButton;

    @BindView(R.id.no_data_message)
    protected TextView mNoDataMessage;

    private List<KMapItem> mKMapItems = new ArrayList<>();

    private KMapItem.OnBinClickedListener onBinClickedListener = clickedView -> {
        new AlertDialog.Builder(getContext(), R.style.DialogStyle)
                .setTitle(R.string.project_screen_delete_dialog_title)
                .setMessage(R.string.project_screen_delete_dialog_message)
                .setPositiveButton(R.string.project_screen_delete_dialog_delete, (dialog, whichButton) ->
                        removeKMapItem(clickedView)
                )
                .setNegativeButton(R.string.project_screen_delete_dialog_discard, null)
                .show();
    };

    private void removeKMapItem(KMapItem itemToRemove) {
        mKMapItemContainer.removeView(itemToRemove);
        mKMapItems.remove(itemToRemove);

        setAddKMapButtonVisibility();
        setNoDataMessageVisibility();

        renameKMapsAccordingToOrder();
        showToast(getResources().getString(
                R.string.project_screen_toast_deleted_kmap,
                itemToRemove.getKMap().getKMapCollection().getTitle()));

        getViewModel().onKMapRemoval(itemToRemove.getKMap().getKMapCollection());
    }

    /** This renaming is quite dumb but this is what we have for now */
    private void renameKMapsAccordingToOrder() {
        int counter = 0;
        for (KMapItem kMapItem : mKMapItems) {
            kMapItem.setTitle(KMAP_BASE_TITLE + counter++);
            kMapItem.invalidate();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getProjectViewModel().mTruthTableCollection.observe(this, truthTableCollection -> {

            if (truthTableCollection != null) {
                mKMapItemContainer.removeAllViews();
                for (KMapCollection kMapCollection : truthTableCollection.getKMapCollections()) {
                    KMapItem itemView = new KMapItem(getContext());
                    itemView.setOnBinClickedListener(onBinClickedListener);
                    final KMapView kMap = KMapView.onLoad(getContext(), kMapCollection);
                    itemView.addKMap(kMap);

                    mKMapItemContainer.addView(itemView);
                    mKMapItems.add(itemView);
                }

                setNoDataMessageVisibility();
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_karnaugh_maps;
    }

    @Override
    protected void init() {
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
        itemView.setOnBinClickedListener(onBinClickedListener);
        KMapView kMap = new KMapView(getContext());
        kMap.setTitle(kMapTitle);
        itemView.addKMap(kMap);

        mKMapItemContainer.addView(itemView);
        mKMapItems.add(itemView);

        setAddKMapButtonVisibility();
        setNoDataMessageVisibility();

        getViewModel().onKMapAddition(kMap.getKMapCollection());
    }

    private void setAddKMapButtonVisibility() {
        if (mKMapItems.size() == MAX_ALLOWED_KMAPS) {
            mAddKMapButton.setVisibility(View.GONE);
        } else {
            mAddKMapButton.setVisibility(View.VISIBLE);
        }
    }

    private void setNoDataMessageVisibility() {
        if (mKMapItems.isEmpty()) {
            mNoDataMessage.setVisibility(View.VISIBLE);
        } else {
            mNoDataMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public ProjectViewModel getProjectViewModel() {
        return ((ProjectFragment) getParentFragment()).getViewModel();
    }
}
