package com.wzj.goodweather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wzj.goodweather.bean.LifestyleResponse;
import com.wzj.goodweather.databinding.ItemLifestyleRvBinding;

import java.util.ArrayList;
import java.util.List;

public class LifestyleAdapter extends RecyclerView.Adapter<LifestyleAdapter.ViewHolder> {
    private final List<LifestyleResponse.DailyBean> dailyBeans;

    public LifestyleAdapter(List<LifestyleResponse.DailyBean> dailyBeans) {
        this.dailyBeans = dailyBeans;
    }

    @NonNull
    @Override
    public LifestyleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLifestyleRvBinding binding = ItemLifestyleRvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LifestyleAdapter.ViewHolder holder, int position) {
        LifestyleResponse.DailyBean dailyBean = dailyBeans.get(position);
        holder.binding.tvLifetstyle.setText(dailyBean.getName() + ": " + dailyBean.getText());
    }

    @Override
    public int getItemCount() {
        return dailyBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemLifestyleRvBinding binding;

        public ViewHolder(@NonNull ItemLifestyleRvBinding itemLifestyleRvBinding) {
            super(itemLifestyleRvBinding.getRoot());
            binding = itemLifestyleRvBinding;
        }
    }
}
