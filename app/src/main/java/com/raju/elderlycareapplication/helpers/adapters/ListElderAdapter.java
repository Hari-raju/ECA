package com.raju.elderlycareapplication.helpers.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;
import com.raju.elderlycareapplication.helpers.utils.ElderListener;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;
import com.raju.elderlycareapplication.databinding.ConnectedElderHolderBinding;

import java.util.List;

public class ListElderAdapter extends RecyclerView.Adapter<ListElderAdapter.ElderHolder>{
    private final List<ConnectedElderModel> elders;
    private final ElderListener listener;
    public ListElderAdapter(List<ConnectedElderModel> elders,ElderListener elderListener) {
        this.elders = elders;
        this.listener=elderListener;
    }

    @NonNull
    @Override
    public ElderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConnectedElderHolderBinding elderHolderBinding = ConnectedElderHolderBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ElderHolder(elderHolderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ElderHolder holder, int position) {
        holder.setElderCard(elders.get(position));
    }

    @Override
    public int getItemCount() {
        return elders.size();
    }

    class ElderHolder extends RecyclerView.ViewHolder{
        ConnectedElderHolderBinding elderCard;
        ElderHolder(ConnectedElderHolderBinding card){
            super(card.getRoot());
            elderCard = card;
        }

        void setElderCard(ConnectedElderModel elder){
            elderCard.connectedElderName.setText(elder.getElderName());
            elderCard.connectedElderPhone.setText(elder.getElderPhone());
            elderCard.connectedElderProfile.setImageBitmap(EncoderDecoder.decodeImage(elder.getElderProfile()));
            elderCard.addMed.setOnClickListener(v->{
                listener.onElderClicked(elder,0);
            });
            elderCard.elderCard.setOnClickListener(v->{
                listener.onElderClicked(elder,1);
            });
            elderCard.addCheckup.setOnClickListener(v->{
                listener.onElderClicked(elder,2);
            });
        }
    }
}
