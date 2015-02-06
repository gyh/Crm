package frame.system.seven.common.bean;

import java.io.Serializable;

/**
 * Created by guoyuehua on 14-5-30.
 */
public class Base implements Serializable{
    public String getUsertime() {
        return usertime;
    }

    public void setUsertime(String usertime) {
        this.usertime = usertime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUserlevel() {
        return userlevel;
    }

    public void setUserlevel(String userlevel) {
        this.userlevel = userlevel;
    }

    public String getUserev() {
        return userev;
    }

    public void setUserev(String userev) {
        this.userev = userev;
    }

    public String getUserrecord() {
        return userrecord;
    }

    public void setUserrecord(String userrecord) {
        this.userrecord = userrecord;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    private String usertime;
    private String username;
    private String phonenumber;
    private String userlevel;
    private String userev;
    private String userrecord;
    private String num;
}
