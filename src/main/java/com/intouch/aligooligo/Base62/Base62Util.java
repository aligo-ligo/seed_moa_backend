package com.intouch.aligooligo.Base62;

import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class Base62Util {
    static final char[] BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public String UrlEncoding(BigInteger value){
        final StringBuilder sb = new StringBuilder();
        while(value.compareTo(BigInteger.ZERO)>0){
            BigInteger[] divRem = value.divideAndRemainder(BigInteger.valueOf(62));
            value = divRem[0];
            sb.append(BASE62[divRem[1].intValue()]);
        }
        return sb.toString();
    }
}
