package com.puli.controller;

import com.puli.exception.RPCCallingException;
import com.puli.service.WalletManager;
import com.puli.service.wallet.WalletRPC;
import com.puli.service.wallet.WalletTransaction;
import com.puli.utils.Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lin on 2018/6/26.
 */
@Controller
@RequestMapping(value = "/api_v1")
public class WalletAPIController {

    private static final Log LOG = LogFactory.getLog(WalletAPIController.class);

    @Autowired
    private WalletManager walletManager;

    @RequestMapping("/getnewaddress")
    @ResponseBody
    public String getNewAddress(
            @RequestParam(required = false, defaultValue = "2") int walletType,
            @RequestParam(required = false, defaultValue = "") String userID
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            WalletRPC rpc = walletManager.get(walletType);
            if (StringUtils.isEmpty(userID)) {
                userID = Utils.getDatetimeStr();
            }
            String address = rpc.getNewAddress(userID);
            if (StringUtils.isEmpty(address)) {
                throw new Exception();
            }
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, address);
        } catch (RPCCallingException e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, e.getMessage());
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");
        } catch (Exception e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "Calling Exception in somewhere! please contact us :)");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");

            LOG.error("getnewaddress" + e.getMessage(), e);
        }
        return jsonObject.toString();
    }

    @RequestMapping("/getbalance")
    @ResponseBody
    public String getBalance(
            @RequestParam(required = false, defaultValue = "2") int walletType
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            WalletRPC rpc = walletManager.get(walletType);
            BigDecimal balance = rpc.getBalance();
            if (balance.compareTo(new BigDecimal(-1)) == 0) {
                throw new Exception();
            }

            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, balance.toPlainString());
        } catch (RPCCallingException e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, e.getMessage());
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");
        } catch (Exception e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "Calling Exception in somewhere! please contact us :)");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");

            LOG.error("getbalance" + e.getMessage(), e);
        }
        return jsonObject.toString();
    }

    @RequestMapping("/sendtoaddress")
    @ResponseBody
    public String sendToAddress(
            @RequestParam(required = false, defaultValue = "2") int walletType,
            @RequestParam(required = true) String address,
            @RequestParam(required = true) BigDecimal amount,
            @RequestParam(required = false, defaultValue = "") String comment
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            WalletRPC rpc = walletManager.get(walletType);
            String txid = rpc.sendTo(address, amount, BigDecimal.ZERO, comment);
            if (StringUtils.isEmpty(txid)) {
                throw new RPCCallingException("Please check parameters, or whether balance is enough");
            }

            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, txid);
        } catch (RPCCallingException e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, e.getMessage());
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");
        } catch (Exception e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "Calling Exception in somewhere! please contact us :)");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");

            LOG.error("sendtoaddress" + e.getMessage(), e);
        }
        return jsonObject.toString();
    }

    @RequestMapping("/listtransactions")
    @ResponseBody
    public String listTransactions(
            @RequestParam(required = false, defaultValue = "2") int walletType,
            @RequestParam(required = true) int count,
            @RequestParam(required = true) int skip
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            WalletRPC rpc = walletManager.get(walletType);
            List<WalletTransaction> ls = rpc.listTransactions(count, skip);
            if (ls == null) {
                throw new Exception();
            }

            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, JSONArray.fromObject(ls));
        } catch (RPCCallingException e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, e.getMessage());
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");
        } catch (Exception e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "Calling Exception in somewhere! please contact us :)");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");

            LOG.error("listTransactions" + e.getMessage(), e);
        }
        return jsonObject.toString();
    }

    @RequestMapping("/gettransaction")
    @ResponseBody
    public String getTransaction(
            @RequestParam(required = false, defaultValue = "2") int walletType,
            @RequestParam(required = true) String txid,
            @RequestParam(required = true) String address,
            @RequestParam(required = false, defaultValue = "receive") String category
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            WalletRPC rpc = walletManager.get(walletType);
            WalletTransaction tx = rpc.getTransaction(txid, address, category);
            if (tx == null) {
                throw new RPCCallingException("Please check parameters");
            }

            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, JSONObject.fromObject(tx));
        } catch (RPCCallingException e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, e.getMessage());
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");
        } catch (Exception e) {
            jsonObject.accumulate(ControllerConstant.TAG_ERROR, "Calling Exception in somewhere! please contact us :)");
            jsonObject.accumulate(ControllerConstant.TAG_RESULT, "");

            LOG.error("getTransaction" + e.getMessage(), e);
        }
        return jsonObject.toString();
    }
}
