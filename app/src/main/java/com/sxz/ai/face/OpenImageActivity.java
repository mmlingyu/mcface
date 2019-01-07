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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OpenImageActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    String TAG="OpencvMainActivity";
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
                OpenImageActivity.this.token = token;
                looperThread.setToken(token);
            }
        });
        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);

        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    MTCNN mtcnn;
    private volatile boolean processing = false;
    public void processImage(final Bitmap bm){
        //Bitmap bm= Utils.copyBitmap(imageBitmap);

            /*for (int i=0;i<boxes.size();i++) {
                Utils.drawRect(bm, boxes.get(i).transform2Rect());
                Utils.drawPoints(bm, boxes.get(i).landmark);
                final org.opencv.core.Rect cvect = new org.opencv.core.Rect(boxes.get(i).transform2Rect().left, boxes.get(i).transform2Rect().top, boxes.get(i).transform2Rect().width(), boxes.get(i).transform2Rect().height());
                Imgproc.rectangle(mRgba,cvect.tl(), cvect.br(), new Scalar(0, 255, 0, 255), 3);
            }*/
        Log.d("---->", "发现人脸:");
        final long startTime = System.currentTimeMillis();
        baiduOauthApi.getFaceInfo(Utils.bitmapToBase64(bm), token,new OauthBack() {
            @Override
            public void onOauthSucc(FaceInfo faceInfo, final Rect rect) {

            }

            @Override
            public void onOauthSucc(FaceInfo faceInfo) {

                processing = true;
                FaceInfo.Resut resut = faceInfo.getResult();
                int i = 0;
                if (resut != null) {

                    FaceInfo.Face[] faces = resut.getFace_list();

                    if (faces != null && faces.length > 0) {
                        Log.d("---->", "识别人脸:"+faces.length);
                        for(FaceInfo.Face face:faces) {
                           /*     stringBuffer.append("第" + (i++) + "性别:" + face.getGender().getType() + "|" + "脸型：" + face.getFace_shape().getType()
                                        + "|颜值:" + Double.valueOf(face.getBeauty()).intValue() + "\n");
                                Log.d("---->", stringBuffer.toString());*/
                            double[] doubles=  new double[4];
                            doubles[0]=(int)face.getLocation().getLeft();
                            doubles[1] = (int)face.getLocation().getTop();
                            doubles[2]=(int)face.getLocation().getWidth();
                            doubles[3] = (int)face.getLocation().getHeight();
                            org.opencv.core.Rect rect1 = new org.opencv.core.Rect();
                            rect1.set(doubles);



                            Bitmap bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                            org.opencv.android.Utils.matToBitmap(mRgba,bm);
                            Paint p = new Paint();
                            String familyName ="宋体";
                            Typeface font = Typeface.create(familyName,Typeface.NORMAL);
                            p.setColor(Color.RED);
                            p.setTypeface(font);
                            p.setTextSize(32);
                            Canvas canvasTemp = new Canvas(bm);
                            canvasTemp.drawText(face.getGender().getType(),rect1.x,(float) (rect1.br().y+rect1.height+5),p);
                            canvasTemp.drawText(face.getFace_shape().getType(),rect1.x,(float) (rect1.br().y+rect1.height+50),p);
                            canvasTemp.drawText(face.getBeauty()+"",rect1.x,(float) (rect1.br().y+rect1.height+90),p);
                            canvasTemp.save();
                            org.opencv.android.Utils.bitmapToMat(bm,mRgba);
                            Imgproc.rectangle(mRgba,rect1.tl(), rect1.br(), new Scalar(0, 255, 0, 255), 3);
                            queue.offer(mRgba);
                            long estimatedTime = System.currentTimeMillis() - startTime;
                            Log.d("---->","单次执行时长： " + estimatedTime + "ms");
                              /*  Imgproc.rectangle(mRgba,detctFaceInfo.getCvrect().tl(), detctFaceInfo.getCvrect().br(), new Scalar(0, 255, 0, 255), 3);
                                long estimatedTime = System.currentTimeMillis() - startTime;
                                Log.d("---->","单次执行时长： " + estimatedTime + "ms");*/
                              /*  Message message = new Message();
                                message.what = 12;
                                message.obj = detctFaceInfo;
                                mHandler.sendMessage(message);*/
                            //processing = false;
                        }
                        //rs.setText(stringBuffer);
                              /*  Message message = new Message();
                                message.what = 12;
                                message.obj = faces;
                                mHandler.sendMessage(message);
                                processing = false;*/
                    }
                }
            }

            @Override
            public void onTokenSucc(String token) {

            }
        });
    }
    public void initCnnLooper() {
        looperThread= new LooperThread(mtcnn,mHandler,baiduOauthApi);
        looperThread.start();
    }
    private Mat mRgba; //图像容器
    private Mat mGray;
    private int absoluteFaceSize = 0;
    private ImageCompare imageCompare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_opencv_main);
        baiduOauthApi = new HairVM(this);
        cameraView = findViewById(R.id.cameraView);
        imageCompare = new ImageCompare();
        cameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        cameraView.setCameraIndex(0); //摄像头索引        -1/0：后置双摄     1：前置
        cameraView.enableFpsMeter(); //显示FPS
        cameraView.setCvCameraViewListener(this);
         mtcnn= new MTCNN(getAssets());
        initCnnLooper();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }




    private ConcurrentLinkedQueue<Mat> queue = new ConcurrentLinkedQueue<Mat>();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrameData frameData = (FrameData) msg.obj;
             if(frameData.getState() == NO_FACE){
                queue.offer(frameData.getRgb());

            }
        }
    };
    FrameData frameData;
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba(); //RGBA
        mGray = inputFrame.gray(); //单通道灰度图

        //解决  前置摄像头旋转显示问题
        //Core.flip(mRgba, mRgba, 0); //旋转

        if(!TextUtils.isEmpty( OpenImageActivity.this.token)&&looperThread!=null) {

            Message msMessage = new Message();
            msMessage.obj = mRgba;
            looperThread.handler.sendMessage(msMessage);
        }


        return queue.poll();

    }


    LooperThread looperThread ;




}
