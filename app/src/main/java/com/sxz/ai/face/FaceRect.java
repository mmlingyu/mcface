package com.sxz.ai.face;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class FaceRect extends Rect {

    private Point getPoint(int offset){
        return new Point(x,y+offset);
    }
}
