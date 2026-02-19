package com.lunatv.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunatv.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private List<String> history;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClick(String keyword);
        void onHistoryDelete(String keyword);
    }

    public SearchHistoryAdapter(List<String> history, OnHistoryClickListener listener) {
        this.history = history;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String keyword = history.get(position);
        holder.tvHistory.setText(keyword);
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onHistoryClick(keyword);
                }
            }
        });
        
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onHistoryDelete(keyword);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return history != null ? history.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistory;
        ImageView ivArrow;

        ViewHolder(View itemView) {
            super(itemView);
            tvHistory = itemView.findViewById(R.id.tv_history);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
        }
    }
}