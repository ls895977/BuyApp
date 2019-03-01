package com.enuos.jimat.model;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by nzz on 2017/6/28.
 * 带城市代码的 json 解析
 */
public class ProvinceJsonBean implements IPickerViewData {

    private String provinceCode;
    private String province;
    private List<CityBean> cities;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<CityBean> getCities() {
        return cities;
    }

    public void setCities(List<CityBean> cities) {
        this.cities = cities;
    }

    /**
     * 实现 IPickerViewData 接口
     * 用来显示 PickerView 上的字符串
     * PickerView 会通过 IPickerViewData 获取 getPickerViewText 方法显示出来
     */
    @Override
    public String getPickerViewText() {
        return this.province;
    }


    /**
     * 内部类
     * 城市选择的 Bean 实体类
     */
    public static class CityBean implements IPickerViewData {

        private String cityCode;
        private String city;
        private List<CountyBean> counties;

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public List<CountyBean> getCounties() {
            return counties;
        }

        public void setCounties(List<CountyBean> counties) {
            this.counties = counties;
        }

        @Override
        public String getPickerViewText() {
            return this.city;
        }

    }

    /**
     * 内部类
     * 地区选择的 Bean 实体类
     */
    public static class CountyBean implements IPickerViewData {

        private String countyCode;
        private String county;

        public CountyBean(String countyCode, String county) {
            this.countyCode = countyCode;
            this.county = county;
        }

        public String getCountyCode() {
            return countyCode;
        }

        public void setCountyCode(String countyCode) {
            this.countyCode = countyCode;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        @Override
        public String getPickerViewText() {
            return this.county;
        }
    }

}
