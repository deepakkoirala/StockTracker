package org.stock.track.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.stock.track.pojo.CurrentStockValueResponse;
import org.stock.track.pojo.SubscribeResponse;
import org.stock.track.service.WebSocketService;

import java.util.Collection;

@RestController
public class StockTrackerController {
    private static final Log logger = LogFactory.getLog(StockTrackerController.class);

    @Autowired
    WebSocketService webSocketService;

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
}
