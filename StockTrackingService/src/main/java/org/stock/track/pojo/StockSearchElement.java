package org.stock.track.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StockSearchElement {
    private String description;
    private String displaySymbol;
    private String symbol;
    private String type;
}
