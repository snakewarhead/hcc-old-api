package com.puli.service.wallet;

/**
 * Created by lin on 2018/6/26.
 */
public enum WalletType {

    BTC(0, "BTC钱包"),
    ETH(1, "ETH钱包"),
    HCC(2, "HCC钱包");

    private int type;
    private String desc;

    WalletType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int type() {
        return type;
    }

    public String desc() {
        return desc;
    }

    public boolean is(int type) {
        return this.type() == type;
    }

    public static WalletType getEnum(int type) {
        WalletType resultEnum = null;
        WalletType[] enumAry = WalletType.values();
        for (int i = 0; i < enumAry.length; i++) {
            if (type == enumAry[i].type) {
                resultEnum = enumAry[i];
                break;
            }
        }
        return resultEnum;
    }
}
