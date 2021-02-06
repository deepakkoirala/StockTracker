package org.stock.track.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stock.track.pojo.CurrentProgress;
import org.stock.track.pojo.CurrentStockValueResponse;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SymbolUtilsService {
    public static final String DEFAULT_TYPE = "STOCK";

    @Value("${symbolLabelFilter}")
    private String symbolLabelFilter;

    @Value("${typeFilter}")
    private String typeFilter;

    private HashMap<String, String> symbolLabelFilterMap;
    private HashMap<String, Set<String>> typeFilterMap;

    @PostConstruct
    private void setFilters() {
        initSymbolFilter();
        initTypeFilter();
    }

    private void initTypeFilter() {
        typeFilterMap = new HashMap<>();
        JSONArray jArr = new JSONArray(this.typeFilter);
        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jObj = jArr.getJSONObject(i);
            JSONArray jArrValues = jObj.getJSONArray("values");
            HashSet<String> values = new HashSet();
            for (int j = 0; j < jArrValues.length(); j++) {
                values.add(jArrValues.getString(j));
            }
            typeFilterMap.put(jObj.getString("type"), values);
        }
    }

    private void initSymbolFilter() {
        symbolLabelFilterMap = new HashMap<>();
        JSONObject jObject = new JSONObject(this.symbolLabelFilter);
        Iterator<?> keys = jObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            symbolLabelFilterMap.put(key, value);
        }
    }

    public CurrentStockValueResponse createSymbolResponseObject(String symbolName, BigDecimal lastPrice, Long timestamp,
                                                                CurrentProgress progress) {
        String symbol = this.getReplacedSymbolName(symbolName);
        String type = this.getSymbolType(symbolName);
        return new CurrentStockValueResponse(symbol, symbolName, lastPrice, timestamp, progress, type);
    }

    private String getReplacedSymbolName(String symbol) {
        for (Map.Entry<String, String> e : symbolLabelFilterMap.entrySet()) {
            symbol = symbol.replace(e.getKey(), e.getValue());
        }
        return symbol;
    }

    private String getSymbolType(String symbol) {
        for(Map.Entry<String, Set<String>> e:typeFilterMap.entrySet()){
            if(e.getValue().contains(symbol)){
                return e.getKey();
            }
        }
        return DEFAULT_TYPE;
    }
}
