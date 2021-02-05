package org.stock.track.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;
import org.stock.track.pojo.CurrentProgress;
import org.stock.track.pojo.CurrentStockValueResponse;
import org.stock.track.pojo.SubscribeResponse;
import org.stock.track.service.SymbolUtilsService;
import org.stock.track.service.WebSocketService;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

@Component
@PropertySource("classpath:application.properties")
@ApplicationScope
public class StockClientClient extends WebSocketClient {

    private static final Log logger = LogFactory.getLog(StockClientClient.class);

    @Value("#{'${defaultStocks}'.split(',')}")
    private List<String> defaultStockList;

    @Autowired
    private SymbolUtilsService symbolutilsservice;

    private final Map<String, CurrentStockValueResponse> cache = Collections.synchronizedSortedMap(new TreeMap<>());

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
            cache.put(stockSymbol, new CurrentStockValueResponse(stockSymbol, CurrentProgress.NEW));
            logger.info(stockSymbol + " subscribed");
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("new connection opened");
        subscribeToAllDefaultList();
    }

    public void subscribeToAllDefaultList() {
        defaultStockList.forEach(this::subscribe);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("closed with exit code " + code + " additional info: " + reason);
        try {
            logger.info("Reconnecting in 5 Seconds.");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (code != CloseFrame.NORMAL) {// reconnect only for abnormal closures
            new Thread(this::reconnect).start();
        }
    }

    @Override
    public void onMessage(String message) {
        JSONObject response = new JSONObject(message);
        Map<String, CurrentStockValueResponse> responseMap = new TreeMap<>();
        if (response.has("data")) {
            JSONArray tradeList = response.getJSONArray("data");
            for (int i = 0; i < tradeList.length(); i++) {
                JSONObject stock = tradeList.getJSONObject(i);
                BigDecimal lastPrice = stock.getBigDecimal("p");
                String stockSymbol = stock.getString("s");
                long timestamp = stock.getLong("t");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                CurrentProgress progress = null;

                if (cache.containsKey(stockSymbol)) {
                    final CurrentStockValueResponse currentStockValueResponse = cache.get(stockSymbol);
                    BigDecimal previousPrice = currentStockValueResponse.getPrice();
                    if (previousPrice == null) {
                        progress = CurrentProgress.NEW;
                    } else {
                        switch (previousPrice.compareTo(lastPrice)) {
                            case 1:
                                progress = CurrentProgress.DECREASING;
                                break;
                            case -1:
                                progress = CurrentProgress.INCREASING;
                                break;
                            default:
                                progress = currentStockValueResponse.getCurrentProgress();
                        }
                    }
                }
                CurrentStockValueResponse rs = symbolutilsservice.createSymbolResponseObject(stockSymbol, lastPrice, timestamp, progress);
                logger.debug(stockSymbol + " at price " + lastPrice + " at " + calendar.getTime());
                responseMap.put(stockSymbol, rs);
                synchronized (cache) {
                    if (cache.containsKey(stockSymbol))
                        cache.put(stockSymbol, rs);
                }
            }
            for (Map.Entry<String, CurrentStockValueResponse> entry : cache.entrySet()) {
                String symbol = entry.getKey();
                if (!responseMap.containsKey(symbol)) {
                    responseMap.put(symbol, entry.getValue());
                }
            }
            if (!cache.isEmpty())
                simpMessagingTemplate.convertAndSend("/topic/updateService", responseMap.values());
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        logger.debug("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        logger.error("an error occurred:" + ex);
    }

    public void onSubscribe(String stockSymbol) {
        cache.put(stockSymbol, new CurrentStockValueResponse(stockSymbol, CurrentProgress.NEW));
        simpMessagingTemplate.convertAndSend("/topic/updateService", cache.values());
    }

    public void onUnSubscribe(String stockSymbol) {
        cache.remove(stockSymbol);
        simpMessagingTemplate.convertAndSend("/topic/updateService", cache.values());
    }

    public Collection<CurrentStockValueResponse> getAllSubscribedStocks() {
        return cache.values();
    }

    public SubscribeResponse unsubscribeAll() {
        logger.info("unsubscribing to all");
        SubscribeResponse subscribeResponse = new SubscribeResponse();

        if (this.isOpen()) {
            Set<String> set = new HashSet<String>(cache.keySet());
            set.forEach(this::unsubscribe);
            subscribeResponse.setSuccess(true);
            synchronized (cache) {
                while (!cache.isEmpty())
                    cache.clear();
                simpMessagingTemplate.convertAndSend("/topic/updateService", new HashSet<>());
            }
        } else subscribeResponse.setSuccess(false);

        return subscribeResponse;
    }

    private void unsubscribe(String stockSymbol) {
        logger.debug("unsubscribing to " + stockSymbol);
        String bt = WebSocketService.getUnSubscribeMessage(stockSymbol).toString();
        send(bt);
        cache.remove(stockSymbol);
        logger.debug(stockSymbol + " unsubscribed");
    }

    public SubscribeResponse propagateSetting(Map<String,Object> settings) {
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        try {
            simpMessagingTemplate.convertAndSend("/topic/getSettings", settings);
            subscribeResponse.setSuccess(true);
        } catch (MessagingException e) {
            subscribeResponse.setSuccess(false);
        }
        return subscribeResponse;
    }
}
