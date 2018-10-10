package com.puli.service.wallet.impl;

import com.puli.service.wallet.WalletRPC;
import com.puli.service.wallet.WalletTransaction;
import com.puli.service.wallet.WalletType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lin on 2018/6/26.
 */
public class WalletRPC_BTCLike extends WalletRPC {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletRPC_BTCLike.class);

    private  String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public WalletRPC_BTCLike(WalletType type) throws Exception {
        super(type);
    }

    @Override
    public String getNewAddress(String userID) throws Exception {
        String result = null;
        if(walletParams.PASSWORD != null && walletParams.PASSWORD.trim().length() >0){
            walletpassphrase(30);
        }
        JSONObject s = getNewaddressForAdmin(userID);
        if(walletParams.PASSWORD != null && walletParams.PASSWORD.trim().length() >0){
            walletlock();
        }
        if(s.containsKey("result")){
            result = s.get("result").toString();
            if(result.equals("null")){
                result = null;
            }
            if(result.contains(":")) {
                result = result.split(":")[1];
            }
        }

        return result;
    }

    @Override
    public BigDecimal getBalance() throws Exception {
        BigDecimal result = new BigDecimal(-1);
        String s = main("getbalance", "[]");
        JSONObject json = JSONObject.fromObject(s);
        if(json.containsKey("result")){
            result =new BigDecimal(json.get("result").toString());
        }
        return result;
    }

    @Override
    public String sendTo(String address, BigDecimal amount, BigDecimal fees, String comment) throws Exception {
        String result = "";
        settxfee(fees);
        JSONObject s = sendtoaddress(address, amount, comment);
        if(s.containsKey("result")){
            if(!s.get("result").toString().equals("null")){
                result = s.get("result").toString();
            }
        }
        return result;
    }

    @Override
    public List<WalletTransaction> listTransactions(int count, int from) throws Exception {
        JSONObject json = listTransactionsRaw(count, from);
        List<WalletTransaction> all = new ArrayList();
        if(json.containsKey("result") && !json.get("result").toString().equals("null")){
            List allResult = (List)json.get("result");
            Iterator it = allResult.iterator();
            while(it.hasNext()){
                Map map = (Map)it.next();
                if(map.get("category").toString().equals("receive")){
                    WalletTransaction info = new WalletTransaction();
                    info.setAccount(map.get("account")+"");
                    info.setAddress(map.get("address")+"");
                    info.setAmount(new BigDecimal(map.get("amount").toString()));	// TODO: 暂时保留6位小数
                    info.setCategory(map.get("category")+"");
                    info.setComment(map.get("comment")+"");
                    try {
                        if(map.get("confirmations") != null
                                && map.get("confirmations").toString().trim().length() >0){
                            info.setConfirmations(Integer.parseInt(map.get("confirmations").toString()));
                        }
                    } catch (Exception e) {
                        info.setConfirmations(0);
                    }
                    try {
                        long time = Long.parseLong(map.get("time").toString());
                        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        java.util.Date dt = new Date(time * 1000);
                        String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
                        info.setTime(Timestamp.valueOf(sDateTime).getTime());
                    } catch (Exception e) {
                        info.setTime(new Date().getTime());
                    }
                    info.setTxid(map.get("txid")+"");
                    all.add(info);
                }
            }
        }
        Collections.reverse(all);
        return all;
    }

    @Override
    public WalletTransaction getTransaction(String txid, String address,String category) throws Exception {
        String s = main("gettransaction", "[\""+txid+"\"]");
        JSONObject json = JSONObject.fromObject(s);

        WalletTransaction info = null ;
        if(json.containsKey("result") && !json.get("result").toString().equals("null")){
            info = new WalletTransaction() ;

            Map map = (Map)json.get("result");
            List xList = (List)map.get("details");
            Iterator it = xList.iterator();
            while(it.hasNext()){
                Map xMap = (Map)it.next();
                if(xMap.get("category").toString().equals(category)){
                    String address2 = xMap.get("address")+"";

                    if(address.equals(address2)){
                        info.setAccount(xMap.get("account")+"");
                        info.setAddress(xMap.get("address")+"");
                        info.setAmount(new BigDecimal(xMap.get("amount").toString()));
                        break;
                    }

                }
            }
            info.setCategory(category);
            info.setComment(map.get("comment")+"");
            try {
                if(map.get("confirmations") != null
                        && map.get("confirmations").toString().trim().length() >0){
                    info.setConfirmations(Integer.parseInt(map.get("confirmations").toString()));
                }
            } catch (Exception e) {
                info.setConfirmations(0);
            }
            try {
                long time = Long.parseLong(map.get("time").toString());
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date dt = new Date(time * 1000);
                String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
                info.setTime(Timestamp.valueOf(sDateTime).getTime());
            } catch (Exception e) {
                info.setTime(new Date().getTime());
            }
            info.setTxid(map.get("txid")+"");
        }
        return info;
    }

    private JSONObject listTransactionsRaw(int count,int from) throws Exception {
        String s = main("listtransactions", "[\"*\","+count+","+from+"]");
        JSONObject json = JSONObject.fromObject(s);
        return json;
    }

    private void settxfee(BigDecimal ffee) throws Exception {
        JSONArray js = new JSONArray();
        js.add(ffee);
        main("settxfee",js.toString());
    }

    private JSONObject sendtoaddress(String address,BigDecimal amount,String comment) throws Exception {
        if(walletParams.PASSWORD != null && walletParams.PASSWORD.trim().length() >0){
            walletpassphrase(30);
        }

        String condition = "[\""+address+"\","+amount+","+"\""+comment+"\"]";

        LOGGER.info("sendtoaddress 1 - " + condition);
        String s = main("sendtoaddress", condition);
        LOGGER.info("sendtoaddress 2 - " + s);

        if(walletParams.PASSWORD != null && walletParams.PASSWORD.trim().length() >0){
            walletlock();
        }
        JSONObject json = JSONObject.fromObject(s);
        return json;
    }

    private JSONObject getNewaddressForAdmin(String userID) throws Exception {
        String s = main("getnewaddress", "[\""+userID+"\"]");
        JSONObject json = JSONObject.fromObject(s);
        return json;
    }

    private boolean walletpassphrase(int times) throws Exception {
        boolean flag = false;
        try {
            String s =main("walletpassphrase","[\""+walletParams.PASSWORD+"\","+times+"]");
            JSONObject json = JSONObject.fromObject(s);
            if(json.containsKey("error")){
                String error = json.get("error").toString();
                if(error.equals("null") || error == null || error == "" || error.trim().length() ==0){
                    flag = true;
                }
            }
        } catch (Exception e) {}
        return flag;
    }

    private void walletlock() throws Exception {
        main("walletlock","[]");
    }

    private void authenticator() {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (walletParams.ACCESS_KEY, walletParams.SECRET_KEY.toCharArray());
            }
        });
    }

    private String getSignature(String data, String key) throws Exception {
        // get an hmac_sha1 key from the raw key bytes
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
                HMAC_SHA1_ALGORITHM);

        // get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);

        // compute the hmac on input data bytes
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return bytArrayToHex(rawHmac);
    }

    private String bytArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    private String main(String method,String condition) throws Exception {
        String result = "";
        String tonce = "" + (System.currentTimeMillis() * 1000);
        authenticator();

        String params = "tonce=" + tonce.toString() + "&accesskey="
                + walletParams.ACCESS_KEY
                + "&requestmethod=post&id=1&method="+method+"&params="+condition;

        String hash = getSignature(params, walletParams.SECRET_KEY);

        String url = "http://"+walletParams.ACCESS_KEY+":"+walletParams.SECRET_KEY+"@"+walletParams.IP+":"+walletParams.PORT;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String userpass = walletParams.ACCESS_KEY + ":" + hash;
        String basicAuth = "Basic "
                + DatatypeConverter.printBase64Binary(userpass.getBytes());

        // add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
        con.setRequestProperty("Authorization", basicAuth);

        String postdata = "{\"method\":\""+method+"\", \"params\":"+condition+", \"id\": 1}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postdata);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        if(responseCode != 200){
            return "{\"result\":null,\"error\":"+responseCode+",\"id\":1}";
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        inputLine = in.readLine();
        response.append(inputLine);
        in.close();
        result = response.toString();
        return result;
    }
}
