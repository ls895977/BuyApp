package com.enuos.jimat.utils.event;

/**
 * Created by nzz on 2017/11/29.
 * 回传收货地址 id 的 event 类
 */

public class AddressEvent {

    public String type;
    public String id;
    public String name;
    public String tel;
    public String address;
    public String provice;

    public AddressEvent(String type, String id, String name, String tel, String address, String provice) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.address = address;
        this.provice = provice;
    }
}
