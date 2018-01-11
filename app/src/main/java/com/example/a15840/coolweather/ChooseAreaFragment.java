package com.example.a15840.coolweather;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a15840.coolweather.activity.WeatherActivity;
import com.example.a15840.coolweather.db.model.Province;
import com.example.a15840.coolweather.application.MyApplication;
import com.example.a15840.coolweather.db.model.City;
import com.example.a15840.coolweather.db.model.Country;
import com.example.a15840.coolweather.util.http.HttpUtil;
import com.example.a15840.coolweather.util.parse.UtilityParse;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    private int currentLevel;
    private TextView title_text;
    private Button back_button;
    private View view;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<>();
    private Province selectProvince;
    private City selectedCity;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    private ProgressDialog progressDialog;
    public ChooseAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        title_text = view.findViewById(R.id.title_text);
        back_button = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(arrayAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                } else if (currentLevel == LEVEL_COUNTRY) {
                    String weatherId = countryList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTRY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
    }
    private void queryProvinces(){
        title_text.setText(R.string.china);
        back_button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
                listView.setSelection(0);
                currentLevel = LEVEL_PROVINCE;
            }
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }

    }
    private void queryCities(){
        title_text.setText(selectProvince.getProvinceName());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectProvince.getId())).find(City.class);
        Log.e(TAG, cityList.toString());
        Log.e(TAG, String.valueOf(selectProvince.getId()));
        if (cityList.size() >0) {
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }
    private void queryCountries(){
        title_text.setText(selectedCity.getCityName());
        back_button.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid=?",
                String.valueOf(selectedCity.getId())).find(Country.class);
        Log.e(TAG, countryList.toString());
        if (countryList.size() > 0) {
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());
                arrayAdapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel = LEVEL_COUNTRY;
            }
        }else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }
    }
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MyApplication.getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = UtilityParse.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = UtilityParse.handleCityResponse(responseText,selectProvince.getId());
                } else if ("country".equals(type)) {
                    result = UtilityParse.handleCountryResponse(responseText, selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("country".equals(type)) {
                                queryCountries();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }
    private void closeProgressDialog(){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
