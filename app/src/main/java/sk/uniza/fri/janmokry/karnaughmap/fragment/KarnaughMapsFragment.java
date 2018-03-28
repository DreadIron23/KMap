package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import sk.uniza.fri.janmokry.karnaughmap.view.KMapItem;
import sk.uniza.fri.janmokry.karnaughmap.view.KMapView;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

/**
 * View logic
 */
public class KarnaughMapsFragment extends ProjectBaseFragment<IEmptyView, EmptyViewModel> implements IEmptyView {

    private static final String ARG_KMAPS = "arg_kmaps";
    private static final int MAX_ALLOWED_KMAPS = 8;

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
                        removeKMapItem(clickedView) // TODO: this need to be delegated to Model for further processing (TruthTable syncing ect)
                )
                .setNegativeButton(R.string.project_screen_delete_dialog_discard, null)
                .show();
    };

    private void removeKMapItem(KMapItem itemToRemove) {
        mKMapItemContainer.removeView(itemToRemove);
        mKMapItems.remove(itemToRemove);

        setAddKMapButtonVisibility();
        setNoDataMessageVisibility();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {

        } else {

            ArrayList<String> serializedKMapViews = savedInstanceState.getStringArrayList(ARG_KMAPS);
            for (String kmapJson : serializedKMapViews) {
                KMapItem itemView = new KMapItem(getContext());
                itemView.setOnBinClickedListener(onBinClickedListener);
                final KMapView kMap = KMapView.onLoad(getContext(), kmapJson);
                itemView.addKMap(kMap);

                mKMapItemContainer.addView(itemView);
                mKMapItems.add(itemView);
            }
        }

        setNoDataMessageVisibility();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_karnaugh_maps;
    }

    @Override
    protected void init() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> serializedKMapViews = new ArrayList<>();
        for (KMapItem kMapItem : mKMapItems) {
            serializedKMapViews.add(kMapItem.getKMap().onSave());
        }
        outState.putStringArrayList(ARG_KMAPS, serializedKMapViews);
    }

    @OnClick(R.id.add_kmap)
    public void onAddKMapButtonClicked() {
        // TODO: this need to be delegated to Model for further processing (TruthTable syncing ect)
        KMapItem itemView = new KMapItem(getContext());
        itemView.setOnBinClickedListener(onBinClickedListener);
        KMapView kMap = new KMapView(getContext());
        itemView.addKMap(kMap);

        mKMapItemContainer.addView(itemView);
        mKMapItems.add(itemView);

        setAddKMapButtonVisibility();
        setNoDataMessageVisibility();
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
}
