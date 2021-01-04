package org.stock.track.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStockValueResponse {
    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("MM-dd hh:mm:ssaaa");
    private String symbol;
    private BigDecimal price;
    private long timestamp;
    private CurrentProgress currentProgress;

    public CurrentStockValueResponse(String symbol, CurrentProgress progress) {
        this.symbol = symbol;
        this.currentProgress = progress;
    }

    public String getTimeString() {
        FORMAT.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        return FORMAT.format(new Date(timestamp));
    }
}
