package com.sxz.ai.face;

import android.graphics.Rect;

public class DetctFaceInfo {
    private Rect rect;
    private org.opencv.core.Rect cvrect;
    private FaceInfo.Face face;

    public FaceInfo.Face getFace() {
        return face;
    }

    public void setFace(FaceInfo.Face face) {
        this.face = face;
    }

    public org.opencv.core.Rect getCvrect() {
        return cvrect;
    }

    public void setCvrect(org.opencv.core.Rect cvrect) {
        this.cvrect = cvrect;
    }

    public DetctFaceInfo() {
    }

    public DetctFaceInfo(Rect rect, FaceInfo.Face face) {
        this.rect = rect;
        this.face = face;
    }


    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

}
