package com.puli.service;

import com.puli.exception.RPCCallingException;
import com.puli.service.wallet.WalletRPC;
import com.puli.service.wallet.WalletType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by lin on 2018/6/27.
 */
@Service
public class WalletManager {

    private static final Log LOG = LogFactory.getLog(WalletManager.class);

    private Map<WalletType, WalletRPC> rpcs;

    public WalletManager() {
        LOG.info("WalletManager inited------------------------");
    }

    public Map<WalletType, WalletRPC> getRpcs() {
        return rpcs;
    }

    public void setRpcs(Map<WalletType, WalletRPC> rpcs) {
        this.rpcs = rpcs;
    }

    public WalletRPC get(int type) {
        WalletType wtype = WalletType.getEnum(type);
        if (wtype == null) {
            throw new RPCCallingException("walletType is illegal");
        }
        return get(wtype);
    }

    public WalletRPC get(WalletType type) {
        WalletRPC rpc = rpcs.get(type);
        if (rpc == null || !rpc.isEanble()) {
            throw new RPCCallingException("this rpc is disabled");
        }
        return rpc;
    }
}
