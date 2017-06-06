package com.cyf.base.common.component;

import com.cyf.base.common.config.PublicConfig;
import com.cyf.base.common.utils.LoggerWrapper;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Administrator on 2017/5/29.
 */
public class EsClient {
    private LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(EsClient.class);
    private TransportClient transportClient;

    public EsClient(String clusterName, String addresses){
        Settings setting = Settings.builder().put("cluster.name", clusterName).build();
        this.transportClient = new PreBuiltTransportClient(setting).addTransportAddresses(getInetSocketTransportAddress(addresses));
    }

    public EsClient(Map<String, String> settings, String addresses){
        Settings setting = Settings.builder().put(settings).build();
        this.transportClient = new PreBuiltTransportClient(setting);
        if(addresses == null || addresses.trim().length() == 0){
            return;
        }
        this.transportClient.addTransportAddresses(getInetSocketTransportAddress(addresses));
    }

    public TransportClient getTransportClient() {
        return transportClient;
    }

    public void setTransportClient(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    private InetSocketTransportAddress[] getInetSocketTransportAddress(String addresses){
        String[] addressArray = addresses.split(PublicConfig.COMMA_SEPARATOR);
        int length = addressArray.length;
        InetSocketTransportAddress[] inetAddresses = new InetSocketTransportAddress[length];
        for(int i=0; i<length; i++){
            String[] hostArr = addressArray[i].split(":");
            InetAddress inetAddress = null;
            int port = 9300;//es的默认端口号
            try{
                inetAddress = InetAddress.getByName(hostArr[0]);
                if(hostArr.length > 1){
                    port = Integer.valueOf(hostArr[1]);
                }
            }catch(UnknownHostException e){
                logger.error(e);
            }

            inetAddresses[i] = new InetSocketTransportAddress(inetAddress, port);
        }
        return inetAddresses;
    }

}
