package edu.cmu.eps.scams.logic.model;

/**
 * Created by fanmichaelyang on 4/15/18.
 */

public class Friend {

    private int fid;

    public String qrString;

    public String PhoneNumber;

    public Friend(int fid, String qrString, String PhoneNumber) {
        this.qrString = qrString;
        this.fid = fid;
        this.PhoneNumber = PhoneNumber;
    }

    public int getFid() {
        return this.fid;
    }

    public String getPhoneNumber() {
        return this.PhoneNumber;
    }

    public void setId(int id) {
        this.fid = fid;
    }

    public String getFriendQr() {
        return this.qrString;
    }

}
