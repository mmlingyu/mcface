package com.sxz.ai.face;
/*
  MTCNN For Android
  by cjf@xmu 20180625
 */

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxz.ai.face.utils.SharedPreferencesHelper;
import com.tao.admin.loglib.Logger;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

public class OpenV2Activity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    public static final String TAG="OpencvMainActivity";
    Bitmap bitmap;
    HairVM baiduOauthApi;
    CameraBridgeViewBase cameraView;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private CamcorderProfile mProfile;
    private String token;
    public static final int START_DECT=12;
    public static final int NO_FACE=13;
    private  Bitmap readFromAssets(String filename){
        Bitmap bitmap;
        AssetManager asm=getAssets();
        try {
            InputStream is=asm.open(filename);
            bitmap= BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            Log.e("MainActivity","[*]failed to open "+filename);
            e.printStackTrace();
            return null;
        }
        return Utils.copyBitmap(bitmap); //返回mutable的image
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    cameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


    @Override
    public void onResume(){
        super.onResume();
        baiduOauthApi.getToken(new OauthBack(){

            @Override
            public void onOauthSucc(FaceInfo faceInfo, Rect rect) {

            }

            @Override
            public void onOauthSucc(FaceInfo faceInfo) {

            }

            @Override
            public void onTokenSucc(String token) {
                OpenV2Activity.this.token = token;
            }
        });
        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);

        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    MTCNN mtcnn;
    Random random = new Random();
    int[] colors = new int[]{Color.RED,Color.GREEN,Color.rgb(255,153,102),Color.rgb(255,204,204),Color.BLUE,Color.YELLOW,Color.rgb(51,153,153)};
    private int getRandomColor(){
        int s = random.nextInt(colors.length)%(colors.length);
        return colors[s];
    }
    String familyName = "宋体";
    Typeface font = Typeface.create(familyName, Typeface.NORMAL);
    Paint p = new Paint();
    private int w,h;
    public void processImage(Bitmap bm){
        //Bitmap bm= Utils.copyBitmap(imageBitmap);

            /*for (int i=0;i<boxes.size();i++) {
                Utils.drawRect(bm, boxes.get(i).transform2Rect());
                Utils.drawPoints(bm, boxes.get(i).landmark);
                final org.opencv.core.Rect cvect = new org.opencv.core.Rect(boxes.get(i).transform2Rect().left, boxes.get(i).transform2Rect().top, boxes.get(i).transform2Rect().width(), boxes.get(i).transform2Rect().height());
                Imgproc.rectangle(mRgba,cvect.tl(), cvect.br(), new Scalar(0, 255, 0, 255), 3);
            }*/
        Logger.i(TAG, "发现人脸:");
        OpenV2Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                banner.setText("有人靠近,识别中...");
            }
        });

             final long startTime = System.currentTimeMillis();
                 baiduOauthApi.getFaceInfoV2(bitmapToBase64(bm), token, new OauthBack() {
                     @Override
                     public void onOauthSucc(FaceInfo faceInfo, final Rect rect) {

                     }

                     @Override
                     public void onOauthSucc(FaceInfo faceInfo) {
                         FaceInfo.Resut resut = faceInfo.getResult();
                         int i = 0;
                         if (resut != null) {
                             final FaceInfo.Face[] faces = resut.getFace_list();
                             if (faces != null && faces.length > 0) {
                                 OpenV2Activity.this.runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         banner.setText(faces[0].getGender().getType()+" "+faces[0].getAge() + "岁 "+faces[0].getFace_shape().getType() +" "+faces[0].getExpression().getType()+"---推荐的广告!");
                                     }
                                 });
                                 long estimatedTime = System.currentTimeMillis() - startTime;
                                 Logger.i(TAG, "识别人脸:" + faces.length + "--" + faceInfo.getError_code() + "--" + estimatedTime);

                                 for (FaceInfo.Face face : faces) {
                                     double[] doubles = new double[4];
                                     doubles[0] = (int) face.getLocation().getLeft();
                                     doubles[1] = (int) face.getLocation().getTop();
                                     doubles[2] = (int) face.getLocation().getWidth();
                                     doubles[3] = (int) face.getLocation().getHeight();
                                     org.opencv.core.Rect rect1 = new org.opencv.core.Rect();
                                     rect1.set(doubles);
                                     Bitmap bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.RGB_565);
                                     org.opencv.android.Utils.matToBitmap(mRgba, bm);

                                     Canvas canvasTemp = new Canvas(bm);
                                     int y = rect1.y + rect1.height;
                                     p.setColor(getRandomColor());
                                     canvasTemp.drawText(face.getAge() + "岁", rect1.x, (float) (y + 30), p);
                                    canvasTemp.drawText(face.getGender().getType(), rect1.x, (float) (y+ 60), p);
                                    canvasTemp.drawText("颜值:"+face.getBeauty(), rect1.x, (float) ( y + 90), p);
                                    canvasTemp.drawText(face.getEmotion().getType(), rect1.x, (float) (y)+120, p);
                                    canvasTemp.drawText(face.getGlasses().getType(), rect1.x, (float) (y+ 150), p);
                                    canvasTemp.drawText(face.getFace_shape().getType(), rect1.x, (float) ( y + 180), p);
                                    canvasTemp.drawText(face.getExpression().getType(), rect1.x, (float) (y)+210, p);
                                    canvasTemp.drawText(face.getFace_type().getType(),rect1.x, (float) (y+ 240), p);
                                    canvasTemp.drawText(face.getRace().getType(), rect1.x, (float) (y+ 270), p);
                                     canvasTemp.save();
                                     org.opencv.android.Utils.bitmapToMat(bm, mRgba);
                                     Imgproc.rectangle(mRgba, rect1.tl(), rect1.br(), new Scalar(0, 255, 0, 255), 3);
                                     long estimatedTime1 = System.currentTimeMillis() - startTime;
                                     Logger.i(TAG, "单次执行时长： " + estimatedTime1 + "ms");

                                 }
                             }
                         }
                     }

                     @Override
                     public void onTokenSucc(String token) {

                     }
                 });
             }
    private Mat mRgba; //图像容器
    private Mat mGray;
    private boolean isFront = false;
    private TextView banner,bottom;
    private String TIME = "time";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_face_ad);
        baiduOauthApi = new HairVM(this);
        cameraView = findViewById(R.id.cameraView);
        banner = findViewById(R.id.banner);
        bottom = findViewById(R.id.bottom);
        WindowManager wm1 = this.getWindowManager();
         w= wm1.getDefaultDisplay().getWidth();
         h= wm1.getDefaultDisplay().getHeight();
        cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_ANY); //摄像头索引        -1/0：后置双摄     1：前置
        cameraView.enableFpsMeter(); //显示FPS
        cameraView.setCvCameraViewListener(this);
        p.setTypeface(font);
        p.setTextSize(26);
        SharedPreferencesHelper.init(this);
        int time = SharedPreferencesHelper.getInstance().getTime(TIME);
        if(time>=20){
            Toast.makeText(this,"使用超过限制,请联系我们",Toast.LENGTH_LONG).show();
            finish();
        }
        SharedPreferencesHelper.getInstance().saveData(TIME,(time+1));
       mtcnn= new MTCNN(getAssets());
    }
    Mat grayscaleImage;
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        FrameLayout.LayoutParams layoutParamsTop = (FrameLayout.LayoutParams) banner.getLayoutParams();
        layoutParamsTop.height = (h-height)/2-240;
        banner.setLayoutParams(layoutParamsTop);

        FrameLayout.LayoutParams layoutParamsBottom = (FrameLayout.LayoutParams) bottom.getLayoutParams();
        layoutParamsBottom.height = (h-height)/2-240;
        bottom.setLayoutParams(layoutParamsBottom);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        grayscaleImage.release();
    }

    Bitmap bm;
        @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            mGray = inputFrame.gray(); //单通道灰度图
            mRgba = inputFrame.rgba();
            // 旋转输入帧
            if (cameraView.getCameraIndex() == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                Core.rotate(mRgba, mRgba, Core.ROTATE_90_COUNTERCLOCKWISE);
                Core.rotate(mGray, mGray, Core.ROTATE_90_COUNTERCLOCKWISE);
                Core.flip(mRgba, mRgba, 1);
                Core.flip(mGray, mGray, 1);
            } else {
                Core.rotate(mRgba, mRgba, Core.ROTATE_90_CLOCKWISE);
                Core.rotate(mGray, mGray, Core.ROTATE_90_CLOCKWISE);
            }
            if (!TextUtils.isEmpty(OpenV2Activity.this.token)) {
                bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                org.opencv.android.Utils.matToBitmap(mRgba, bm);
                Vector<Box> boxes = mtcnn.detectFaces(bm, 80);
                if (boxes.size() < 1) {

                    OpenV2Activity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            banner.setText("广告banner区域");
                        }
                    });
                }else{
                    processImage(bm);
                }
            } else {

            }

            return mRgba;

    }

}
