package com.ruoyi.system.domain.rpa;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RpaAccountInfo {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "登录密码不能为空")
    /** 登录密码 */
    private String loginPassword;

    @NotBlank(message = "ukey密码不能为空")
    /** ukey密码 */
    private String keyPassword;

    @NotBlank(message = "机柜编码不能为空")
    /** 机柜编码 */
    private String cabinetCode;

    @NotNull(message = "端口号不能为空")
    /** 端口号 */
    private Integer usbPort;

    /** 网银代数 */
    private String bankAlgebra;

    @NotBlank(message = "金融机构不能为空")
    /** 金融机构 */
    private String financialInstitution;

    @NotBlank(message = "开户机构不能为空")
    /** 开户机构 */
    private String accountOpenInstitution;

    @NotBlank(message = "户名不能为空")
    /** 户名 */
    private String accountName;

    @NotBlank(message = "账号不能为空")
    /** 账号 */
    private String accountNum;

    /** 开户日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date accountOpenDate;

    /** 联网方式 */
    private String networkingMethod;

    /** 直连开通日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date directConnectionDate;

    /** 是否多币种账户 */
    private String ismulticurrency;

    /** 币种 */
    private String currency;

    /** 账户类别 */
    private String accountType;

    /** 账户用途 */
    private String accountPurpose;

    /** 账户性质 */
    private String accountNature;

    /** 账户收支类型 */
    private String accountIncomeExpenditureType;

    /** 外汇业务类型 */
    private String foreignExchangeBusinessTypes;

    /** 账户有效期（开始日期） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date accountValidityPeriodBeginDate;

    /** 账户有效期（结束日期） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date accountValidityPeriodEndDate;

    /** 专用账户资金性质 */
    private String natureFundsDedicatedAccounts;

    /** 是否票据账户 */
    private String isBillAccount;

    /** 票据账户类型 */
    private String billAccountType;

    /** 是否监管账户 */
    private String isRegulatoryAccounts;

    /** 财务负责人 */
    private String financialManager;

    /** 负责人人事编号 */
    private String managerNo;

    private String remark;

    /** 账户负责人 */
    private String accountManager;

    /** 账户负责人编号 */
    private String accountManagerNo;

    /** 是否有支付密码器 */
    private String hasPayPassword;

    /** 是否开通网银 */
    private String hasOnlineBanking;

    /** 网银开通日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date onlineBankingDate;

    /** 客户经理姓名 */
    private String customManagerName;

    /** 客户经理电话 */
    private String customManagerNumber;

    /** 支付限额（日） */
    private BigDecimal payLimitDay;

    /** 支付限额（笔） */
    private BigDecimal payLimitTransaction;

    /** 提现限额（日） */
    private BigDecimal withdrawalLimitDay;

    /** 提现限额（笔） */
    private BigDecimal withdrawalLimitTransaction;

    /** 预留1 */
    private String remark1;

    /** 预留2 */
    private String remark2;

    /** 预留3 */
    private String remark3;

    /** 预留4 */
    private String remark4;

    /** 预留5 */
    private String remark5;

    @TableField(exist = false)
    private Boolean isOpenDevice;

}
