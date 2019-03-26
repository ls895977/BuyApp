package com.enuos.jimat.activity.goods.bean;

public class GoodsBuyBean {

    /**
     * data : {"ID":2590,"CREATE_TIME":"2019-03-26 12:45:36","GOODS_ID":"143","MEMBER_ID":"1854","GOODS_PRICE":"4006.01","GOODS_COUNT":"1","GOODS_NAME":"[Beta] Mi Laptop Air 13.3\" i5-8250U 8GB DDR4 256GB SSD MX150 2GB GDDR5 (Grey)","MEMBER_NAME":"测试","PAY_TYPE":"4","PAY_TYPE_NAME":null,"TAKE_NAME":"Tihbcf","TAKE_MOBILE":"68658648658","TAKE_PROVINCE":"Sarawak","TAKE_CITY":null,"TAKE_AREA":null,"TAKE_ADDRESS":"Cjbn","TAKE_PROVINCE_ID":"360000","TAKE_CITY_ID":null,"TAKE_AREA_ID":null,"LOGISTICS_COMPANY_ID":null,"LOGISTICS_COMPANY_NAME":null,"LOGISTICS_CODE":null,"PAY_STATE":"1","ORDER_REMARK":null,"ORDER_CODE":"201903261245368773","COMPANY_ID":"3","COMPANY_NAME":"Nanjing Enuos Information & Technology Co.,Ltd.","PAY_TIME":null,"TOTAL_PRICE":"4131.51","APP_NAME":null,"APP_VERSION_NUMBER":null,"APP_VERSION_NUMBER_B":null,"MOBILE_NAME":null,"MOBILE_ONLY_CODE":null,"MOBILE_SYSTEM_NAME":null,"MOBILE_SYSTEM_CODE":null,"MOBILE_PATTERN":null,"MOBILE_PATTERN_NATIVE":null,"MOBILE_LANGUAGE":null,"MOBILE_NATION":null,"ORDER_SOURCE":null,"GOODS_IMG":"http://47.254.192.108:18080/jimat/upload/image/201901/0164c6e9e5d644b3a08027ddf2e3f249.jpg","TAKE_ID":"1249","GOODS_WEIGHT":"27","AREA_PRICE":"125.50","MEMBER_MOBILE":"60100000000","PAY_PRICE":null,"vcode":"0c05aa04e8eb2f13486b9456c029fd53"}
     * errorcode : 2000
     * errormsg : Update Successfully.
     */

    private DataBean data;
    private String errorcode;
    private String errormsg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public static class DataBean {
        /**
         * ID : 2590
         * CREATE_TIME : 2019-03-26 12:45:36
         * GOODS_ID : 143
         * MEMBER_ID : 1854
         * GOODS_PRICE : 4006.01
         * GOODS_COUNT : 1
         * GOODS_NAME : [Beta] Mi Laptop Air 13.3" i5-8250U 8GB DDR4 256GB SSD MX150 2GB GDDR5 (Grey)
         * MEMBER_NAME : 测试
         * PAY_TYPE : 4
         * PAY_TYPE_NAME : null
         * TAKE_NAME : Tihbcf
         * TAKE_MOBILE : 68658648658
         * TAKE_PROVINCE : Sarawak
         * TAKE_CITY : null
         * TAKE_AREA : null
         * TAKE_ADDRESS : Cjbn
         * TAKE_PROVINCE_ID : 360000
         * TAKE_CITY_ID : null
         * TAKE_AREA_ID : null
         * LOGISTICS_COMPANY_ID : null
         * LOGISTICS_COMPANY_NAME : null
         * LOGISTICS_CODE : null
         * PAY_STATE : 1
         * ORDER_REMARK : null
         * ORDER_CODE : 201903261245368773
         * COMPANY_ID : 3
         * COMPANY_NAME : Nanjing Enuos Information & Technology Co.,Ltd.
         * PAY_TIME : null
         * TOTAL_PRICE : 4131.51
         * APP_NAME : null
         * APP_VERSION_NUMBER : null
         * APP_VERSION_NUMBER_B : null
         * MOBILE_NAME : null
         * MOBILE_ONLY_CODE : null
         * MOBILE_SYSTEM_NAME : null
         * MOBILE_SYSTEM_CODE : null
         * MOBILE_PATTERN : null
         * MOBILE_PATTERN_NATIVE : null
         * MOBILE_LANGUAGE : null
         * MOBILE_NATION : null
         * ORDER_SOURCE : null
         * GOODS_IMG : http://47.254.192.108:18080/jimat/upload/image/201901/0164c6e9e5d644b3a08027ddf2e3f249.jpg
         * TAKE_ID : 1249
         * GOODS_WEIGHT : 27
         * AREA_PRICE : 125.50
         * MEMBER_MOBILE : 60100000000
         * PAY_PRICE : null
         * vcode : 0c05aa04e8eb2f13486b9456c029fd53
         */

        private int ID;
        private String CREATE_TIME;
        private String GOODS_ID;
        private String MEMBER_ID;
        private String GOODS_PRICE;
        private String GOODS_COUNT;
        private String GOODS_NAME;
        private String MEMBER_NAME;
        private String PAY_TYPE;
        private Object PAY_TYPE_NAME;
        private String TAKE_NAME;
        private String TAKE_MOBILE;
        private String TAKE_PROVINCE;
        private Object TAKE_CITY;
        private Object TAKE_AREA;
        private String TAKE_ADDRESS;
        private String TAKE_PROVINCE_ID;
        private Object TAKE_CITY_ID;
        private Object TAKE_AREA_ID;
        private Object LOGISTICS_COMPANY_ID;
        private Object LOGISTICS_COMPANY_NAME;
        private Object LOGISTICS_CODE;
        private String PAY_STATE;
        private Object ORDER_REMARK;
        private String ORDER_CODE;
        private String COMPANY_ID;
        private String COMPANY_NAME;
        private Object PAY_TIME;
        private String TOTAL_PRICE;
        private Object APP_NAME;
        private Object APP_VERSION_NUMBER;
        private Object APP_VERSION_NUMBER_B;
        private Object MOBILE_NAME;
        private Object MOBILE_ONLY_CODE;
        private Object MOBILE_SYSTEM_NAME;
        private Object MOBILE_SYSTEM_CODE;
        private Object MOBILE_PATTERN;
        private Object MOBILE_PATTERN_NATIVE;
        private Object MOBILE_LANGUAGE;
        private Object MOBILE_NATION;
        private Object ORDER_SOURCE;
        private String GOODS_IMG;
        private String TAKE_ID;
        private String GOODS_WEIGHT;
        private String AREA_PRICE;
        private String MEMBER_MOBILE;
        private Object PAY_PRICE;
        private String vcode;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getCREATE_TIME() {
            return CREATE_TIME;
        }

        public void setCREATE_TIME(String CREATE_TIME) {
            this.CREATE_TIME = CREATE_TIME;
        }

        public String getGOODS_ID() {
            return GOODS_ID;
        }

        public void setGOODS_ID(String GOODS_ID) {
            this.GOODS_ID = GOODS_ID;
        }

        public String getMEMBER_ID() {
            return MEMBER_ID;
        }

        public void setMEMBER_ID(String MEMBER_ID) {
            this.MEMBER_ID = MEMBER_ID;
        }

        public String getGOODS_PRICE() {
            return GOODS_PRICE;
        }

        public void setGOODS_PRICE(String GOODS_PRICE) {
            this.GOODS_PRICE = GOODS_PRICE;
        }

        public String getGOODS_COUNT() {
            return GOODS_COUNT;
        }

        public void setGOODS_COUNT(String GOODS_COUNT) {
            this.GOODS_COUNT = GOODS_COUNT;
        }

        public String getGOODS_NAME() {
            return GOODS_NAME;
        }

        public void setGOODS_NAME(String GOODS_NAME) {
            this.GOODS_NAME = GOODS_NAME;
        }

        public String getMEMBER_NAME() {
            return MEMBER_NAME;
        }

        public void setMEMBER_NAME(String MEMBER_NAME) {
            this.MEMBER_NAME = MEMBER_NAME;
        }

        public String getPAY_TYPE() {
            return PAY_TYPE;
        }

        public void setPAY_TYPE(String PAY_TYPE) {
            this.PAY_TYPE = PAY_TYPE;
        }

        public Object getPAY_TYPE_NAME() {
            return PAY_TYPE_NAME;
        }

        public void setPAY_TYPE_NAME(Object PAY_TYPE_NAME) {
            this.PAY_TYPE_NAME = PAY_TYPE_NAME;
        }

        public String getTAKE_NAME() {
            return TAKE_NAME;
        }

        public void setTAKE_NAME(String TAKE_NAME) {
            this.TAKE_NAME = TAKE_NAME;
        }

        public String getTAKE_MOBILE() {
            return TAKE_MOBILE;
        }

        public void setTAKE_MOBILE(String TAKE_MOBILE) {
            this.TAKE_MOBILE = TAKE_MOBILE;
        }

        public String getTAKE_PROVINCE() {
            return TAKE_PROVINCE;
        }

        public void setTAKE_PROVINCE(String TAKE_PROVINCE) {
            this.TAKE_PROVINCE = TAKE_PROVINCE;
        }

        public Object getTAKE_CITY() {
            return TAKE_CITY;
        }

        public void setTAKE_CITY(Object TAKE_CITY) {
            this.TAKE_CITY = TAKE_CITY;
        }

        public Object getTAKE_AREA() {
            return TAKE_AREA;
        }

        public void setTAKE_AREA(Object TAKE_AREA) {
            this.TAKE_AREA = TAKE_AREA;
        }

        public String getTAKE_ADDRESS() {
            return TAKE_ADDRESS;
        }

        public void setTAKE_ADDRESS(String TAKE_ADDRESS) {
            this.TAKE_ADDRESS = TAKE_ADDRESS;
        }

        public String getTAKE_PROVINCE_ID() {
            return TAKE_PROVINCE_ID;
        }

        public void setTAKE_PROVINCE_ID(String TAKE_PROVINCE_ID) {
            this.TAKE_PROVINCE_ID = TAKE_PROVINCE_ID;
        }

        public Object getTAKE_CITY_ID() {
            return TAKE_CITY_ID;
        }

        public void setTAKE_CITY_ID(Object TAKE_CITY_ID) {
            this.TAKE_CITY_ID = TAKE_CITY_ID;
        }

        public Object getTAKE_AREA_ID() {
            return TAKE_AREA_ID;
        }

        public void setTAKE_AREA_ID(Object TAKE_AREA_ID) {
            this.TAKE_AREA_ID = TAKE_AREA_ID;
        }

        public Object getLOGISTICS_COMPANY_ID() {
            return LOGISTICS_COMPANY_ID;
        }

        public void setLOGISTICS_COMPANY_ID(Object LOGISTICS_COMPANY_ID) {
            this.LOGISTICS_COMPANY_ID = LOGISTICS_COMPANY_ID;
        }

        public Object getLOGISTICS_COMPANY_NAME() {
            return LOGISTICS_COMPANY_NAME;
        }

        public void setLOGISTICS_COMPANY_NAME(Object LOGISTICS_COMPANY_NAME) {
            this.LOGISTICS_COMPANY_NAME = LOGISTICS_COMPANY_NAME;
        }

        public Object getLOGISTICS_CODE() {
            return LOGISTICS_CODE;
        }

        public void setLOGISTICS_CODE(Object LOGISTICS_CODE) {
            this.LOGISTICS_CODE = LOGISTICS_CODE;
        }

        public String getPAY_STATE() {
            return PAY_STATE;
        }

        public void setPAY_STATE(String PAY_STATE) {
            this.PAY_STATE = PAY_STATE;
        }

        public Object getORDER_REMARK() {
            return ORDER_REMARK;
        }

        public void setORDER_REMARK(Object ORDER_REMARK) {
            this.ORDER_REMARK = ORDER_REMARK;
        }

        public String getORDER_CODE() {
            return ORDER_CODE;
        }

        public void setORDER_CODE(String ORDER_CODE) {
            this.ORDER_CODE = ORDER_CODE;
        }

        public String getCOMPANY_ID() {
            return COMPANY_ID;
        }

        public void setCOMPANY_ID(String COMPANY_ID) {
            this.COMPANY_ID = COMPANY_ID;
        }

        public String getCOMPANY_NAME() {
            return COMPANY_NAME;
        }

        public void setCOMPANY_NAME(String COMPANY_NAME) {
            this.COMPANY_NAME = COMPANY_NAME;
        }

        public Object getPAY_TIME() {
            return PAY_TIME;
        }

        public void setPAY_TIME(Object PAY_TIME) {
            this.PAY_TIME = PAY_TIME;
        }

        public String getTOTAL_PRICE() {
            return TOTAL_PRICE;
        }

        public void setTOTAL_PRICE(String TOTAL_PRICE) {
            this.TOTAL_PRICE = TOTAL_PRICE;
        }

        public Object getAPP_NAME() {
            return APP_NAME;
        }

        public void setAPP_NAME(Object APP_NAME) {
            this.APP_NAME = APP_NAME;
        }

        public Object getAPP_VERSION_NUMBER() {
            return APP_VERSION_NUMBER;
        }

        public void setAPP_VERSION_NUMBER(Object APP_VERSION_NUMBER) {
            this.APP_VERSION_NUMBER = APP_VERSION_NUMBER;
        }

        public Object getAPP_VERSION_NUMBER_B() {
            return APP_VERSION_NUMBER_B;
        }

        public void setAPP_VERSION_NUMBER_B(Object APP_VERSION_NUMBER_B) {
            this.APP_VERSION_NUMBER_B = APP_VERSION_NUMBER_B;
        }

        public Object getMOBILE_NAME() {
            return MOBILE_NAME;
        }

        public void setMOBILE_NAME(Object MOBILE_NAME) {
            this.MOBILE_NAME = MOBILE_NAME;
        }

        public Object getMOBILE_ONLY_CODE() {
            return MOBILE_ONLY_CODE;
        }

        public void setMOBILE_ONLY_CODE(Object MOBILE_ONLY_CODE) {
            this.MOBILE_ONLY_CODE = MOBILE_ONLY_CODE;
        }

        public Object getMOBILE_SYSTEM_NAME() {
            return MOBILE_SYSTEM_NAME;
        }

        public void setMOBILE_SYSTEM_NAME(Object MOBILE_SYSTEM_NAME) {
            this.MOBILE_SYSTEM_NAME = MOBILE_SYSTEM_NAME;
        }

        public Object getMOBILE_SYSTEM_CODE() {
            return MOBILE_SYSTEM_CODE;
        }

        public void setMOBILE_SYSTEM_CODE(Object MOBILE_SYSTEM_CODE) {
            this.MOBILE_SYSTEM_CODE = MOBILE_SYSTEM_CODE;
        }

        public Object getMOBILE_PATTERN() {
            return MOBILE_PATTERN;
        }

        public void setMOBILE_PATTERN(Object MOBILE_PATTERN) {
            this.MOBILE_PATTERN = MOBILE_PATTERN;
        }

        public Object getMOBILE_PATTERN_NATIVE() {
            return MOBILE_PATTERN_NATIVE;
        }

        public void setMOBILE_PATTERN_NATIVE(Object MOBILE_PATTERN_NATIVE) {
            this.MOBILE_PATTERN_NATIVE = MOBILE_PATTERN_NATIVE;
        }

        public Object getMOBILE_LANGUAGE() {
            return MOBILE_LANGUAGE;
        }

        public void setMOBILE_LANGUAGE(Object MOBILE_LANGUAGE) {
            this.MOBILE_LANGUAGE = MOBILE_LANGUAGE;
        }

        public Object getMOBILE_NATION() {
            return MOBILE_NATION;
        }

        public void setMOBILE_NATION(Object MOBILE_NATION) {
            this.MOBILE_NATION = MOBILE_NATION;
        }

        public Object getORDER_SOURCE() {
            return ORDER_SOURCE;
        }

        public void setORDER_SOURCE(Object ORDER_SOURCE) {
            this.ORDER_SOURCE = ORDER_SOURCE;
        }

        public String getGOODS_IMG() {
            return GOODS_IMG;
        }

        public void setGOODS_IMG(String GOODS_IMG) {
            this.GOODS_IMG = GOODS_IMG;
        }

        public String getTAKE_ID() {
            return TAKE_ID;
        }

        public void setTAKE_ID(String TAKE_ID) {
            this.TAKE_ID = TAKE_ID;
        }

        public String getGOODS_WEIGHT() {
            return GOODS_WEIGHT;
        }

        public void setGOODS_WEIGHT(String GOODS_WEIGHT) {
            this.GOODS_WEIGHT = GOODS_WEIGHT;
        }

        public String getAREA_PRICE() {
            return AREA_PRICE;
        }

        public void setAREA_PRICE(String AREA_PRICE) {
            this.AREA_PRICE = AREA_PRICE;
        }

        public String getMEMBER_MOBILE() {
            return MEMBER_MOBILE;
        }

        public void setMEMBER_MOBILE(String MEMBER_MOBILE) {
            this.MEMBER_MOBILE = MEMBER_MOBILE;
        }

        public Object getPAY_PRICE() {
            return PAY_PRICE;
        }

        public void setPAY_PRICE(Object PAY_PRICE) {
            this.PAY_PRICE = PAY_PRICE;
        }

        public String getVcode() {
            return vcode;
        }

        public void setVcode(String vcode) {
            this.vcode = vcode;
        }
    }
}
