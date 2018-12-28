package com.sxz.ai.face;

import java.util.HashMap;

/**
 * Created by gjt on 2016/7/18.
 */
public class AliFaceInfo {

    private int errno;
    private String err_msg;
    private int face_num;
    private int[] face_rect;
    private int[] gender;
    private int[] age;
    private int[] expression;
    private int[] glass;

    public int getFace_num() {
        return face_num;
    }

    public void setFace_num(int face_num) {
        this.face_num = face_num;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public int[] getFace_rect() {
        return face_rect;
    }

    public void setFace_rect(int[] face_rect) {
        this.face_rect = face_rect;
    }

    public int[] getGender() {
        return gender;
    }

    public void setGender(int[] gender) {
        this.gender = gender;
    }

    public int[] getAge() {
        return age;
    }

    public void setAge(int[] age) {
        this.age = age;
    }

    public int[] getExpression() {
        return expression;
    }

    public void setExpression(int[] expression) {
        this.expression = expression;
    }

    public int[] getGlass() {
        return glass;
    }

    public void setGlass(int[] glass) {
        this.glass = glass;
    }
}

