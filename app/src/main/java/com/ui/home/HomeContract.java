package com.ui.home;

import com.base.BaseModel;
import com.base.BasePresenter;
import com.base.BaseView;
import com.data.entity._User;

/**
 * Created by baixiaokang on 16/4/22.
 */
public interface HomeContract {
    // TODO: 2016/10/21 Contract 合同


    // TODO: 2016/10/21 这里 model和view 是如何绑定的?
    interface Model extends BaseModel {
        String[] getTabs();
    }


    interface View extends BaseView {
        void showTabList(String[] mTabs);

        void initUserInfo(_User user);
    }


    // TODO: 2016/10/21 三个类里含有不同的方法,一般情况下,presenter是实现model接口中的方法
    abstract class Presenter extends BasePresenter<Model, View> {
        // TODO: 2016/10/21 获取数据
        public abstract void getTabList();

        public abstract void getUserInfo();
    }
}
