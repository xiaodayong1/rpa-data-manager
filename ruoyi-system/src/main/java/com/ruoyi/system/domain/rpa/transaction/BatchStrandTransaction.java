package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

import java.util.List;

@Data
public class BatchStrandTransaction {

      private List<StandardTransaction> standardTransactionList;

      private boolean isDesc;

      public BatchStrandTransaction(List<StandardTransaction> standardTransactionList,Boolean isDesc) {
            this.standardTransactionList = standardTransactionList;
            this.isDesc = isDesc;
      }
}

