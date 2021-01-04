package org.stock.track.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stock.track.pojo.CurrentStockValueResponse;
import org.stock.track.pojo.SubscribeResponse;
import org.stock.track.websocket.StockClientClient;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class WebSocketService {
    private static final Log logger = LogFactory.getLog(WebSocketService.class);

    @Autowired
    private StockClientClient stockClientClient;

    @PostConstruct
    private void connectSocket() {
        if (!stockClientClient.isOpen()) {
            stockClientClient.connect();
        }
    }

    public SubscribeResponse subscribe(String stockSymbol) {
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        if (stockSymbol == null) {
            subscribeResponse.setSuccess(false);
            return subscribeResponse;
        }
        stockSymbol = stockSymbol.toUpperCase();

        logger.info("subscribing to " + stockSymbol);

        if (stockClientClient.isOpen()) {
            String bt = getSubscribeMessage(stockSymbol).toString();
            System.out.println(bt);
            stockClientClient.send(bt);
            logger.info(stockSymbol + " subscribed");
            subscribeResponse.setSuccess(true);
            stockClientClient.onSubscribe(stockSymbol);
        } else subscribeResponse.setSuccess(false);

        return subscribeResponse;
    }

    public SubscribeResponse unsubscribe(String stockSymbol) {
        logger.info("unsubscribing to " + stockSymbol);
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        if (stockSymbol == null) {
            subscribeResponse.setSuccess(false);
            return subscribeResponse;
        }
        stockSymbol = stockSymbol.toUpperCase();
        if (stockClientClient.isOpen()) {
            String bt = getUnSubscribeMessage(stockSymbol).toString();
            System.out.println(bt);
            stockClientClient.send(bt);
            logger.info(stockSymbol + " unsubscribed");
            subscribeResponse.setSuccess(true);
            stockClientClient.onUnSubscribe(stockSymbol);
        } else subscribeResponse.setSuccess(false);

        return subscribeResponse;
    }


    public static JSONObject getSubscribeMessage(String symbol) {
        JSONObject obj = new JSONObject();
        obj.put("type", "subscribe");
        obj.put("symbol", symbol);
        return obj;
    }

    public static JSONObject getUnSubscribeMessage(String symbol) {
        JSONObject obj = new JSONObject();
        obj.put("type", "unsubscribe");
        obj.put("symbol", symbol);
        return obj;
    }

    public Collection<CurrentStockValueResponse> getAllSubscribedStocks() {
        return stockClientClient.getAllSubscribedStocks();
    }

    public SubscribeResponse unsubscribeAll() {
        return stockClientClient.unsubscribeAll();
    }

    public SubscribeResponse reset() {
        SubscribeResponse subscribeResponse = stockClientClient.unsubscribeAll();
        if (subscribeResponse.getSuccess())
            stockClientClient.subscribeToAllDefaultList();
        return subscribeResponse;
    }

    public SubscribeResponse propagateSetting(JSONObject settings) {
        return stockClientClient.propagateSetting(settings);
    }
}
