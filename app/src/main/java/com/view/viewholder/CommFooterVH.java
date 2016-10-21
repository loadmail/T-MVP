package com.view.viewholder;


import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.base.BaseViewHolder;
import com.ui.main.R;

import butterknife.Bind;


public class CommFooterVH extends BaseViewHolder<Object> {
    @Bind(R.id.progressbar)
    public ProgressBar progressbar;
    @Bind(R.id.tv_state)
    public TextView tv_state;
    public static final int LAYOUT_TYPE = R.layout.list_footer_view;

    public CommFooterVH(View view) {
        super(view);
    }

    @Override
    public int getType() {
        return LAYOUT_TYPE;
    }

    @Override
    // TODO: 2016/10/21 这个方法很优雅 底部文字说明与view的显示

    public void onBindViewHolder(View view, Object o) {
        boolean isHasMore = (null == o ? false : true);
        progressbar.setVisibility(isHasMore ? View.VISIBLE : View.GONE);
        tv_state.setText(isHasMore ? "正在加载" : "已经到底");
    }
}