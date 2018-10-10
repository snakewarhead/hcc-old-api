package com.puli.service.wallet;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by lin on 2018/6/27.
 */
public class WalletTransaction {
    private String account;// 帐户，USERID

    private String fromAddress;// 充从地址
    private String address;// 充向地址
    private String category;// 类型，receive OR SEND
    private BigDecimal amount= BigDecimal.ZERO;// 数量
    private int confirmations;// 确认数
    private String txid;// 交易ID
    private long time;// 时间
    private String comment;// 备注
    private long blockNumber;// 备注

    @Override
    public String toString() {
        return "from:" + fromAddress +
                ", to:" + address +
                ", amount:" + amount +
                ", txid:" + txid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
}
