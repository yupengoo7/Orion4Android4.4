package com.lunatv.adapters;

import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunatv.R;
import com.lunatv.models.Video;
import com.squareup.picasso.Picasso;

public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";
    private static final int CARD_WIDTH = 300;
    private static final int CARD_HEIGHT = 300;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        android.support.v7.widget.CardView cardView = new android.support.v7.widget.CardView(parent.getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(CARD_WIDTH, CARD_HEIGHT));
        cardView.setCardBackgroundColor(parent.getContext().getResources().getColor(R.color.background_card));
        cardView.setRadius(8);
        cardView.setCardElevation(8);
        
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        if (item == null) return;
        
        Video video = (Video) item;
        android.support.v7.widget.CardView cardView = (android.support.v7.widget.CardView) viewHolder.view;
        
        // 清除旧视图
        cardView.removeAllViews();
        
        // 创建卡片内容
        FrameLayout frameLayout = new FrameLayout(cardView.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        
        // 海报图片
        ImageView imageView = new ImageView(cardView.getContext());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        // 加载图片
        String posterUrl = video.getPoster();
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Picasso.get()
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .resize(CARD_WIDTH, CARD_HEIGHT)
                .centerCrop()
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher);
        }
        
        // 渐变遮罩
        View overlay = new View(cardView.getContext());
        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            80);
        overlayParams.gravity = Gravity.BOTTOM;
        overlay.setLayoutParams(overlayParams);
        overlay.setBackgroundResource(R.drawable.gradient_overlay);
        
        // 标题
        TextView titleView = new TextView(cardView.getContext());
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
        titleParams.gravity = Gravity.BOTTOM;
        titleParams.setMargins(12, 0, 12, 12);
        titleView.setLayoutParams(titleParams);
        
        String title = video.getTitle();
        titleView.setText(title != null ? title : "");
        titleView.setTextColor(cardView.getContext().getResources().getColor(R.color.text_primary));
        titleView.setTextSize(14);
        titleView.setMaxLines(2);
        titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        
        frameLayout.addView(imageView);
        frameLayout.addView(overlay);
        frameLayout.addView(titleView);
        
        cardView.addView(frameLayout);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        // 清理资源
    }
}
