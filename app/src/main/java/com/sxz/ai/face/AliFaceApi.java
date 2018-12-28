package com.sxz.ai.face;

import android.content.Context;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;


/**
 * Created by Administrator on 2016/8/19.
 */
public class AliFaceApi extends BaseApi {
    public static final MediaType urlencode = MediaType.parse("application/x-www-form-urlencoded;");
    private WeakReference<Context> weakcontext;
    // 官网获取的 API Key 更新为你注册的
    final String clientId = "LTAIRp9oYCCZGgce";
    // 官网获取的 Secret Key 更新为你注册的
    final String clientSecret = "8jAGLn8bFOyfjnDXXLy8ACFsBBKINA";
    public AliFaceApi(Context context){
        this.weakcontext = new WeakReference<Context>(context);
    }



    public  void detectBase64(String imageBase64, String accessToken, OauthBack oauthBack) {
        String url = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/attribute";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("content", imageBase64);
            map.put("type", 1);

            String param = GsonUtils.toJson(map);
                AliFaceInfo alifaceInfo = new Gson().fromJson(AESDecode.sendPost(url, param, clientId, clientSecret),AliFaceInfo.class);
                FaceInfo faceInfo1 = new FaceInfo();
                int num = alifaceInfo.getFace_num();
                if(num>0){
                    int pos = 0,arr=0;
                    FaceInfo.Face[] faces = new FaceInfo.Face[num];
                    for(int i=0;i<num;i++){
                        pos = 0;
                        FaceInfo.Face face = faceInfo1.newFace();
                        face.setAge(alifaceInfo.getAge()[i]);
                        FaceInfo.Location location = faceInfo1.newLoation();
                        location.setLeft(alifaceInfo.getFace_rect()[i]);
                        location.setTop(alifaceInfo.getFace_rect()[i+1]);
                        location.setWidth(alifaceInfo.getFace_rect()[i+2]);
                        location.setHeight(alifaceInfo.getFace_rect()[i+3]);
                        face.setLocation(location);
                        faces[arr++] = face;
                    }
                    FaceInfo.Resut resut = faceInfo1.newResult();
                    resut.setFace_list(faces);
                    resut.setFace_num(num);
                    faceInfo1.setResult(resut);

                }
                oauthBack.onOauthSucc(faceInfo1);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
