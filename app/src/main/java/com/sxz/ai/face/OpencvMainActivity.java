package com.sxz.ai.face;
/*
  MTCNN For Android
  by cjf@xmu 20180625
 */

import android.content.Intent;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Vector;

public class OpencvMainActivity extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2{
    String TAG="OpencvMainActivity";
    Bitmap bitmap;
    HairVM baiduOauthApi;
    CameraBridgeViewBase cameraView;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private CamcorderProfile mProfile;
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
        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);

        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void staticLoadCVLibraries(){
        boolean load = OpenCVLoader.initDebug();
        if(load) {
            Log.i("CV", "Open CV Libraries loaded...");
        }
    }

    MTCNN mtcnn;
    private int line;
    private boolean processing;
    public void processImage(Mat mRgba){
        //Bitmap bm= Utils.copyBitmap(imageBitmap);
        ;
        try {
            Bitmap bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);;
            org.opencv.android.Utils.matToBitmap(mRgba, bm);
            Vector<Box> boxes=mtcnn.detectFaces(bm,80);
            if(boxes.size()<1){
                processing = false;
                return;
            }

            final StringBuffer stringBuffer = new StringBuffer();
            for (int i=0;i<boxes.size();i++){

               /* Utils.drawRect(bm,boxes.get(i).transform2Rect());
                Utils.drawPoints(bm,boxes.get(i).landmark);*/
                final org.opencv.core.Rect cvect = new org.opencv.core.Rect(boxes.get(i).transform2Rect().left, boxes.get(i).transform2Rect().top, boxes.get(i).transform2Rect().width(), boxes.get(i).transform2Rect().height());
                org.opencv.android.Utils.bitmapToMat(bm,mRgba);
                android.graphics.Rect rect = boxes.get(i).transform2Rect();

                Bitmap rectBitmap = Bitmap.createBitmap(bm, boxes.get(i).transform2Rect().left, boxes.get(i).transform2Rect().top, boxes.get(i).transform2Rect().width(), boxes.get(i).transform2Rect().height());
            //imageView.setImageBitmap(bm);
                final int finalI = i;
                baiduOauthApi.getFaceInfo(new File(Utils.saveImage(rectBitmap)), new OauthBack() {
                    @Override
                    public void onOauthSucc(FaceInfo faceInfo, final Rect rect) {
                        FaceInfo.Resut resut = faceInfo.getResult();
                        if(resut!=null){
                            final FaceInfo.Face[] faces = resut.getFace_list();
                            if(faces!=null&&faces.length>0) {
                                stringBuffer.append("性别:"+faces[0].getGender().getType()+"|"+"脸型："+faces[0].getFace_shape().getType()
                                        +"|颜值:"+Double.valueOf(faces[0].getBeauty()).intValue()+"\n");
                                Log.d("---->",stringBuffer.toString());
                                //rs.setText(stringBuffer);
                                Message message = new Message();
                                message.what = 12;
                                message.obj = new DetctFaceInfo(rect,faces[0]);
                                mHandler.sendMessage(message);
                                processing = false;


                            }
                        }
                    }

                    @Override
                    public void onOauthSucc(FaceInfo faceInfo) {

                    }

                    @Override
                    public void onTokenSucc(String token) {

                    }
                },rect);
            }
        }catch (Exception e){
            Log.e(TAG,"[*]detect false:"+e);
        }
    }
    public void myMain(){
        //imageView =(ImageView)findViewById(R.id.imageView);
       // rs = (TextView)findViewById(R.id.rs);

        mtcnn=new MTCNN(getAssets());

        //staticLoadCVLibraries();
        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent, 0x1);
            }
        });*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(data==null)return;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
        }catch (Exception e){
            Log.d("MainActivity","[*]"+e);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
       //cameraView.enableFpsMeter(); //显示FPS
        cameraView.setCvCameraViewListener(this);
        myMain();
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

    private LinkedHashSet<DetctFaceInfo> linkedList = new LinkedHashSet<>();
    private void volidate(DetctFaceInfo detctFaceInfo){
        if(linkedList.size()>0) {
            DetctFaceInfo[] arraySet = new DetctFaceInfo[linkedList.size()];
            linkedList.toArray(arraySet);
            for (DetctFaceInfo dfi : arraySet) {
                if (!dfi.getRect().intersect(detctFaceInfo.getRect())) {
                    linkedList.add(detctFaceInfo);
                }
            }
        }else{
            linkedList.add(detctFaceInfo);
        }

    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==12){
                //linkedList = new LinkedList<>();
                draw((DetctFaceInfo) msg.obj);
                last=mRgba;
               /* try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }else if(msg.what == 13){


            }
        }
    };


    synchronized void draw(DetctFaceInfo detctFaceInfo ){
        volidate(detctFaceInfo);
        Bitmap bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mRgba,bm);
        Paint p = new Paint();
        String familyName ="宋体";
        Typeface font = Typeface.create(familyName,Typeface.BOLD);
        p.setColor(Color.GREEN);
        p.setTypeface(font);
        p.setTextSize(34);
        Canvas canvasTemp = new Canvas(bm);

        for(DetctFaceInfo faceInfo:linkedList) {
            canvasTemp.drawText(faceInfo.getFace().getGender().getType(),faceInfo.getRect().left,faceInfo.getRect().bottom+30,p);
            canvasTemp.drawText("脸型 ：" + faceInfo.getFace().getFace_shape(),faceInfo.getRect().left,faceInfo.getRect().bottom+80,p);
            canvasTemp.drawText("颜值 :" + faceInfo.getFace().getBeauty(),faceInfo.getRect().left,faceInfo.getRect().bottom+120,p);
            canvasTemp.save();
            //Log.d("DetctFaceInfo",faceInfo.getRect().left+"----"+faceInfo.getRect().bottom+"  ---"+linkedList.size());
           /* Imgproc.putText(mRgba, faceInfo.getGender(), new Point(faceInfo.getRect().left,faceInfo.getRect().bottom+30), 3, 1, new Scalar(0, 255, 0, 255), 2);
            Imgproc.putText(mRgba, "脸型 ：" + faceInfo.getFaceShape(), new Point(faceInfo.getRect().left,faceInfo.getRect().bottom+70), 3, 1, new Scalar(0, 255, 0, 255), 2);
            Imgproc.putText(mRgba, "颜值 :" + faceInfo.getBeauty(), new Point(faceInfo.getRect().left,faceInfo.getRect().bottom+110), 3, 1, new Scalar(0, 255, 0, 255), 2);*/
            org.opencv.android.Utils.bitmapToMat(bm,mRgba);
        }  Imgproc.rectangle(mRgba,detctFaceInfo.getCvrect().tl(), detctFaceInfo.getCvrect().br(), new Scalar(0, 255, 0, 255), 3);
    }

    private void check(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //double similer = imageCompare.CompareAndMarkDiff(last,mRgba);
               // Log.d("DetctFaceInfo","相似-"+similer);
                /*if(similer<0.7&&similer>0) {
                    linkedList.clear();
                }*/
                linkedList.clear();
                check();
            }
        }, 8000);
    }

    private boolean isInit = false;
    private Mat last;
        @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba(); //RGBA
        mGray = inputFrame.gray(); //单通道灰度图

        //解决  前置摄像头旋转显示问题
        //Core.flip(mRgba, mRgba, 0); //旋转

            processImage(mRgba);

            if(!isInit) {
                check();
                isInit = true;
            }


        //Core.flip(mGray, mGray, 1);
        //检测并显示
       /* MatOfRect faces = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0){
            for (int i = 0; i < facesArray.length; i++) {    //用框标记
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
            }
        }*/
        return mRgba;
    }

    private Bitmap mBitmap;
    public Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {


        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i("ygy", "onPictureTaken");

            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String picName = df.format(new Date());
                    File file = new File(Environment.getExternalStorageDirectory()+"/adtest/" + picName + ".jpg");
                    try {
                        File DIR = new File(Environment.getExternalStorageDirectory()+"/adtest");
                        if(!DIR.exists()){
                            DIR.mkdir();
                        }
                        file.createNewFile();
                        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.flush();
                        os.close();

                        Toast.makeText(getApplicationContext(), "图像保存成功", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    };

}
