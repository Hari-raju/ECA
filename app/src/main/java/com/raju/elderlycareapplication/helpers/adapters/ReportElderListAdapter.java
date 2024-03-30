package com.raju.elderlycareapplication.helpers.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raju.elderlycareapplication.databinding.EldersListLayoutBinding;
import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.utils.ElderReportListener;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;

import java.util.List;

public class ReportElderListAdapter extends RecyclerView.Adapter<ReportElderListAdapter.ReportElderHolder> {
    private final List<ConnectedElderModel> elders;
    private ElderReportListener listener;

    public ReportElderListAdapter(List<ConnectedElderModel> list,ElderReportListener listener){
        this.elders = list;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ReportElderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EldersListLayoutBinding layout = EldersListLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ReportElderHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportElderHolder holder, int position) {
        holder.setData(elders.get(position));
    }

    @Override
    public int getItemCount() {
        return elders.size();
    }

    class ReportElderHolder extends RecyclerView.ViewHolder{
        EldersListLayoutBinding elderCard;
        ReportElderHolder(EldersListLayoutBinding elderCard){
            super(elderCard.getRoot());
            this.elderCard = elderCard;
        }

        void setData(ConnectedElderModel model){
            elderCard.elderListElderName.setText(model.getElderName());
            elderCard.elderListElderPhone.setText(model.getElderPhone());
            elderCard.elderListElderProfile.setImageBitmap(EncoderDecoder.decodeImage(model.getElderProfile()));
            elderCard.elderCard.setOnClickListener(v->{
                Elder_Model elderModel = new Elder_Model();
                elderModel.setElderName(model.getElderName());
                elderModel.setElderPhone(model.getElderPhone());
                elderModel.setElderProfile(model.getElderProfile());
                listener.onClicked(elderModel);
            });
        }
    }
}
