package com.sxz.ai.face;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HairVM {

    private BaiduOauthApi baiduOauthApi;
    private AliFaceApi aliFaceApi;
    private static String tokens= null;
    public HairVM(Context context){
        baiduOauthApi = new BaiduOauthApi(context);
        aliFaceApi = new AliFaceApi(context);
    }

    public void getToken(OauthBack oauthBack){
        try {
            baiduOauthApi.getAuth(oauthBack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFaceInfo(final File file, final OauthBack oauthBack, final Rect rect) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                if(TextUtils.isEmpty(tokens)) {
                    baiduOauthApi.getAuth(new OauthBack() {
                        @Override
                        public void onOauthSucc(FaceInfo faceInfo, Rect rect) {

                        }

                        @Override
                        public void onOauthSucc(FaceInfo faceInfo) {
                        }

                        @Override
                        public void onTokenSucc(String token) {

                            tokens = token;
                            e.onNext(token);
                            e.onComplete();
                        }
                    });
                }else{
                    e.onNext(tokens);
                    e.onComplete();
                }


            }
        });
        Consumer<String> onNextConsumer = new Consumer<String>() {
            @Override
            public void accept(String baidu_accessToken) {
                baiduOauthApi.uploadUserFace(file, baidu_accessToken, new OauthBack() {
                    @Override
                    public void onOauthSucc(FaceInfo faceInfo, Rect rect) {
                        oauthBack.onOauthSucc(faceInfo,rect);
                    }

                    @Override
                    public void onOauthSucc(FaceInfo faceInfo) {
                        oauthBack.onOauthSucc(faceInfo,rect);
                    }
                    @Override
                    public void onTokenSucc(String token) {
                    }
                },rect);
            }
        };
        observable.subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNextConsumer);


    }

    public void getFaceInfo(String imageBase64,String token, final OauthBack oauthBack) {
        baiduOauthApi.uploadUserFace(imageBase64, token, new OauthBack() {
            @Override
            public void onOauthSucc(FaceInfo faceInfo, Rect rect) {
                oauthBack.onOauthSucc(faceInfo);
            }

            @Override
            public void onOauthSucc(FaceInfo faceInfo) {
                oauthBack.onOauthSucc(faceInfo);
            }
            @Override
            public void onTokenSucc(String token) {
            }
        });
    }

    public void getFaceInfoV2(String imageBase64,String token, final OauthBack oauthBack) {
        baiduOauthApi.uploadUserFaceV2(imageBase64, token, new OauthBack() {
            @Override
            public void onOauthSucc(FaceInfo faceInfo, Rect rect) {
                oauthBack.onOauthSucc(faceInfo);
            }

            @Override
            public void onOauthSucc(FaceInfo faceInfo) {
                oauthBack.onOauthSucc(faceInfo);
            }
            @Override
            public void onTokenSucc(String token) {
            }
        });
    }

    public void getFaceInfoV3(String imageBase64,String token, final OauthBack oauthBack) {
        aliFaceApi.detectBase64(imageBase64, token, new OauthBack() {
            @Override
            public void onOauthSucc(FaceInfo faceInfo, Rect rect) {
                oauthBack.onOauthSucc(faceInfo);
            }

            @Override
            public void onOauthSucc(FaceInfo faceInfo) {
                oauthBack.onOauthSucc(faceInfo);
            }
            @Override
            public void onTokenSucc(String token) {
            }
        });
    }

    public void getFaceInfo(final File file, final OauthBack oauthBack) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                baiduOauthApi.getAuth(new OauthBack() {
                    @Override
                    public void onOauthSucc(FaceInfo faceInfo, Rect rect) {

                    }

                    @Override
                    public void onOauthSucc(FaceInfo faceInfo) {
                    }

                    @Override
                    public void onTokenSucc(String token) {
                        e.onNext(token);
                        e.onComplete();
                    }
                });


            }
        });
        Consumer<String> onNextConsumer = new Consumer<String>() {
            @Override
            public void accept(String baidu_accessToken) {
                baiduOauthApi.uploadUserFace(file, baidu_accessToken, new OauthBack() {
                    @Override
                    public void onOauthSucc(FaceInfo faceInfo, Rect rect) {
                        oauthBack.onOauthSucc(faceInfo);
                    }

                    @Override
                    public void onOauthSucc(FaceInfo faceInfo) {
                        oauthBack.onOauthSucc(faceInfo);
                        Log.d("BAIDU  succ ||", faceInfo.getResult().getFace_list()[0].getFace_shape().getType());
                    }
                    @Override
                    public void onTokenSucc(String token) {
                    }
                });
            }
        };
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextConsumer);


    }

}
