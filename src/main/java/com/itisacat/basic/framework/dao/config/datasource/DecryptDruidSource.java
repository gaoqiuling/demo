package com.itisacat.basic.framework.dao.config.datasource;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.consts.SysErrorConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.exception.SysException;

/**
 * 用来解密配置中的密文(重点配置，在这里扩展用户名的解密) setUsername(name) 方法对应xml中的一个property属性，password默认加密不需要重写， 还可以加密url 重写setUrl(url)
 */
@SuppressWarnings("all")
public class DecryptDruidSource extends DruidDataSource {

    private static final String PUBLIC_KEY =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAI12voYOca1M40kkcFD8aJd0sXLJQLSAVvCcI95WrKA6OFE1oz+sZkCr6JvQN0e4PkaTnO0ULdtAY6pru7K9evsCAwEAAQ==";

    @Override
    public void setPassword(String password) {
        try {
            password = ConfigTools.decrypt(BaseProperties.getProperty(PropConsts.Dao.JDBC_RSA_PUBLICKEY, PUBLIC_KEY), password);
        } catch (Exception e) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, "DB password decryption failure");
        }
        super.setPassword(password);
    }

    
    public static void main(String[] args) {
        try {
            System.out.println(ConfigTools.decrypt(PUBLIC_KEY, "cEmBErkZLUmVKLcZFREBcNM1aAWMBA0Mm/najGhaionw4z4MhLhNmQ5FqcbcjgFiiqI96hhDPFyOR5hducr/9A=="));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
