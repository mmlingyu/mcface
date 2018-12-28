package com.sxz.ai.face;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

public class LooperThread extends Thread {
    public static Handler handler;
    private FrameData frameData;
    private Handler mainHanderl;
    HairVM baiduOauthApi;
    private String token;
    private Object object = new Object();
    public LooperThread(MTCNN mtcnn,Handler mainHander,HairVM hairVM){
        this.mtcnn = mtcnn;
        this.mainHanderl = mainHander;
        this.baiduOauthApi = hairVM;
    }
    public LooperThread(MTCNN mtcnn,Handler mainHander){
        this.mtcnn = mtcnn;
        this.mainHanderl = mainHander;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    MTCNN mtcnn;
    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();

        Looper.prepare();
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                synchronized (object) {
                    Mat rgbs = (Mat) msg.obj;
                    Bitmap bm = Bitmap.createBitmap(rgbs.cols(), rgbs.rows(), Bitmap.Config.ARGB_8888);
                    org.opencv.android.Utils.matToBitmap(rgbs, bm);
                    Vector<Box> boxes = mtcnn.detectFaces(bm, 80);
                    if (boxes.size() < 1) {
                        frameData = new FrameData(OpenImageActivity.NO_FACE, rgbs);
                        Message message = new Message();
                        message.obj = frameData;
                        mainHanderl.sendMessage(message);
                    } else {
                        frameData = new FrameData(OpenImageActivity.START_DECT, rgbs);
                        frameData.setFaceBox(boxes);
                        frameData.setBitmap(bm);
                        processImage(frameData);
                    }
                }

            }

        };
        Looper.loop();

    }

    public void processImage(final FrameData frameData){
        //Bitmap bm= Utils.copyBitmap(imageBitmap);

            /*for (int i=0;i<boxes.size();i++) {
                Utils.drawRect(bm, boxes.get(i).transform2Rect());
                Utils.drawPoints(bm, boxes.get(i).landmark);
                final org.opencv.core.Rect cvect = new org.opencv.core.Rect(boxes.get(i).transform2Rect().left, boxes.get(i).transform2Rect().top, boxes.get(i).transform2Rect().width(), boxes.get(i).transform2Rect().height());
                Imgproc.rectangle(mRgba,cvect.tl(), cvect.br(), new Scalar(0, 255, 0, 255), 3);
            }*/
        Log.d("---->", "发现人脸:");
        final long startTime = System.currentTimeMillis();
        baiduOauthApi.getFaceInfo(Utils.bitmapToBase64(frameData.getBitmap()), token,new OauthBack() {
            @Override
            public void onOauthSucc(FaceInfo faceInfo, final Rect rect) {

            }

            @Override
            public void onOauthSucc(FaceInfo faceInfo) {
                if(faceInfo.getError_code()>0){
                    Message message = new Message();
                    message.obj = frameData;
                    mainHanderl.sendMessage(message);
                    return;
                }
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



                            Bitmap bm = Bitmap.createBitmap(frameData.getRgb().cols(), frameData.getRgb().rows(), Bitmap.Config.ARGB_8888);
                            org.opencv.android.Utils.matToBitmap(frameData.getRgb(),bm);
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
                            org.opencv.android.Utils.bitmapToMat(bm,frameData.getRgb());
                            Imgproc.rectangle(frameData.getRgb(),rect1.tl(), rect1.br(), new Scalar(0, 255, 0, 255), 3);
                            Message message = new Message();
                            message.obj = frameData;
                            mainHanderl.sendMessage(message);
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
}
