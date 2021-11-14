package com.gykj.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NioWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
//        ByteBuf delimiter = Unpooled.copiedBuffer("SIHX".getBytes());
        socketChannel.pipeline().addLast(new IdleStateHandler(10,0,0, TimeUnit.SECONDS));
//        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));//
        socketChannel.pipeline().addLast(new ByteArrayDecoder());//
        socketChannel.pipeline().addLast(new ByteArrayEncoder());//
        socketChannel.pipeline().addLast("handler",new NioWebSocketHandler());//自定义的业务handler

    }
}
