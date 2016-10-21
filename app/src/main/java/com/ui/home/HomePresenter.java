package com.ui.home;

import com.C;
import com.base.util.SpUtil;
import com.data.entity._User;

/**
 * Created by baixiaokang on 16/4/22.
 */
public class HomePresenter extends HomeContract.Presenter {

    @Override
    public void getTabList() {
        // TODO: 2016/10/21 这里是view与model的交互
        mView.showTabList(mModel.getTabs());
    }

    @Override
    public void getUserInfo() {
        _User user = SpUtil.getUser();  // TODO: 2016/10/21 这个user相当于model
        if (user != null)
            mView.initUserInfo(user);
    }

    // TODO: 2016/10/21 这里有个onStart方法,是方法执行的入口,在baseActivity调用
    @Override
    public void onStart() {
        // TODO: 2016/10/21 获取数据,通过线程总线连接
        getTabList();
        getUserInfo();
        mRxManager.on(C.EVENT_LOGIN, arg -> mView.initUserInfo((_User) arg));
    }
}
