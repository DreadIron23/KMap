package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.view.KMapView;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

/**
 * View logic
 */
public class KarnaughMapsFragment extends ProjectBaseFragment<IEmptyView, EmptyViewModel> implements IEmptyView {

    private static final String ARG_KMAPS = "arg_kmaps";

    public static KarnaughMapsFragment newInstance() {
        return new KarnaughMapsFragment();
    }

    @BindView(R.id.kmap_container)
    protected LinearLayout mKMapContainer;

    private List<KMapView> mKMapViews = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            KMapView kMap = new KMapView(getContext());
            mKMapContainer.addView(kMap);
            mKMapViews.add(kMap);
        } else {
            ArrayList<String> serializedKMapViews = savedInstanceState.getStringArrayList(ARG_KMAPS);
            for (String kmapJson : serializedKMapViews) {
                final KMapView kMapView = KMapView.onLoad(getContext(), kmapJson);
                mKMapContainer.addView(kMapView);
                mKMapViews.add(kMapView);
            }
        }
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
        for (KMapView kmap : mKMapViews) {
            serializedKMapViews.add(kmap.onSave());
        }
        outState.putStringArrayList(ARG_KMAPS, serializedKMapViews);
    }
}
