package com.itisacat.basic.framework.rest.handler;

import com.itisacat.basic.framework.consts.SysRestConsts;
import com.itisacat.basic.framework.core.app.FmkUtil;
import com.itisacat.basic.framework.core.util.IpUtil;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderProcess {

    private static volatile String serverId = null;
    private static volatile String powered = null;
    
    private ResponseHeaderProcess(){
      
    }
    
    static {
    	  getServerId();
          getPowered();
    }
    
    public static void process(HttpServletResponse resp){
    	resp.setHeader(SysRestConsts.HEADER_RESP_ID, MDC.get(SysRestConsts.REQUEST_ID));
    	resp.setHeader(SysRestConsts.HEADER_SERVER_ID, getServerId());
    	resp.setHeader(SysRestConsts.HEADER_POWERED_BY, getPowered());
    }
    
    private static String getServerId(){
        if(serverId == null){
            synchronized (ResponseHeaderProcess.class) {
                String flag = "!%s@%s#";
                String ip = IpUtil.getIp();
                if(ip != null){
                    String[] numbers = ip.split("\\.");
                    if(numbers.length == 4){
                        serverId = String.format(flag, numbers[3], numbers[2]);
                    }else if(numbers.length == 6){
                        serverId = String.format(flag, numbers[5], numbers[4]);
                    }
                 }
            }
        }
        return serverId;
    }
    
    private static String getPowered(){
        if(powered == null){
            synchronized (ResponseHeaderProcess.class) {
                powered = "QQFramework-" + FmkUtil.getHJframeworkVersion();
            }
        }
        return powered;
    }

}
