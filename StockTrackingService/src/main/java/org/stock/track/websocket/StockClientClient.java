package org.stock.track.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.stock.track.pojo.CurrentStockValueResponse;
import org.stock.track.pojo.SubscribeResponse;
import org.stock.track.service.WebSocketService;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

@Component
public class StockClientClient extends WebSocketClient {

    private static final Log logger = LogFactory.getLog(StockClientClient.class);

    @Value("#{'${defaultStocks}'.split(',')}")
    private List<String> defaultStockList;

    private Map<String, CurrentStockValueResponse> cache = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public StockClientClient(URI serverURI) {
        super(serverURI);
    }

    private void subscribe(String stockSymbol) {
        logger.info("subscribing to " + stockSymbol);
        if (isOpen()) {
            String bt = WebSocketService.getSubscribeMessage(stockSymbol).toString();
            send(bt);
            logger.info(stockSymbol + " subscribed");
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("new connection opened");
        defaultStockList.forEach(this::subscribe);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        JSONObject response = new JSONObject(message);
        Map<String, CurrentStockValueResponse> responseList = new TreeMap<>();
        if (response.has("data")) {
            JSONArray tradeList = response.getJSONArray("data");
            for (int i = 0; i < tradeList.length(); i++) {
                JSONObject stock = tradeList.getJSONObject(i);
                BigDecimal lastPrice = stock.getBigDecimal("p");
                String stockSymbol = stock.getString("s");
                long timestamp = stock.getLong("t");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                CurrentStockValueResponse rs = new CurrentStockValueResponse(stockSymbol, lastPrice, timestamp);
                logger.info(stockSymbol + " at price " + lastPrice + " at " + calendar.getTime());
                responseList.put(stockSymbol, rs);
                cache.put(stockSymbol, rs);
            }
            for (String symbol : defaultStockList) {
                if (!responseList.containsKey(symbol)) {
                    if (cache.containsKey(symbol)) {
                        responseList.put(symbol, cache.get(symbol));
                    } else {
                        CurrentStockValueResponse value = new CurrentStockValueResponse();
                        value.setSymbol(symbol);
                        responseList.put(symbol, value);
                    }
                }
            }
            simpMessagingTemplate.convertAndSend("/topic/updateService", responseList.values());
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        logger.error("an error occurred:" + ex);
    }
}
