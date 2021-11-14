package com.gykj.netty;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class NioWebSocketHandler extends ChannelInboundHandlerAdapter {
    private int lossConnectCount = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        lossConnectCount = 0;
        Channel channel = ctx.channel();
        final int hashCode = channel.hashCode();
        System.out.println("channel hashCode:" + hashCode + " msg:" + msg );
        byte[] bytes = (byte[]) msg;
        byte cmd = bytes[4];
        if(cmd == (byte) 0xA0 ){
            byte[] header = new byte[9];
            byte[] sihx = "SIHX".getBytes(StandardCharsets.UTF_8);
            header = ArrayUtils.add(sihx,(byte)0xA1);
            byte[] length = int2bytes(35);
            header = ArrayUtils.addAll(header,length);
            byte[] content= "{\"Result\":\"0\",\"Desc\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            byte[] resp = ArrayUtils.addAll(header,content);
            ctx.channel().writeAndFlush(resp).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("server端返回成功");
                    } else {
                        System.out.println("server端返回失败");
                    }
                }
            });
        } else if(cmd == (byte)0xB0){
            byte[] header = new byte[9];
            byte[] sihx = "SIHX".getBytes(StandardCharsets.UTF_8);
            header = ArrayUtils.add(sihx,(byte)0xB1);
            byte[] length = int2bytes(35);
            header = ArrayUtils.addAll(header,length);
            byte[] content= "{\"Status\":\"0\",\"Desc\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            byte[] resp = ArrayUtils.addAll(header,content);
            ctx.channel().writeAndFlush(resp).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("server端返回成功");
                    } else {
                        System.out.println("server端返回失败");
                    }
                }
            });
        }else if(cmd == (byte)0x31){
           //原格式返回，将cmd改成0x33即可
            bytes[4]=(byte)0x33;
            ctx.channel().writeAndFlush(bytes);
        }
    }
    public static byte[] int2bytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >>> 24) & 0xff);//说明一
        result[1] = (byte) ((num >>> 16) & 0xff);
        result[2] = (byte) ((num >>> 8) & 0xff);
        result[3] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        ChannelSupervise.addChannel(ctx.channel());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("已经5秒未收到客户端的消息了！");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                lossConnectCount++;
                if (lossConnectCount>3){
                    System.out.println("关闭这个不活跃通道！");
                    ctx.channel().close();
                }
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    public static void main(String[] args) {
        String ss = "SIH=13.1;MsgLen=00001293;HeadLen=00000119;ExtLen=00000170;ResLen=00000035;\n TraceLevel=0;OriSys=AVE;DesSys=FARE_S_IFS:server02;MsgType=3;TransactionID=FARE_S_IFS2014052716503000000001;SessionID=UID2873_ByOK3vjFD75aPnrF7C2;\n" +
                "Compress=0;FuncCode=11;UID=xxx;Channel=eTerm;ChannelUser=zhangsan;UsasSys=B;PID=12345;CWA=A;Office=pek099;Airline=CA;Country=CN;Agent=99;Level=73;ReqFormat=2;ResFormat=5;\n" +
                "MCSS:00000004=YYYY;JCF:00000002=xx;\n";
        System.out.println(ss.length());
    }
}
