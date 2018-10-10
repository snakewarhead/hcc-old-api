package com.puli.service.wallet;

import com.puli.utils.Utils;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

/**
 * Created by lin on 2018/6/26.
 */
public abstract class WalletRPC {

    private static final String DIR_CONFIG = "wallet_config";

    protected WalletParams walletParams;

    public WalletRPC(WalletType type) throws Exception {
        WalletParams params = new WalletParams();

        // 加载配置文件
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(Utils.getWebInfPath() + DIR_CONFIG + "/" + type.name() + ".properties");
//        FileInputStream in = new FileInputStream(Utils.getWebInfPath() + DIR_CONFIG + "/" + type.name() + "_test.properties");
        properties.load(in);
        in.close();

        params.ENABLED = Boolean.parseBoolean(properties.getProperty("ENABLED"));
        params.ACCESS_KEY = properties.getProperty("ACCESS_KEY");
        params.SECRET_KEY = properties.getProperty("SECRET_KEY");
        params.PASSWORD = properties.getProperty("PASSWORD");
        params.IP = properties.getProperty("IP");
        params.PORT = properties.getProperty("PORT");

        this.walletParams = params;
    }

    public boolean isEanble() {
        return walletParams.ENABLED;
    }

    public abstract String getNewAddress(String usrID) throws Exception;

    public abstract BigDecimal getBalance() throws Exception;

    public abstract String sendTo(String address,BigDecimal amount,BigDecimal fees,String comment) throws Exception;

    public abstract List<WalletTransaction> listTransactions(int count,int from) throws Exception;

    public abstract WalletTransaction getTransaction(String txid, String address,String category) throws Exception;
}
