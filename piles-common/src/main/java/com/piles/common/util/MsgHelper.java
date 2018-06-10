package com.piles.common.util;

/**
 *  报文帮助类
 *  Created by zhanglizhi on 2018/6/10.
 */
public class MsgHelper {

    /**
     * 获取报文中的枪号
     * @param msg
     * @return
     */
    public int getGunNo(byte[] msg){

        return BytesUtil.bytesToIntLittle(BytesUtil.copyBytes(msg, 7, 1));
    }
}
