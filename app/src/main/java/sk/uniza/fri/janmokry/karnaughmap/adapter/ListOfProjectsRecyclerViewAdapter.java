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

        private OnClickListener mListener;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }
    public interface OnClickListener {

        void onProjectClicked(String projectName);

        void onEditClicked(String projectName);

        void onBinClicked(String projectName);

    }

    private Context mContext;

    private OnClickListener mOnClickListener;
    private List<String> mItems = new ArrayList<>();
    public ListOfProjectsRecyclerViewAdapter(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }


    public void add(String projectName) {
        mItems.add(projectName);
        notifyDataSetChanged();
    }

    public void delete(String projectName) {
        final int indexOfRemovedItem = mItems.indexOf(projectName);
        mItems.remove(projectName);
        notifyItemRemoved(indexOfRemovedItem);
    }

    public void addAll(Collection<String> projectNames) {
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
        castedHolder.mName.setText(mItems.get(position));
        castedHolder.mListener = mOnClickListener;
        castedHolder.mBaseLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClickListener.onProjectClicked(castedHolder.mName.getText().toString());
            }

        });
        castedHolder.mEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClickListener.onEditClicked(castedHolder.mName.getText().toString());
            }

        });
        castedHolder.mBin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClickListener.onBinClicked(castedHolder.mName.getText().toString());
            }

        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
