package com.ruoyi.rpa.util;

import com.ruoyi.rpa.socketClient.WebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class SocketShutdownManager {
    @PreDestroy
    public void onShutdown() {
        // 在应用关闭时执行的清理操作
        log.info("程序关闭，关闭socket连接");
        WebSocketClient.getInstance().close();
    }
}
