package org.stock.track.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import org.stock.track.pojo.CurrentStockValueResponse;
import org.stock.track.pojo.SubscribeResponse;
import org.stock.track.service.WebSocketService;

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@RestController
public class StockTrackerController {
    private static final Log logger = LogFactory.getLog(StockTrackerController.class);

    @Autowired
    WebSocketService webSocketService;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String redirect(ServletResponse response) {
        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        ((HttpServletResponse) response).setHeader("Location", "/admin/index.html");
        return null;
    }

    @RequestMapping("/stock-track/subscribe/{stockSymbol}")
    @ResponseBody
    public SubscribeResponse subscribe(@PathVariable(value = "stockSymbol") String stockSymbol) {
        logger.info("subscribing to symbol " + stockSymbol);
        return webSocketService.subscribe(stockSymbol);
    }

    @RequestMapping("/stock-track/unsubscribe/{stockSymbol}")
    @ResponseBody
    public SubscribeResponse unsubscribe(@PathVariable(value = "stockSymbol") String stockSymbol) {
        logger.info("unsubscribing to symbol " + stockSymbol);
        return webSocketService.unsubscribe(stockSymbol);
    }

    @RequestMapping("/stock-track/getAllSubscribedStocks")
    @ResponseBody
    public Collection<CurrentStockValueResponse> getAllSubscribedStocks() {
        return webSocketService.getAllSubscribedStocks();
    }

    @RequestMapping("/stock-track/unsubscribeAll")
    @ResponseBody
    public SubscribeResponse unsubscribeAll() {
        return webSocketService.unsubscribeAll();
    }

    /***
     * Not used
     */
    @MessageMapping("/getAllSubscribedStocks")
    @SendTo("/topic/updateService")
    public Collection<CurrentStockValueResponse> getAllSubscribedStocks(@Payload String message) {
        return webSocketService.getAllSubscribedStocks();
    }

    @SubscribeMapping("/updateService")
    public Collection<CurrentStockValueResponse> sendAllSubscribedStocks() {
        return webSocketService.getAllSubscribedStocks();
    }

    @RequestMapping("/stock-track/resetStockSubscription")
    @ResponseBody
    public SubscribeResponse resetStockSubscription() {
        return webSocketService.reset();
    }

    @PostMapping("/stock-track/setSetting")
    @ResponseBody
    public SubscribeResponse setSetting(@RequestBody Map<String,Object> setting) {
        return webSocketService.propagateSetting(setting);
    }

    @SubscribeMapping("/stock-track/getSettings")
    @ResponseBody
    public Map<String,Object> getSettings() {
        return webSocketService.getSettings();
    }
}
