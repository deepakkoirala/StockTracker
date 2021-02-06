package org.stock.track.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStockValueResponse {

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("MM-dd, hh:mm:ssaaa");
    private String symbol;
    private String symbolName;
    private BigDecimal price;
    private long timestamp;
    private CurrentProgress currentProgress;
    private String type;

    public CurrentStockValueResponse(String symbol, CurrentProgress progress) {
        this.symbol = symbol;
        this.currentProgress = progress;
    }


}
