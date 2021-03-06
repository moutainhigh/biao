package com.biao.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * offline资产转入转出
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperTransferVO implements Serializable {

    private String coinId;
    private String symbol;
    private BigDecimal volume;

}
