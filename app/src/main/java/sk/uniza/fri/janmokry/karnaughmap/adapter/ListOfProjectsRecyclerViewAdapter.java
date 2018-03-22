package sk.uniza.fri.janmokry.karnaughmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;

public class ListOfProjectsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.base_layout)
        protected LinearLayout mBaseLayout;

        @BindView(R.id.project_name)
        protected TextView mName;

        @BindView(R.id.edit)
        protected ImageView mEdit;

        @BindView(R.id.bin)
        protected ImageView mBin;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnClickListener {

        void onProjectClicked(ProjectInfo projectInfo);

        void onEditClicked(ProjectInfo projectInfo);

        void onBinClicked(ProjectInfo projectInfo);

    }

    private Context mContext;

    private OnClickListener mOnClickListener;
    private List<ProjectInfo> mItems = new ArrayList<>();
    public ListOfProjectsRecyclerViewAdapter(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }


    public void add(ProjectInfo projectInfo) {
        mItems.add(projectInfo);
        notifyDataSetChanged();
    }

    public void delete(ProjectInfo projectInfo) {
        final int indexOfRemovedItem = mItems.indexOf(projectInfo);
        mItems.remove(projectInfo);
        notifyItemRemoved(indexOfRemovedItem);
    }

    public void addAll(Collection<ProjectInfo> projectNames) {
        mItems.addAll(projectNames);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_of_projects_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder castedHolder = (ViewHolder) holder;
        final ProjectInfo projectInfo = mItems.get(position);
        castedHolder.mName.setText(projectInfo.name);
        castedHolder.mBaseLayout.setOnClickListener(v -> mOnClickListener.onProjectClicked(projectInfo));
        castedHolder.mEdit.setOnClickListener(v -> mOnClickListener.onEditClicked(projectInfo));
        castedHolder.mBin.setOnClickListener(v -> mOnClickListener.onBinClicked(projectInfo));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
