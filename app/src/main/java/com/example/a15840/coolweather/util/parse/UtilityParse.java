package com.example.a15840.coolweather.util.parse;

import android.text.StaticLayout;
import android.text.TextUtils;

import com.example.a15840.coolweather.db.model.Province;
import com.example.a15840.coolweather.db.model.City;
import com.example.a15840.coolweather.db.model.Country;
import com.example.a15840.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 15840 on 2018/1/2.
 *
 */
/**
 * 解析服务器返回的省级数据,可以用抽象方法工厂模式实现
 */
public class UtilityParse {
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
               for (int i =0;i<allProvinces.length();i++){
                   JSONObject provinceObject = allProvinces.getJSONObject(i);
                   Province province = new Province();
                   province.setProvinceName(provinceObject.getString("name"));
                   province.setProvinceCode(provinceObject.getInt("id"));
                   province.save();
               }
               return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountryResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountries = new JSONArray(response);
                for (int i=0;i<allCountries.length();i++){
                    JSONObject countryObject = allCountries.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(countryObject.getString("name"));
                    country.setCountryCode(countryObject.getInt("id"));
                    country.setWeatherId(countryObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * @param response
     * @return
     * 解析服务器返回的json数据
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            /**
                获取转为全局文本数据的json数据后使之与Weather类进行映射
             */
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
