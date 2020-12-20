package org.stock.track.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stock.track.config.Config;
import org.stock.track.pojo.SubscribeResponse;
import org.stock.track.websocket.StockClientClient;

import javax.annotation.PostConstruct;

@Service
public class WebSocketService {
    private static final Log logger = LogFactory.getLog(WebSocketService.class);

    @Autowired
    private StockClientClient stockClientClient;

    @PostConstruct
    private void connectSocket() {
        if (!stockClientClient.isOpen())
            stockClientClient.connect();
    }

    public SubscribeResponse subscribe(String stockSymbol) {
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        if (stockClientClient.isOpen()) {
            String bt = getSubscribeMessage(stockSymbol).toString();
            System.out.println(bt);
            stockClientClient.send(bt);
            logger.info(stockSymbol + " subscribed");
            subscribeResponse.setSuccess(true);
        } else subscribeResponse.setSuccess(false);

        return subscribeResponse;
    }

    public SubscribeResponse unsubscribe(String stockSymbol) {
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        if (stockClientClient.isOpen()) {
            String bt = getUnSubscribeMessage(stockSymbol).toString();
            System.out.println(bt);
            stockClientClient.send(bt);
            logger.info(stockSymbol + " unsubscribed");
            subscribeResponse.setSuccess(true);
        } else subscribeResponse.setSuccess(false);
        
        return subscribeResponse;
    }

    private JSONObject getSubscribeMessage(String symbol) {
        JSONObject obj = new JSONObject();
        obj.put("type", "subscribe");
        obj.put("symbol", symbol);
        return obj;
    }

    private JSONObject getUnSubscribeMessage(String symbol) {
        JSONObject obj = new JSONObject();
        obj.put("type", "unsubscribe");
        obj.put("symbol", symbol);
        return obj;
    }
}
