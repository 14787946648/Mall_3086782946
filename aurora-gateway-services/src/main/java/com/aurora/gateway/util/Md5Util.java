package com.aurora.gateway.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * @Description MD5根据类
 * @Date 2022-11-22 2:02
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
public class Md5Util {

    /**
     * 加密字符串
     *
     * @param content 需要加密的内容
     * @return String
     */
    public static String encryption(String content) {
        if (StrUtil.isNotBlank(content)) {
            return SecureUtil.md5(content);
        }
        return null;
    }

    /**
     * 验证密文
     *
     * @param ciphertext 密文
     * @return boolean
     */
    public static boolean verifyingCiphertext(String ciphertext, String content) {
        if (StrUtil.isNotBlank(ciphertext) && StrUtil.isNotBlank(content)) {
            return ciphertext.equals(encryption(content));
        }
        return false;
    }
}


