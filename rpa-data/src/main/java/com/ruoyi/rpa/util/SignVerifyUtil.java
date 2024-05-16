package com.ruoyi.rpa.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.SM2;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * 签名验签方法
 * */
public class SignVerifyUtil {

    /**
     * 加签方法
     *
     * @param plain
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     */
    public static String sign(String plain, InputStream inputStream) {
        SM2 sm2=new SM2(IoUtil.read(inputStream).toByteArray(),null);
        String ver= sm2.signHex(HexUtil.encodeHexStr(plain));
        return ver;
    }


    /**
     * 验签方法
     *
     * @param plain
     * @param signatureByte
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     * @throws InvalidKeySpecException
     */
/*    public static boolean verify(String plain, String signatureByte, InputStream inputStream) {
        SM2 sm2=new SM2(null,IoUtil.read(inputStream).toByteArray());
        return  sm2.verifyHex(HexUtil.encodeHexStr(plain),signatureByte);

    }*/
    public static boolean verify(String plain, String signatureByte, InputStream inputStream){
        SM2 sm2 = new SM2(null, IoUtil.read(inputStream).toByteArray());
         return sm2.verifyHex(HexUtil.encodeHexStr(plain),signatureByte);
    }


}
