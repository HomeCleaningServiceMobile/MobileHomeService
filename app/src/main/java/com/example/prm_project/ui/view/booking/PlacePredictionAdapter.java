package com.example.prm_project.ui.view.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

public class PlacePredictionAdapter extends RecyclerView.Adapter<PlacePredictionAdapter.PredictionViewHolder> {

    private List<AutocompletePrediction> predictions = new ArrayList<>();
    private OnPredictionClickListener listener;

    public interface OnPredictionClickListener {
        void onPredictionClick(AutocompletePrediction prediction);
    }

    public PlacePredictionAdapter(OnPredictionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new PredictionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        AutocompletePrediction prediction = predictions.get(position);
        holder.bind(prediction);
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public void updatePredictions(List<AutocompletePrediction> newPredictions) {
        predictions.clear();
        if (newPredictions != null) {
            predictions.addAll(newPredictions);
        }
        notifyDataSetChanged();
    }

    class PredictionViewHolder extends RecyclerView.ViewHolder {
        private TextView primaryText;
        private TextView secondaryText;

        PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            primaryText = itemView.findViewById(android.R.id.text1);
            secondaryText = itemView.findViewById(android.R.id.text2);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPredictionClick(predictions.get(position));
                }
            });
        }

        void bind(AutocompletePrediction prediction) {
            primaryText.setText(prediction.getPrimaryText(null));
            secondaryText.setText(prediction.getSecondaryText(null));
        }
    }
} 