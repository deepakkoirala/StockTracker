package org.stock.track.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Calendar;

@Component
public class StockClientClient extends WebSocketClient {

    private static final Log logger = LogFactory.getLog(StockClientClient.class);

    @Autowired
    public StockClientClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        JSONObject response = new JSONObject(message);
        if (response.has("data")) {
            JSONArray tradeList = response.getJSONArray("data");
            for (int i = 0; i < tradeList.length(); i++) {
                JSONObject stock = tradeList.getJSONObject(i);
                BigDecimal lastPrice = stock.getBigDecimal("p");
                String stockSymbol = stock.getString("s");
                long timestamp = stock.getLong("t");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                logger.info(stockSymbol + " at price " + lastPrice + " at " + calendar.getTime());
            }
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
