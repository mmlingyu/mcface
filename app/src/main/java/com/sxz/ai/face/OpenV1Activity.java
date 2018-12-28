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
import android.util.Log;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

import com.example.vcvyc.mtcnn_new.R;
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

public class OpenV1Activity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
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
                OpenV1Activity.this.token = token;
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

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {

    if (bitmap != null){ Matrix m = new Matrix();

     m.postRotate(degress);
    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    return bitmap;
    }
    return bitmap;

    }
    MTCNN mtcnn;
    private volatile boolean processing = false;
    private Object object = new Object();
    private boolean isP = false;
    Random random = new Random();
    int[] colors = new int[]{Color.RED,Color.GREEN,Color.rgb(255,153,102),Color.rgb(255,204,204),Color.BLUE,Color.YELLOW,Color.rgb(51,153,153)};
    private int getRandomColor(){
        int s = random.nextInt(colors.length)%(colors.length);
        return colors[s];
    }
    String familyName = "宋体";
    Typeface font = Typeface.create(familyName, Typeface.NORMAL);
    Paint p = new Paint();

    public void processImage(final FrameData frameData){
        //Bitmap bm= Utils.copyBitmap(imageBitmap);

            /*for (int i=0;i<boxes.size();i++) {
                Utils.drawRect(bm, boxes.get(i).transform2Rect());
                Utils.drawPoints(bm, boxes.get(i).landmark);
                final org.opencv.core.Rect cvect = new org.opencv.core.Rect(boxes.get(i).transform2Rect().left, boxes.get(i).transform2Rect().top, boxes.get(i).transform2Rect().width(), boxes.get(i).transform2Rect().height());
                Imgproc.rectangle(mRgba,cvect.tl(), cvect.br(), new Scalar(0, 255, 0, 255), 3);
            }*/
        Logger.i("---->", "发现人脸:");
      /*      Bitmap bm = Bitmap.createBitmap(frameData.getRgb().cols(), frameData.getRgb().rows(), Bitmap.Config.ARGB_8888);

           *//* if(!isP){
                rotateBitmap(bm,90);
            }*//*

        org.opencv.android.Utils.matToBitmap(frameData.getRgb(), bm);*/
             final long startTime = System.currentTimeMillis();
             synchronized (this) {
                 baiduOauthApi.getFaceInfoV2(bitmapToBase64(frameData.getBitmap()), token, new OauthBack() {
                     @Override
                     public void onOauthSucc(FaceInfo faceInfo, final Rect rect) {

                     }

                     @Override
                     public void onOauthSucc(FaceInfo faceInfo) {
                         FaceInfo.Resut resut = faceInfo.getResult();
                         int i = 0;
                         if (resut != null) {
                             FaceInfo.Face[] faces = resut.getFace_list();
                             if (faces != null && faces.length > 0) {
                                 long estimatedTime = System.currentTimeMillis() - startTime;
                                 Logger.i("---->", "识别人脸:" + faces.length + "--" + faceInfo.getError_code() + "--" + estimatedTime);
                                 for (FaceInfo.Face face : faces) {
                                     double[] doubles = new double[4];
                                     doubles[0] = (int) face.getLocation().getLeft();
                                     doubles[1] = (int) face.getLocation().getTop();
                                     doubles[2] = (int) face.getLocation().getWidth();
                                     doubles[3] = (int) face.getLocation().getHeight();
                                     org.opencv.core.Rect rect1 = new org.opencv.core.Rect();
                                     rect1.set(doubles);
                                     Bitmap bm = Bitmap.createBitmap(frameData.getRgb().cols(), frameData.getRgb().rows(), Bitmap.Config.RGB_565);
                                     org.opencv.android.Utils.matToBitmap(frameData.getRgb(), bm);

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
                                     org.opencv.android.Utils.bitmapToMat(bm, frameData.getRgb());
                                     Imgproc.rectangle(frameData.getRgb(), rect1.tl(), rect1.br(), new Scalar(0, 255, 0, 255), 3);
                                     long estimatedTime1 = System.currentTimeMillis() - startTime;
                                     Logger.i("---->", "单次执行时长： " + estimatedTime1 + "ms");
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
    }
    private Mat mRgba; //图像容器
    private Mat mGray;
    private int absoluteFaceSize = 0;
    private ImageCompare imageCompare;
    private boolean isFront = false;
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
        cameraView.setCameraIndex(isFront==true?1:0); //摄像头索引        -1/0：后置双摄     1：前置
        cameraView.enableFpsMeter(); //显示FPS
        cameraView.setCvCameraViewListener(this);
        p.setTypeface(font);
        p.setTextSize(24);
       mtcnn= new MTCNN(getAssets());
    }
    Mat grayscaleImage;
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        absoluteFaceSize = (int)(height * 0.2);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        grayscaleImage.release();
    }

    FrameData frameData;
    Bitmap bm;
        @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

            mGray = inputFrame.gray(); //单通道灰度图
            mRgba = inputFrame.rgba();

            if (isFront){
                Core.flip(mRgba, mRgba, 1);//flip aroud Y-axis
                Core.flip(mGray, mGray, 1);

            }
            int rotation = cameraView.getDisplay().getRotation();
            if (rotation == Surface.ROTATION_0) {
                Mat rotateMat = Imgproc.getRotationMatrix2D(new Point(mRgba.rows()/2,mRgba.cols()/2), 270, 1.5);
                Imgproc.warpAffine(mRgba, mRgba, rotateMat, mRgba.size());
            }
            if (!TextUtils.isEmpty(OpenV1Activity.this.token)) {
                bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.RGB_565);

                org.opencv.android.Utils.matToBitmap(mRgba, bm);
                Vector<Box> boxes = mtcnn.detectFaces(bm, 80);
                if (boxes.size() < 1) {
                    frameData = new FrameData(OpenV1Activity.START_DECT,mRgba);
                }else{
                    frameData = new FrameData(OpenV1Activity.START_DECT,mRgba);
                    frameData.setFaceBox(boxes);
                    frameData.setBitmap(bm);
                    processImage(frameData);
                }
            } else {
                frameData = new FrameData(OpenV1Activity.START_DECT, mRgba);
            }

            return frameData.getRgb();

    }

}
