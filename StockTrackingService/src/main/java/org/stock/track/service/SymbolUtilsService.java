package org.stock.track.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stock.track.pojo.CurrentProgress;
import org.stock.track.pojo.CurrentStockValueResponse;

@Service
public class SymbolUtilsService {
    @Value("${symbolLabelFilter}")
    private String symbolLabelFilter;

    private Map<String, String> getSymbolLabelFilter() {
        JSONObject jObject = new JSONObject(this.symbolLabelFilter);
        Iterator<?> keys = jObject.keys();
        HashMap<String, String> symMap = new HashMap<String, String>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            symMap.put(key, value);

        }
        return symMap;
    }

    public CurrentStockValueResponse CreateSymbolResponseObject(String symbolName, BigDecimal lastPrice, Long timestamp,
            CurrentProgress progress) {
        String symbol = this.getReplacedSymbolname(symbolName);
        if (symbolName.contains("BINANCE:")) {
            return new CurrentStockValueResponse(symbol, symbolName, lastPrice, timestamp, progress, "CRYPTO");
        } else
            return new CurrentStockValueResponse(symbol, symbolName, lastPrice, timestamp, progress, "STOCK");
    }

    private String getReplacedSymbolname(String symbol) {
        for (Entry<String, String> entry : this.getSymbolLabelFilter().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            symbol = symbol.replace(key, value.toString());
        }
        return symbol;
    }
}
