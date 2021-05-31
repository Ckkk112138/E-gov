package com.example.e_gov;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import  com.baidu.mapapi.overlayutil.BusLineOverlay;

import java.util.ArrayList;
import java.util.List;



public class Map extends FragmentActivity implements OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener
        , BaiduMap.OnMapClickListener {
    private Button btnPre;
    private Button btnNext;
    private int nodeIndex = -2; // index for node

    private BusLineResult route = null;
    private List<String> busLineIDList = null;
    private int busLineIndex = 0;

    private PoiSearch mSearch = null;

    private BusLineSearch mBusLineSearch = null;
    private BaiduMap mBaiduMap = null;
    private MapView mapView;


    BusLineOverlay overlay;  // object to draw route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        CharSequence titileLable = "Bus Line Search";
        setTitle(titileLable);

        btnPre = findViewById(R.id.pre);
        btnNext = findViewById(R.id.next);
        btnPre.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);

        mapView = (MapView)findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();

        mBaiduMap.setOnMapClickListener(this);

        mSearch = PoiSearch.newInstance();
        mSearch.setOnGetPoiSearchResultListener(this);

        mBusLineSearch = BusLineSearch.newInstance();
        mBusLineSearch.setOnGetBusLineSearchResultListener(this);
        busLineIDList = new ArrayList<String>();

        overlay = new BusLineOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(overlay);



    }

    public void searchButtonProc(View v)
    {
        busLineIDList.clear();
        busLineIndex = 0;
        btnPre.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        EditText editCity = (EditText)findViewById(R.id.city);
        EditText editSearchKey = (EditText)findViewById(R.id.searchkey);

        mSearch.searchInCity((new PoiCitySearchOption()).city(editCity.getText().toString())
                .keyword(editSearchKey.getText().toString()).scope(2));
    }

    public void searchNextBusLine(View v)
    {
        if(busLineIndex >= busLineIDList.size())
        {
            busLineIndex = 0;
        }
        if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
                && busLineIDList.size() > 0)
        {
            mBusLineSearch.searchBusLine((new BusLineSearchOption()
                    .city(((EditText)findViewById(R.id.city)).getText().toString())
                    .uid(busLineIDList.get(busLineIndex))));
            busLineIndex++;
        }
    }

    public void nodeClick(View view)
    {
        if(nodeIndex < -1 || route == null || nodeIndex >= route.getStations().size())
        {
            return;
        }
        TextView popupText = new TextView(this);
        popupText.setTextColor(0xff000000);

        if(btnPre.equals(view) && nodeIndex > 0)
            nodeIndex--;
        if(btnNext.equals(view) && nodeIndex < (route.getStations().size() - 1))
        {
            nodeIndex++;
        }
        if(nodeIndex >= 0)
        {
            // move to index coordinate
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(route
                    .getStations().get(nodeIndex).getLocation()));
            // pop up bubble
            popupText.setText(route.getStations().get(nodeIndex).getTitle());
            mBaiduMap.showInfoWindow(new InfoWindow(popupText,route.getStations()
                    .get(nodeIndex).getLocation(),0));
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        mSearch.destroy();
        mBusLineSearch.destroy();
        mapView.onDestroy();

    }

    @Override
    public void onMapClick(LatLng latLng) {

        mBaiduMap.hideInfoWindow();
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public void onGetBusLineResult(BusLineResult busLineResult) {
        if(busLineResult == null || busLineResult.error != SearchResult.ERRORNO.NO_ERROR)
        {
            Toast.makeText(Map.this,"No result found,GR",Toast.LENGTH_SHORT).show();
            return;
        }
        mBaiduMap.clear();
        route = busLineResult;
        nodeIndex = -1;

        overlay.removeFromMap();
        overlay.setData(busLineResult);
        overlay.addToMap();
        overlay.zoomToSpan();

        btnPre.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        Toast.makeText(Map.this,busLineResult.getBusLineName(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {

        if(poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR)
        {
            Toast.makeText(Map.this, "No result found",Toast.LENGTH_SHORT).show();
            return;
        }
        // check poi find poi belongs to bus
        busLineIDList.clear();
        for (PoiInfo poiInfo : poiResult.getAllPoi())
        {
            busLineIDList.add(poiInfo.uid);
        }
        searchNextBusLine(null);
        route = null;
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
