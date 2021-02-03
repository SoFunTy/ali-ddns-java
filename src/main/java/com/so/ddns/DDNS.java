package com.so.ddns;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DDNS {
    public static void main(String[] args) {
            while(true) {
                DefaultProfile profile = DefaultProfile.getProfile(
                        PropertiesUtil.getKey("Region"),
                        PropertiesUtil.getKey("AccessKeyId"),
                        PropertiesUtil.getKey("AccessKeySecret"));
                IAcsClient client = new DefaultAcsClient(profile);

                DDNS ddns = new DDNS();
                DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
                describeDomainRecordsRequest.setDomainName(PropertiesUtil.getKey("Domain"));
                describeDomainRecordsRequest.setRRKeyWord(PropertiesUtil.getKey("Rrefix"));
                describeDomainRecordsRequest.setType(PropertiesUtil.getKey("Type"));
                DescribeDomainRecordsResponse describeDomainRecordsResponse = ddns.describeDomainRecords(describeDomainRecordsRequest, client);
                List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();
                if (domainRecords.size() != 0) {
                    DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
                    String recordId = record.getRecordId();
                    String recordsValue = record.getValue();
                    String currentHostIP = ddns.getCurrentHostIP();
                    System.out.println(">"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\t当前IP地址：" + currentHostIP + "\t" + PropertiesUtil.getKey("Rrefix") + "." + PropertiesUtil.getKey("Domain") + "域名指向：" + recordsValue);
                    if (!currentHostIP.equals(recordsValue)) {
                        UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
                        updateDomainRecordRequest.setRR((PropertiesUtil.getKey("Rrefix")));
                        updateDomainRecordRequest.setRecordId(recordId);
                        updateDomainRecordRequest.setValue(currentHostIP);
                        updateDomainRecordRequest.setType(PropertiesUtil.getKey("Type"));
                        UpdateDomainRecordResponse updateDomainRecordResponse = ddns.updateDomainRecord(updateDomainRecordRequest, client);
                        System.out.println("解析更新");
                    }
                }
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
    /**
     * 获取主域名的所有解析记录列表
     */
    private DescribeDomainRecordsResponse describeDomainRecords(DescribeDomainRecordsRequest request, IAcsClient client) {
        try {
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 获取当前主机公网IP
     */
    private String getCurrentHostIP() {
        String jsonip = "https://jsonip.com/";
        String result = "";
        BufferedReader in = null;
        try {
            URL url = new URL(jsonip);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            in = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        String rexp = "(\\d{1,3}\\.){3}\\d{1,3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(result);
        String res = "";
        while (mat.find()) {
            res = mat.group();
            break;
        }
        return res;
    }

    /**
     * 修改解析记录
     */
    private UpdateDomainRecordResponse updateDomainRecord(UpdateDomainRecordRequest request, IAcsClient client) {
        try {
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static void log_print(String functionName, Object result) {
        Gson gson = new Gson();
        System.out.println("-------------------------------" + functionName + "-------------------------------");
        System.out.println(gson.toJson(result));
    }
}