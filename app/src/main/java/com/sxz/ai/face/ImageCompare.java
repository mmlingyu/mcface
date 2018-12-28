package com.sxz.ai.face;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageCompare {

    private boolean compareResult = false;
    private String mark = "_compareResult";
    /**
     * 比较两张图片，如不同则将不同处标记并输出到新的图片中
     */
    public double CompareAndMarkDiff(Mat mat1, Mat mat2) {
        if(mat1==null||mat2==null)return -1;
        if(mat1.empty()||mat2.empty())return -1;
        mat1 = Imgcodecs.imdecode(mat1, Imgcodecs.IMREAD_UNCHANGED);
        mat2 = Imgcodecs.imdecode(mat2, Imgcodecs.IMREAD_UNCHANGED);
        /*Mat mat1 = Imgcodecs.imread(imagePath1, Imgcodecs.IMREAD_UNCHANGED);
        Mat mat2 = Imgcodecs.imread(imagePath2, Imgcodecs.IMREAD_UNCHANGED);*/
        if (mat1.cols() == 0 || mat2.cols() == 0 || mat1.rows() == 0 || mat2.rows() == 0) {
            System.out.println("图片文件路径异常，获取的图片大小为0，无法读取");
            return -1;
        }
        if (mat1.cols() != mat2.cols() || mat1.rows() != mat2.rows()) {
            System.out.println("两张图片大小不同，无法比较");
            return -1;
        }
        mat1.convertTo(mat1, CvType.CV_8UC1);
        mat2.convertTo(mat2, CvType.CV_8UC1);
        Mat mat1_gray = new Mat();
        Imgproc.cvtColor(mat1, mat1_gray, Imgproc.COLOR_BGR2GRAY);
        Mat mat2_gray = new Mat();
        Imgproc.cvtColor(mat2, mat2_gray, Imgproc.COLOR_BGR2GRAY);
        mat1_gray.convertTo(mat1_gray, CvType.CV_32F);
        mat2_gray.convertTo(mat2_gray, CvType.CV_32F);
        double result = Imgproc.compareHist(mat1_gray, mat2_gray, Imgproc.CV_COMP_CORREL);
        if (result == 1) {
            compareResult = true;//此处结果为1则为完全相同
            return 1;
        }

        return result;
    }
}
