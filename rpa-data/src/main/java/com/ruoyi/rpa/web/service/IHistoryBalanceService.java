package com.ruoyi.rpa.web.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.rpa.AccountDetailsPageDto;
import com.ruoyi.system.domain.rpa.transaction.*;

import java.io.IOException;

public interface IHistoryBalanceService {
    AjaxResult historyBalance(BalanceDto balanceDto) throws IOException;

    AjaxResult transaction(StandardTransaction StandardTransaction) throws IOException;

    AjaxResult transactionList(BatchStrandTransaction batchStrandTransaction) throws IOException;

    AjaxResult electronicTransaction(ElectronicTransactionDto electronicTransactionDto);

    Object electronicFile(ElectronicFileDto electronicFileDto) throws IOException;

    Object accountDetailsPage(AccountDetailsPageDto paramAccountDetailsPageDto);

}
