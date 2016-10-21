package com.view.layout;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.C;
import com.base.BaseViewHolder;
import com.base.RxManager;
import com.base.util.LogUtil;
import com.data.Data;
import com.data.Repository;
import com.ui.main.R;
import com.view.viewholder.CommFooterVH;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/** todo 这个TRecyclerView可以研究一下
 *
 * @author Administrator
 */
public class TRecyclerView<T extends Repository> extends LinearLayout {
    private T model;

    @Bind(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.ll_emptyview)
    LinearLayout ll_emptyview;
    private LinearLayoutManager mLayoutManager;
    private Context context;
    public CoreAdapter mCommAdapter = new CoreAdapter();
    private int begin = 0;
    // TODO: 2016/10/21 有头 可刷新 空数据
    private boolean isRefreshable = true, isHasHeadView = false, isEmpty = false;

    public RxManager mRxManager = new RxManager();
    private Map<String, String> param = new HashMap<>();

    public TRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public TRecyclerView(Context context, AttributeSet att) {
        super(context, att);
        init(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRxManager.clear();
    }

    public void init(Context context) {
        this.context = context;
        // TODO: 2016/10/21 这个布局很有意思  SwipeRefreshLayout嵌套recyclerView 还有一个emptyView 都是覆盖页面
        View layout = LayoutInflater.from(context).inflate(
                R.layout.layout_list_recyclerview, null);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        addView(layout);  // TODO: 2016/10/21 覆盖
        ButterKnife.bind(this, layout);
        initView(context);
    }

    private void initView(Context context) {

        // TODO: 2016/10/21 swiperefresh setting
        swiperefresh.setColorSchemeResources(android.R.color.holo_blue_bright);
        swiperefresh.setEnabled(isRefreshable);
        // TODO: 刷新的开始和停止时  swiperefresh.setRefreshing(true);控制的一个动画,具体的数据操作是在fetch()方法中实现的
        swiperefresh.setOnRefreshListener(() -> reFetch());


        recyclerview.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(mCommAdapter);
        // TODO: 2016/10/21 添加滚动监听
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            protected int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerview.getAdapter() != null
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == recyclerview.getAdapter()
                        .getItemCount() && mCommAdapter.isHasMore)
                    fetch();  // TODO: 2016/10/21 刷新数据
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int arg0, int arg1) {
                super.onScrolled(recyclerView, arg0, arg1);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        mRxManager.on(C.EVENT_DEL_ITEM, (arg0) -> mCommAdapter.removeItem((Integer) arg0));
        mRxManager.on(C.EVENT_UPDATE_ITEM, (arg0) -> {
                    if (model.getClass().getSimpleName().equals(((UpDateData) arg0).oj.getClass().getSimpleName())) {
                        mCommAdapter.upDateItem(((UpDateData) arg0).i, ((UpDateData) arg0).oj);
                    }
                }
        );
        ll_emptyview.setOnClickListener((view -> {
            isEmpty = false;
            ll_emptyview.setVisibility(View.GONE);
            swiperefresh.setVisibility(View.VISIBLE);
            reFetch();
        }));


    }

    public CoreAdapter getAdapter() {
        return mCommAdapter;
    }

    public void setRefreshing(boolean i) {
        swiperefresh.setRefreshing(i);
    }

    public TRecyclerView setIsRefreshable(boolean i) {
        isRefreshable = i;
        swiperefresh.setEnabled(i);
        return this;
    }

    public TRecyclerView setHeadView(Class<? extends BaseViewHolder> cla) {
        if (cla == null) {
            isHasHeadView = false;
            this.mCommAdapter.setHeadViewType(0, cla, null);
        } else
            try {
                Object obj = ((Activity) context).getIntent().getSerializableExtra(C.HEAD_DATA);

                // TODO: 2016/10/21 获取type这个方法不懂 
                int mHeadViewType = ((BaseViewHolder) (cla.getConstructor(View.class)
                        .newInstance(new LinearLayout(context)))).getType();
                this.mCommAdapter.setHeadViewType(mHeadViewType, cla, obj);
                isHasHeadView = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return this;
    }

    public TRecyclerView setFooterView(Class<? extends BaseViewHolder> cla) {
        this.begin = 0;
        try {
            int mFooterViewType = ((BaseViewHolder) (cla.getConstructor(View.class)
                    .newInstance(new LinearLayout(context)))).getType();
            this.mCommAdapter.setFooterViewType(mFooterViewType, cla);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void setEmpty() {
        if (!isHasHeadView && !isEmpty) {
            isEmpty = true;
            ll_emptyview.setVisibility(View.VISIBLE);
            swiperefresh.setVisibility(View.GONE);
        }
    }

    public TRecyclerView setView(Class<? extends BaseViewHolder<T>> cla) {

// TODO: 2016/10/21 根据泛型,获取实例
        try {
            BaseViewHolder mIVH = ((BaseViewHolder) (cla.getConstructor(View.class)
                    .newInstance(new LinearLayout(context))));
            int mType = mIVH.getType();
            this.model = ((Class<T>) ((ParameterizedType) (cla
                    .getGenericSuperclass())).getActualTypeArguments()[0])
                    .newInstance();// 根据类的泛型类型获得model的实例
            this.mCommAdapter.setViewType(mType, cla);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public TRecyclerView setParam(String key, String value) {
        this.param.put(key, value);
        return this;
    }

    public TRecyclerView setData(List<T> datas) {
        if (isEmpty) {
            ll_emptyview.setVisibility(View.GONE);
            swiperefresh.setVisibility(View.VISIBLE);
        }
        mCommAdapter.setBeans(datas, 1);
        return this;
    }

    public void reFetch() {
        this.begin = 0;   // TODO: 2016/10/21 对照
        swiperefresh.setRefreshing(true);
        fetch();
    }

    public void fetch() {
        begin++;   // TODO: 2016/10/21 这个方法牛逼 把刷新和加载更多漂亮的分开了
        if (isEmpty) {
            ll_emptyview.setVisibility(View.GONE);
            swiperefresh.setVisibility(View.VISIBLE);
        }
        if (model == null) {
            Log.e("model", "null");
            return;
        }
        model.param = param;
        /* todo 用户列表的封装
        * 分页加载的封装操作：mTRecyclerView.setItemView(ItemView.class);
        * 不需要在当前页面写网络请求，不需要写下拉刷新和分页加载更多的回调，不需要写任何Adapter，只需要这一句即可。
        * 因为用泛型在TRecyclerView里面写过抽象层的操作，将监听和数据的获取封装成通用模板，从此可以一劳永逸了。
        * */
        mRxManager.add(model.getPageAt(begin)
                .subscribe(
                        new Action1<Data>() {
                            @Override
                            public void call(Data subjects) {
                                swiperefresh.setRefreshing(false);
                                List<T> mList = new ArrayList<T>();// TODO: 2016/10/21 这里不太理解,每次取出的是固定条目的数据,什么时候把数据都加上了呢?
                                for (Object o : subjects.results) {
                                    T d = (T) model.clone(); // TODO: 2016/10/21 这句什么意思?
                                    d.data = o;
                                    mList.add(d);  // TODO: 2016/10/21 list添加数据
                                }
                                mCommAdapter.setBeans(mList, begin);// TODO: 2016/10/21 这个很好的管理了数据和页数的关系

                                if (begin == 1 && (subjects.results == null || subjects.results.size() == 0))
                                    setEmpty(); // TODO: 2016/10/21 显示 加载完成
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                e.printStackTrace();
                                setEmpty();
                            }
                        }
                ));
    }


    public class UpDateData {
        public int i;
        public T oj;

        public UpDateData(int i, T oj) {
            this.i = i;
            this.oj = oj;
        }
    }

    public class CoreAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        protected List<T> mItemList = new ArrayList<>();
        public boolean isHasMore = true;
        public int viewtype, isHasFooter = 1, isHasHader = 0, mHeadViewType;
        public Object mHeadData;
        // TODO: 2016/10/21 厉害了我的哥 三viewHolder合一
        public Class<? extends BaseViewHolder> mItemViewClass, mHeadViewClass, mFooterViewClass = CommFooterVH.class;
        public int mFooterViewType = CommFooterVH.LAYOUT_TYPE;

        public void setViewType(int i, Class<? extends BaseViewHolder> cla) {
            this.isHasMore = true;
            this.viewtype = i;
            this.mItemList = new ArrayList<>();
            this.mItemViewClass = cla;
            notifyDataSetChanged();
        }

        public void setHeadViewType(int i, Class<? extends BaseViewHolder> cla, Object data) {
            if (cla == null) {
                this.isHasHader = 0;
            } else {
                this.isHasHader = 1;
                this.mHeadViewType = i;
                this.mHeadViewClass = cla;
                this.mHeadData = data;
            }
        }

        public void setHeadViewData(Object data) {
            this.mHeadData = data;
        }

        public void setFooterViewType(int i, Class<? extends BaseViewHolder> cla) {
            this.mFooterViewType = i;
            this.mFooterViewClass = cla;
            this.mItemList = new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return isHasHader == 1 ? (position == 0 ? mHeadViewType
                    : (position + 1 == getItemCount() ? mFooterViewType : viewtype))
                    : (position + 1 == getItemCount() ? mFooterViewType : viewtype);
        }

        @Override
        public int getItemCount() {
            return mItemList.size() + isHasFooter + isHasHader;
        }

        public void setBeans(List<T> datas, int begin) {
            if (datas == null) datas = new ArrayList<>();
            this.isHasMore = datas.size() >= C.PAGE_COUNT;
            if (begin > 1) {
                this.mItemList.addAll(datas);
            } else {
                this.mItemList = datas;
            }
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                boolean isFoot = viewType == mFooterViewType;
                return (RecyclerView.ViewHolder) (viewType == mHeadViewType ? mHeadViewClass
                        .getConstructor(View.class).newInstance(
                                LayoutInflater.from(parent.getContext()).inflate(
                                        mHeadViewType, parent, false))
                        : (RecyclerView.ViewHolder) (isFoot ? mFooterViewClass : mItemViewClass)
                        .getConstructor(View.class).newInstance(
                                LayoutInflater.from(parent.getContext())
                                        .inflate(
                                                isFoot ? mFooterViewType
                                                        : viewtype, parent,
                                                false)));
            } catch (Exception e) {
                LogUtil.d("ViewHolderException", "onCreateViewHolder十有八九是xml写错了,哈哈");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((BaseViewHolder) holder).onBindViewHolder(holder.itemView,
                    position + 1 == getItemCount() ? (isHasMore ? new Object()
                            : null) : isHasHader == 1 && position == 0 ? mHeadData
                            : mItemList.get(position - isHasHader));
        }

        public void removeItem(int position) {
            mItemList.remove(position);
            notifyItemRemoved(position);
            if (mItemList.size() == 0) reFetch();
        }

        public void upDateItem(int position, T item) {
            mItemList.remove(position);
            mItemList.add(position, item);
            notifyItemChanged(position);
        }
    }

}