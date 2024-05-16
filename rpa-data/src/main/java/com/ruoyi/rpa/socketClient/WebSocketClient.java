package com.ruoyi.rpa.socketClient;

import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * websocket client 客户端端控制
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient implements AutoCloseable{

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
    private static final String WS_URL = "ws://127.0.0.1:50926/SysSvr";

    private static volatile WebSocketClient instance;

    private volatile int sendFlag = 0;
    private String result = null;

    private WebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public static WebSocketClient getInstance() {
        if (instance == null) {
            synchronized (WebSocketClient.class) {
                if (instance == null) {
                    try {
                        instance = new WebSocketClient(new URI(WS_URL));
                        instance.connect();
                    } catch (URISyntaxException e) {
                        logger.error("WebSocket 构建实例出现问题", e);
                    }
                }
            }
        } else {
            if (instance.getReadyState() == ReadyState.NOT_YET_CONNECTED
                    || instance.getReadyState() == ReadyState.CLOSED) {
                if (instance.isClosed()) {
                    instance.reconnect();
                }
            }
        }
        return instance;
    }

    // 发送字符串消息
    public String sendStr(String text) {
        if (instance.isClosed()){
            instance.reconnect();
        }
        if (StringUtils.isEmpty(text)){
            return "socket发送内容为空";
        }
        synchronized (this) {
            sendFlag = 1;
            this.send(text);
            while (sendFlag != 0) {
                logger.debug(" 等待返回值中 =============== " + sendFlag);
            }
            return result;
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.debug(" ws 服务正常打开！！");
    }

    @Override
    public void onMessage(String s) {
        result = s;
        sendFlag = 0;
        logger.debug(" ws 接收服务器推送的消息！！" + s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        result = null;
        sendFlag = 0;
        synchronized (WebSocketClient.class) {
            if (instance != null && !instance.isClosing()) {
                instance.close();
                instance = null; // 清除引用，确保下次调用 initialize 时能够创建新实例
            }
        }
        logger.debug(" ws 客户端正常关闭！！");
    }

    @Override
    public void onError(Exception e) {
        result = null;
        sendFlag = 0;
        logger.debug(" ws 客户端连接出现错误！！");
    }
}