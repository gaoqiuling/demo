package com.itisacat.basic.framework.core.app;

import java.lang.management.ManagementFactory;

public class FmkUtil {
    private static final String HJ_FRAMEWORK_VERSION = "2.1.8.3-RELEASE";
    
    private static int pid = 0;
    
    private FmkUtil(){}
    
    public static String getHJframeworkVersion(){
        return HJ_FRAMEWORK_VERSION;
    }
    
    public static int getPid(){
        if(pid == 0){
            String name = ManagementFactory.getRuntimeMXBean().getName();  
            pid = Integer.valueOf(name.split("@")[0]);
        }
        return pid;
        
    }
    
}
