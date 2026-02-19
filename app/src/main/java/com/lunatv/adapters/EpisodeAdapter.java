package com.lunatv.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lunatv.R;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private List<String> episodes;
    private OnEpisodeClickListener listener;
    private int selectedPosition = 0;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(int position);
    }

    public EpisodeAdapter(List<String> episodes, OnEpisodeClickListener listener) {
        this.episodes = episodes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.button.setText(String.valueOf(position + 1));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onEpisodeClick(position);
                }
            }
        });
        
        // 设置选中状态
        if (position == selectedPosition) {
            holder.button.setBackgroundResource(R.drawable.button_background);
        } else {
            holder.button.setBackgroundResource(R.drawable.edit_text_background);
        }
    }

    @Override
    public int getItemCount() {
        return episodes != null ? episodes.size() : 0;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.btn_episode);
        }
    }
}