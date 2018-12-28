package com.sxz.ai.face;

import java.util.HashMap;

/**
 * Created by gjt on 2016/7/18.
 */
public class FaceInfo {

    private int error_code;
    private String error_msg;
    private Resut result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public Resut getResult() {
        return result;
    }
    public Resut newResult() {
        return new Resut();
    }
    public void setResult(Resut result) {
        this.result = result;
    }

    public class Resut{
        private int face_num;
        private Face[] face_list;

        public int getFace_num() {
            return face_num;
        }

        public void setFace_num(int face_num) {
            this.face_num = face_num;
        }

        public Face[] getFace_list() {
            return face_list;
        }

        public void setFace_list(Face[] face_list) {
            this.face_list = face_list;
        }
    }

    public class Gender{
        private String type;
        private double probability;

        public String getType() {
            return gender.get(type);
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getProbability() {
            return probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }
    }
    private static HashMap<String,String> emotion = new HashMap();
    private static HashMap<String,String> expression = new HashMap();
    private static HashMap<String,String> glasses = new HashMap();
    private static HashMap<String,String> race = new HashMap();
    private static HashMap<String,String> face_type = new HashMap();
    private static HashMap<String,String> gender = new HashMap();
    private static HashMap<String,String> faceShape = new HashMap();
    static {
        emotion.put("angry","愤怒");
        emotion.put("disgust","厌恶");
        emotion.put("fear","恐惧");
        emotion.put("happy","高兴");
        emotion.put("sad","伤心");
        emotion.put("surprise","惊讶");
        emotion.put("neutral","无情绪");

        expression.put("none","不笑");
        expression.put("smile","微笑");
        expression.put("laugh","大笑");

        glasses.put("none","无眼镜");
        glasses.put("smile","普通眼镜");
        glasses.put("laugh","墨镜");

        race.put("yellow","黄种人");
        race.put("white","白种人");
        race.put("black","黑种人");
        race.put("arabs","阿拉伯人");


        face_type.put("human","真实人脸");
        face_type.put("cartoon","卡通人脸");

        gender.put("male","男性 ");
        gender.put("female","女性");


        faceShape.put("square","方脸");
        faceShape.put("triangle","三角脸型");
        faceShape.put("oval","椭圆脸");
        faceShape.put("heart","心形脸");
        faceShape.put("round","圆脸");
    }
     class Emotion{
        private String type;//angry:愤怒 disgust:厌恶 fear:恐惧 happy:高兴sad:伤心 surprise:惊讶 neutral:无情绪
        public String getType() {
            return emotion.get(type);
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    class Expression{
        private String type;//表情，none，不笑；smile，微笑；laugh，大笑
        private float probability;

        public String getType() {
            return expression.get(type);
        }

        public void setType(String type) {
            this.type = type;
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }
    }

    class Glasses{
        private String type;///是否带眼镜，0-无眼镜，1-普通眼镜，2-墨镜
        private float probability;

        public String getType() {
            return glasses.get(type);
        }

        public void setType(String type) {
            this.type = type;
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }
    }

    class Race{
        private String type;////yellow、white、black、arabs
        private float probability;

        public String getType() {
            return race.get(type);
        }

        public void setType(String type) {
            this.type = type;
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }
    }
    class FaceType{
        private String type;//human: 真实人脸 cartoon: 卡通人脸
        private float probability;

        public String getType() {
            return face_type.get(type);
        }

        public void setType(String type) {
            this.type = type;
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }
    }

    public Face newFace(){
        return new Face();
    }
    public class Face{
        private String face_token;
        private Location location;//人脸
        private double face_probability;
        private Angle angle;
        private FaceShape face_shape;//脸型
        private Emotion emotion;
        private Gender gender;//性别 male、female
        private Point[] landmark;
       // private Point[] landmark72;
        private double beauty;//颜值
        private double age;//年龄
        private Expression expression;//表情，0，不笑；1，微笑；2，大笑
        private Glasses glasses;//是否带眼镜，0-无眼镜，1-普通眼镜，2-墨镜
        private Race race;//yellow、white、black、arabs
        private FaceType face_type;//human: 真实人脸 cartoon: 卡通人脸

        public Face(){

        }
        public FaceType getFace_type() {
            return face_type;
        }

        public void setFace_type(FaceType face_type) {
            this.face_type = face_type;
        }

        public Emotion getEmotion() {
            return emotion;
        }

        public void setEmotion(Emotion emotion) {
            this.emotion = emotion;
        }

        public Race getRace() {
            return race;
        }

        public void setRace(Race race) {
            this.race = race;
        }

        public double getAge() {
            return age;
        }

        public Expression getExpression() {
            return expression;
        }

        public void setExpression(Expression expression) {
            this.expression = expression;
        }

        public Glasses getGlasses() {
            return glasses;
        }

        public void setGlasses(Glasses glasses) {
            this.glasses = glasses;
        }

        public void setAge(double age) {
            this.age = age;
        }

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public double getFace_probability() {
            return face_probability;
        }

        public void setFace_probability(double face_probability) {
            this.face_probability = face_probability;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public Angle getAngle() {
            return angle;
        }

        public void setAngle(Angle angle) {
            this.angle = angle;
        }

        public FaceShape getFace_shape() {
            return face_shape;
        }

        public void setFace_shape(FaceShape face_shape) {
            this.face_shape = face_shape;
        }

        public Point[] getLandmark() {
            return landmark;
        }

        public void setLandmark(Point[] landmark) {
            this.landmark = landmark;
        }

      /*  public Point[] getLandmark72() {
            return landmark72;
        }

        public void setLandmark72(Point[] landmark72) {
            this.landmark72 = landmark72;
        }*/

        public double getBeauty() {
            return beauty;
        }

        public void setBeauty(double beauty) {
            this.beauty = beauty;
        }
    }
    public  class Point{
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
    public class FaceShape{
        private String type;//square/triangle/oval/heart/round
        private double probability;

        public String getType() {
            return faceShape.get(type);
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getProbability() {
            return probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }
    }
    public  class Angle{
        private double yaw;
        private double pitch;
        private double roll;

        public double getYaw() {
            return yaw;
        }

        public void setYaw(double yaw) {
            this.yaw = yaw;
        }

        public double getPitch() {
            return pitch;
        }

        public void setPitch(double pitch) {
            this.pitch = pitch;
        }

        public double getRoll() {
            return roll;
        }

        public void setRoll(double roll) {
            this.roll = roll;
        }
    }
    public Location newLoation(){
        return new Location();
    }
    public class Location{
        private double left;
        private double top;
        private double width;
        private double height;
        private double rotation;

        public double getLeft() {
            return left;
        }

        public void setLeft(double left) {
            this.left = left;
        }

        public double getTop() {
            return top;
        }

        public void setTop(double top) {
            this.top = top;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public double getRotation() {
            return rotation;
        }

        public void setRotation(double rotation) {
            this.rotation = rotation;
        }
    }
}

