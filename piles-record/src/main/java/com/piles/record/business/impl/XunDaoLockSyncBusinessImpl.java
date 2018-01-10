package com.piles.record.business.impl;

import com.google.common.primitives.Bytes;
import com.piles.common.business.IBusiness;
import com.piles.common.util.BytesUtil;
import com.piles.common.util.CRC16Util;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 * 循道时钟同步接口逻辑
 */
@Slf4j
@Component("xunDaoLockSyncBusiness")
public class XunDaoLockSyncBusinessImpl implements IBusiness {


    @Override
    public byte[] process(byte[] msg, Channel incoming) {
        log.info("接收到循道心跳请求报文");
        //依照报文体规则解析报文
        DateTime dateTime = new DateTime();
        int ms = dateTime.getMillisOfSecond();
        int min = dateTime.getMinuteOfHour();
        int hour = dateTime.getHourOfDay();
        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthOfYear();
        int year = dateTime.getYear() - 2000;
        byte[] data = Bytes.concat(BytesUtil.intToBytesLittle(ms), BytesUtil.intToBytes(min, 1), BytesUtil.intToBytes(hour, 1),
                BytesUtil.intToBytes(day, 1), BytesUtil.intToBytes(month, 1), BytesUtil.intToBytes(year, 1));

        byte[] head = new byte[]{0x68};
        byte[] length = new byte[]{0x20};
        byte[] contrl = BytesUtil.copyBytes(msg, 2, 4);
        byte[] type = new byte[]{(byte) 0x103};
        byte[] beiyong = new byte[]{0x00};
        byte[] reason = new byte[]{0x03, 0x00};
        byte[] crc = CRC16Util.getXunDaoCRC(data);
        byte[] addr = new byte[]{0x00, 0x00, 0x00};


        byte[] temp = Bytes.concat(head, length, contrl, type, beiyong, reason, crc, addr, data);

        //组装返回报文体

        return temp;
    }
}
