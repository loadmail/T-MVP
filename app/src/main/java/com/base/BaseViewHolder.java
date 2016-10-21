package com.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public Context mContext;

    public BaseViewHolder(View v) {
        super(v);
        mContext = v.getContext();
        if (((ViewGroup) v).getChildCount() > 0)
            ButterKnife.bind(this, v);
    }

    /**
     * 调用:
     *  int mFooterViewType = ((BaseViewHolder) (cla.getConstructor(View.class)
     .newInstance(new LinearLayout(context)))).getType();

     *
     * ViewHolder的Type，同时也是它的LayoutId
     *
     * @return
     */
    // TODO: 2016/10/21  方法很奇妙 在子类中有实现,就是所用布局的id
    public abstract int getType();

    /**
     * 绑定ViewHolder
     *
     * @return
     */
    public abstract void onBindViewHolder(View view, T obj);

}
