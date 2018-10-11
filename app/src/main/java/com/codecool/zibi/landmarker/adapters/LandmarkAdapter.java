package com.codecool.zibi.landmarker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.Toast;
import com.codecool.zibi.landmarker.R;
import com.codecool.zibi.landmarker.model.Landmark;

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.LandmarkAdapterViewHolder>{
    private Landmark[] mLandmarkData;

    final private LandmarkAdapterOnClickHandler mClickHandler;
    final private LandmarkAdapterOnLongClickHandler mLongClickHandler;

    public interface LandmarkAdapterOnClickHandler{
        void onClick(Landmark landmark);
    }

    public interface LandmarkAdapterOnLongClickHandler{
        boolean onLongClick(Toast toast);
    }



    public LandmarkAdapter(LandmarkAdapterOnClickHandler clickHandler, LandmarkAdapterOnLongClickHandler longClickHandler){
        mClickHandler = clickHandler;
        mLongClickHandler = longClickHandler;
    }


    public class LandmarkAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public final TextView mLandmarkTextView;
        public final TextView mDistanceTextView;

        public LandmarkAdapterViewHolder(View view) {
            super(view);
            mLandmarkTextView = view.findViewById(R.id.tv_landmark_data);
            mDistanceTextView = view.findViewById(R.id.tv_landmark_distance);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Landmark oneSpecificLandmark = mLandmarkData[adapterPosition];
            mClickHandler.onClick(oneSpecificLandmark);
        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            Toast toast = Toast.makeText(v.getContext(), "Adapterpozi: " + adapterPosition, Toast.LENGTH_LONG);
            mLongClickHandler.onLongClick(toast);
            return true;
        }
    }


    @Override
    public LandmarkAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.landmark_list_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new LandmarkAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(LandmarkAdapterViewHolder forecastAdapterViewHolder, int position) {
        Landmark currentLandmark = mLandmarkData[position];
        String landmarkName = currentLandmark.getName();
        float landmarkDistance = currentLandmark.getLocation().distanceTo(currentLandmark.getRequestMadeFrom());
        Integer distanceInt = ((int) landmarkDistance);
        String distanceToDisplay = String.valueOf(distanceInt);
        forecastAdapterViewHolder.mLandmarkTextView.setText(landmarkName);
        forecastAdapterViewHolder.mDistanceTextView.setText(distanceToDisplay + " m");
    }


    @Override
    public int getItemCount() {
        if (mLandmarkData ==  null) return 0;
        return mLandmarkData.length;
    }


    public void setLandmarkData(Landmark[] landmarkData) {
        mLandmarkData = landmarkData;
        notifyDataSetChanged();
    }
}
