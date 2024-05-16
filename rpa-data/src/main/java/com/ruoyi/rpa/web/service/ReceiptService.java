package com.ruoyi.rpa.web.service;


import com.ruoyi.system.domain.rpa.transaction.ReceiptDto;

import java.io.IOException;

public interface ReceiptService {
    void pushRecepit(ReceiptDto receiptDto) throws IOException;

    void splitReceiptPDF(ReceiptDto receiptDto) throws Exception;
}
