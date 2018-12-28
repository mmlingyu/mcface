package com.sxz.ai.face;

import android.graphics.Rect;

/**
 * Created by Administrator on 2018/8/8.
 */

public interface OauthBack {
    public void onOauthSucc(FaceInfo faceInfo, Rect rect);
    public void onOauthSucc(FaceInfo faceInfo);
    public void onTokenSucc(String token);
}
