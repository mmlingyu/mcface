package com.sxz.ai.face;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

import java.util.Vector;

public class FrameData {
    private int state;
    private Mat rgb;
    private Vector<Box> faceBox;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Vector<Box> getFaceBox() {
        return faceBox;
    }

    public void setFaceBox(Vector<Box> faceBox) {
        this.faceBox = faceBox;
    }

    public FrameData(int state, Mat rgb) {
        this.state = state;
        this.rgb = rgb;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Mat getRgb() {
        return rgb;
    }

    public void setRgb(Mat rgb) {
        this.rgb = rgb;
    }
}
