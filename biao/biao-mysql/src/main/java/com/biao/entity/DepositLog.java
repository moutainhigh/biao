package com.biao.entity;

import com.biao.sql.SqlField;
import com.biao.sql.SqlTable;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 用户coin充值记录
 */
@SqlTable("js_plat_user_deposit_log")
public class DepositLog extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @SqlField("user_id")
    private String userId;
    @SqlField("coin_id")
    private String coinId;
    @SqlField("coin_type")
    private String coinType;
    @SqlField("status")
    private Integer status;
    @SqlField("raise_status")
    private Integer raiseStatus;
    @SqlField("address")
    private String address;
    @SqlField("tx_id")
    private String txId;
    @SqlField("volume")
    private BigDecimal volume;
    @SqlField("coin_symbol")
    private String coinSymbol;
    @SqlField("confirms")
    private BigInteger confirms;
    @SqlField("block_number")
    private BigInteger blockNumber;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public BigInteger getConfirms() {
        return confirms;
    }

    public void setConfirms(BigInteger confirms) {
        this.confirms = confirms;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Integer getRaiseStatus() {
        return raiseStatus;
    }

    public void setRaiseStatus(Integer raiseStatus) {
        this.raiseStatus = raiseStatus;
    }

    @Override
    public String toString() {
        return "DepositLog{" +
                "userId='" + userId + '\'' +
                ", coinId='" + coinId + '\'' +
                ", coinType='" + coinType + '\'' +
                ", status=" + status +
                ", raiseStatus=" + raiseStatus +
                ", address='" + address + '\'' +
                ", txId='" + txId + '\'' +
                ", volume=" + volume +
                ", coinSymbol='" + coinSymbol + '\'' +
                ", confirms=" + confirms +
                ", blockNumber=" + blockNumber +
                '}';
    }
}
