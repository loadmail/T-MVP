package com.ui.login;

import com.C;
import com.base.util.SpUtil;

/**
 * Created by baixiaokang on 16/4/29.
 */
public class LoginPresenter extends LoginContract.Presenter {
    // TODO: 2016/10/21 presenter的本质没有变,只不过是把model的方法名换了一下,进行实现
    @Override
    public void login(String name, String pass) {
        mRxManager.add(mModel.login(name, pass).subscribe(user -> {
                    SpUtil.setUser(user);
                    mRxManager.post(C.EVENT_LOGIN, user);
                    mView.loginSuccess();
                }, e -> mView.showMsg("登录失败!")
        ));
    }

    @Override
    public void sign(String name, String pass) {
        mRxManager.add(mModel.sign(name, pass)
                .subscribe(res -> mView.signSuccess(),
                        e -> mView.showMsg("注册失败!")));
    }
}
