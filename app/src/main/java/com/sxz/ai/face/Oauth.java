package com.sxz.ai.face;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/8.
 */

public class Oauth implements Serializable {
    String access_token;
    String token_type;
    String code;
    String player;
    String token;
    public String getCode() {
        return code;
    }

    public String getToken() {
        return token_type+" "+access_token;
    }


    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
