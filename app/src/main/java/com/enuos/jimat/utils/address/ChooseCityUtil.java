package com.enuos.jimat.utils.address;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.model.CityBean;
import com.enuos.jimat.model.CityData;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.List;


/**
 * Created by nzz on 2017/7/5.
 */

public class ChooseCityUtil implements View.OnClickListener, NumberPicker.OnValueChangeListener {
    Context context;
    AlertDialog dialog;
    ChooseCityInterface cityInterface;
    NumberPicker npProvince, npCity, npCounty;
    TextView tvCancel, tvSure;
    String[] newCityArray = new String[3];
    CityBean bean;

    public void createDialog(Context context, String[] oldCityArray, ChooseCityInterface cityInterface) {
        this.context = context;
        this.cityInterface = cityInterface;
        Gson gson = new Gson();
        bean = gson.fromJson(CityData.getJson(), CityBean.class);
        newCityArray[0] = oldCityArray[0];
        newCityArray[1] = oldCityArray[1];
        newCityArray[2] = oldCityArray[2];

        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_choose_address);
        tvCancel = (TextView) window.findViewById(R.id.tvCancel);
        tvSure = (TextView) window.findViewById(R.id.tvSure);
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        npProvince = (NumberPicker) window.findViewById(R.id.npProvince);
        npCity = (NumberPicker) window.findViewById(R.id.npCity);
        npCounty = (NumberPicker) window.findViewById(R.id.npCounty);
        setNomal();
        String[] provinceArray = new String[bean.getData().size()];
        for (int i = 0; i < provinceArray.length; i++) {
            provinceArray[i] = bean.getData().get(i).getName();
        }
        npProvince.setDisplayedValues(provinceArray);
        npProvince.setMinValue(0);
        npProvince.setMaxValue(provinceArray.length - 1);
        for (int i = 0; i < provinceArray.length; i++) {
            if (provinceArray[i].equals(newCityArray[0])) {
                npProvince.setValue(i);
                changeCity(i);
            }
        }
    }

    private void changeCity(int provinceTag) {
        List<CityBean.Data.City> cityList = bean.getData().get(provinceTag).getCity();
        String[] cityArray = new String[cityList.size()];
        for (int i = 0; i < cityArray.length; i++) {
            cityArray[i] = cityList.get(i).getName();
        }
        try {
            npCity.setMinValue(0);
            npCity.setMaxValue(cityArray.length - 1);
            npCity.setWrapSelectorWheel(false);
            npCity.setDisplayedValues(cityArray);
        } catch (Exception e) {
            npCity.setDisplayedValues(cityArray);
            npCity.setMinValue(0);
            npCity.setMaxValue(cityArray.length - 1);
            npCity.setWrapSelectorWheel(false);
        }
        for (int i = 0; i < cityArray.length; i++) {
            if (cityArray[i].equals(newCityArray[1])) {
                npCity.setValue(i);
                changeCounty(provinceTag, i);
                return;
            }
        }
        npCity.setValue(0);
        changeCounty(provinceTag, npCity.getValue());
    }

    private void changeCounty(int provinceTag, int cityTag) {
        List<String> countyList = bean.getData().get(provinceTag).getCity().get(cityTag).getCounty();
        String[] countyArray = new String[countyList.size()];
        for (int i = 0; i < countyArray.length; i++) {
            countyArray[i] = countyList.get(i).toString();
        }
        try {
            npCounty.setMinValue(0);
            npCounty.setMaxValue(countyArray.length - 1);
            npCounty.setWrapSelectorWheel(false);
            npCounty.setDisplayedValues(countyArray);
        } catch (Exception e) {
            npCounty.setDisplayedValues(countyArray);
            npCounty.setMinValue(0);
            npCounty.setMaxValue(countyArray.length - 1);
            npCounty.setWrapSelectorWheel(false);
        }
        for (int i = 0; i < countyArray.length; i++) {
            if (countyArray[i].equals(newCityArray[2])) {
                npCounty.setValue(i);
                return;
            }
        }
        npCounty.setValue(0);
    }

    private void setNomal() {
        npProvince.setOnValueChangedListener(this);
        npCity.setOnValueChangedListener(this);
        npCounty.setOnValueChangedListener(this);
        setNumberPickerDividerColor(npProvince);
        setNumberPickerDividerColor(npCity);
        setNumberPickerDividerColor(npCounty);
        setNumberPickerTextColor(npProvince, context.getResources().getColor(R.color.black));
        setNumberPickerTextColor(npCity, context.getResources().getColor(R.color.black));
        setNumberPickerTextColor(npCounty, context.getResources().getColor(R.color.black));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dialog.dismiss();
                break;
            case R.id.tvSure:
                dialog.dismiss();
                cityInterface.sure(newCityArray);
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.npProvince:
                List<CityBean.Data> dataList = bean.getData();
                newCityArray[0] = dataList.get(npProvince.getValue()).getName();
                changeCity(npProvince.getValue());
                newCityArray[1] = dataList.get(npProvince.getValue()).getCity().get(0).getName();
                newCityArray[2] = dataList.get(npProvince.getValue()).getCity().get(0).getCounty().get(0).toString();
                break;
            case R.id.npCity:
                List<CityBean.Data.City> cityList = bean.getData().get(npProvince.getValue()).getCity();
                newCityArray[1] = cityList.get(npCity.getValue()).getName();
                changeCounty(npProvince.getValue(), npCity.getValue());
                newCityArray[2] = cityList.get(npCity.getValue()).getCounty().get(0).toString();
                break;
            case R.id.npCounty:
                List<String> countyList = bean.getData().get(npProvince.getValue()).getCity().get(npCity.getValue()).getCounty();
                newCityArray[2] = countyList.get(npCounty.getValue()).toString();
                break;
        }
    }

    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, new ColorDrawable(context.getResources().getColor(R.color.mainBlue)));// pf.set(picker, new Div)
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        boolean result = false;
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    result = true;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
