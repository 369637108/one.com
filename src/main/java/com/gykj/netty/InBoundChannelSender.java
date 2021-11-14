package com.gykj.netty;

import com.gykj.netty.ChannelSupervise;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.sql.Time;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class InBoundChannelSender implements Runnable{

    @Override
    public void run() {
        System.out.println("InBoundChannelSender()执行");
        while(true){
            System.out.println("channelMap()执行");

            ConcurrentMap<String, ChannelId> channelMap = ChannelSupervise.getChannelMap();
            for(String id : channelMap.keySet()){
                Channel channel = ChannelSupervise.findChannel(id);
                if(channel.isActive()&&channel.isWritable()){
                    channel.writeAndFlush(getInboundSihInfo());
                }
            }
            try {
                System.out.println(Thread.currentThread().getName());
                //Thread.currentThread().sleep(3000);
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取推送的信息
     * @return
     */
    public String getInboundSihInfo(){
        //组装返回报文
        return "hello word";
    }

    public static void main(String[] args) {
        new Thread(new InBoundChannelSender()).run();

    }
}
