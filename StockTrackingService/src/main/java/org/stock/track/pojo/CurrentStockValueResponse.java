package org.stock.track.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStockValueResponse {
    private String symbol;
    private BigDecimal price;
    private long timestamp;
}
