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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,Runnable {
    String TAG="MainActivity";
    Bitmap bitmap;
    HairVM baiduOauthApi;
    private Camera mCamera;
    private SurfaceView textureView;
    private Canvas canvas;
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
    MTCNN mtcnn;
    private int line;
    public void processImage(Bitmap bm){

        try {
            if(TextUtils.isEmpty(bm.toString()))return;
            Vector<Box> boxes=mtcnn.detectFaces(bm,80);
            final StringBuffer stringBuffer = new StringBuffer();
            for (int i=0;i<boxes.size();i++){
                Bitmap rectBitmap = Bitmap.createBitmap(bm, boxes.get(i).transform2Rect().left, boxes.get(i).transform2Rect().top, boxes.get(i).transform2Rect().width(), boxes.get(i).transform2Rect().height());
                Utils.drawRect(bm,boxes.get(i).transform2Rect());
                Utils.drawPoints(bm,boxes.get(i).landmark);
                Utils.saveImage(bm);

                baiduOauthApi.getFaceInfo(new File(Utils.saveImage(rectBitmap)), new OauthBack() {
                    @Override
                    public void onOauthSucc(FaceInfo faceInfo, Rect rect) {

                    }

                    @Override
                    public void onOauthSucc(FaceInfo faceInfo) {
                        FaceInfo.Resut resut = faceInfo.getResult();
                        if(resut!=null){
                            final FaceInfo.Face[] faces = resut.getFace_list();
                            if(faces!=null&&faces.length>0) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        line++;
                                        stringBuffer.append("第"+line+"个人：性别:"+faces[0].getGender().getType()+"|"+"脸型："+faces[0].getFace_shape().getType()
                                                +"|颜值:"+Double.valueOf(faces[0].getBeauty()).intValue()+"\n");
                                        Log.d("---->",stringBuffer.toString());
                                    }
                                });


                            }
                        }
                    }

                    @Override
                    public void onTokenSucc(String token) {

                    }
                });
            }
        }catch (Exception e){
            Log.e(TAG,"[*]detect false:"+e);
        }
    }
    public void myMain(){
        //imageView =(ImageView)findViewById(R.id.imageView);
        //bitmap=readFromAssets("2.jpg");
        mtcnn=new MTCNN(getAssets());
        //processImage(bitmap);

       /* imageView.setOnClickListener(new View.OnClickListener() {
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
            processImage(bitmap);
        }catch (Exception e){
            Log.d("MainActivity","[*]"+e);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baiduOauthApi = new HairVM(this);
        textureView = ((SurfaceView)findViewById(R.id.textureview));
        surfaceHolder = textureView.getHolder();
        surfaceHolder.addCallback(this);
        myMain();
    }

    private static int pos = 0;
    private void bian(){
        takePic();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bian();
            }
        },15*1000);

    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    private void takePic() {
        if (mCamera != null) {
            //调用抓拍摄像头抓拍
            mCamera.takePicture(null, null, pictureCallback);
        } else {
            Log.e("TAG", "请检查摄像头！");
        }
    }

    private Bitmap mBitmap;
    public Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {


        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i("ygy", "onPictureTaken "+ data.length);

            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            mBitmap = rotaingImageView(270,mBitmap);
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
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
                        processImage(mBitmap);
                        Toast.makeText(getApplicationContext(), "图像保存成功", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    };

    private int w,h;


   private int getDisplayOrientation(){
    // 获取当前窗口管理器显示方向
    WindowManager windowManager = getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    int rotation = display.getRotation();
    int degrees = 0;
        switch (rotation){
        case Surface.ROTATION_0:
            degrees = 0;
            break;
        case Surface.ROTATION_90:
            degrees = 90;
            break;
        case Surface.ROTATION_180:
            degrees = 180;
            break;
        case Surface.ROTATION_270:
            degrees = 270;
            break;
    }

    android.hardware.Camera.CameraInfo camInfo =
            new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);

    // 这里其实还是不太懂：为什么要获取camInfo的方向呢？相当于相机标定？？
    int result = (camInfo.orientation - degrees + 360) % 360;

        return result;
}

    private void setSurfaceViewSize(String surfaceSize,SurfaceView surfaceTexture) {
        ViewGroup.LayoutParams params = surfaceTexture.getLayoutParams();
        if (surfaceSize.equals("16:9")) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else if (surfaceSize.equals("4:3")) {
            params.height = 4 * w / 3;
        }
        surfaceTexture.setLayoutParams(params);
    }
        public String getSurfaceViewSize(int width, int height) {
        if (equalRate(width, height, 1.33f)) {
            return "4:3";
        } else {
            return "16:9";
        }
    }

    public boolean equalRate(int width, int height, float rate) {
        float r = (float)width /(float) height;
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }



    @Override
    protected void onStop() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        super.onStop();
    }

    private SurfaceHolder surfaceHolder;

    private void drawBitmapForAlpha(Canvas c, Bitmap bmp, int x, int y) {
        bmp.setHasAlpha(true);
        Paint paint = new Paint();

        c.drawBitmap(bmp,x,y,paint);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = Camera.open(1);
        if (mCamera != null) {
            try {

                WindowManager windowManager = this.getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                //获取屏幕的宽和高
                display.getMetrics(metrics);
                w = metrics.widthPixels;
                h = metrics.heightPixels;

                mCamera.setPreviewDisplay(surfaceHolder);
               // setSurfaceViewSize(getSurfaceViewSize(w,h),textureView);

                //mCamera.setDisplayOrientation(90);


            } catch (IOException e) {
                Log.d("TAG", e.getMessage());
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.startPreview();
        int rotation = getDisplayOrientation(); //获取当前窗口方向
        mCamera.setDisplayOrientation(rotation); //设定相机显示方向

        bian();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    public void run() {

        if(mBitmap!=null) {
            canvas = surfaceHolder.lockCanvas();
            drawBitmapForAlpha(canvas, mBitmap, 0, 0);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
}
