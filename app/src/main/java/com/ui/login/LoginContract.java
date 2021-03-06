package com.ui.login;

import com.base.BaseModel;
import com.base.BasePresenter;
import com.base.BaseView;
import com.data.CreatedResult;
import com.data.entity._User;

import rx.Observable;

/**
 * Created by baixiaokang on 16/4/29.
 */
public interface LoginContract {
    // TODO: 2016/10/21 contract 合同 契约 con全部  tract 大片土地  束

    // TODO: 2016/10/21 model 只做数据操作
    // TODO: 2016/10/21  用 Observable<T> 获得被观察者对象,进一步用rxjava的链式调用后面的方法操作
    //todo 参照 LoginPresenter
    interface Model extends BaseModel {
        Observable<_User> login(String name, String pass);
        Observable<CreatedResult> sign(String name, String pass);
    }

    interface View extends BaseView {
        void loginSuccess();
        void signSuccess();
        void showMsg(String  msg);
    }

    abstract class Presenter extends BasePresenter<Model, View> {
        public abstract void login(String name, String pass);
        public abstract void sign(String name, String pass);
        @Override
        public void onStart() {}
    }
}
