<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="机柜编码" prop="cabinetCode">
        <el-input
          v-model="queryParams.cabinetCode"
          placeholder="请输入机柜编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="金融机构" prop="financialInstitution">
        <el-input
          v-model="queryParams.financialInstitution"
          placeholder="请输入金融机构"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="户名" prop="accountName">
        <el-input
          v-model="queryParams.accountName"
          placeholder="请输入户名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账号" prop="accountNum">
        <el-input
          v-model="queryParams.accountNum"
          placeholder="请输入账号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:info:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="登录密码">
              <span>{{ props.row.loginPassword }}</span>
            </el-form-item>
            <el-form-item label="ukey密码">
              <span>{{ props.row.keyPassword }}</span>
            </el-form-item>
            <el-form-item label="网银代数">
              <span>{{ props.row.bankAlgebra }}</span>
            </el-form-item>
            <el-form-item label="备注">
              <span>{{ props.row.remark }}</span>
            </el-form-item>
            <el-form-item label="开户日期">
              <span>{{ props.row.accountOpenDate }}</span>
            </el-form-item>
            <el-form-item label="联网方式">
              <span>{{ props.row.networkingMethod }}</span>
            </el-form-item>
            <el-form-item label="直连开通日期">
              <span>{{ props.row.directConnectionDate === '1' ?'是':'否' }}</span>
            </el-form-item>
            <el-form-item label="是否多币种账户">
              <span>{{ props.row.ismulticurrency  === '1' ?'是':'否' }}</span>
            </el-form-item>
            <el-form-item label="币种">
              <span>{{ props.row.currency }}</span>
            </el-form-item>
            <el-form-item label="账户类别">
              <span>{{ props.row.accountType }}</span>
            </el-form-item>
            <el-form-item label="账户用途">
              <span>{{ props.row.accountPurpose }}</span>
            </el-form-item>
            <el-form-item label="账户性质">
              <span>{{ props.row.accountNature }}</span>
            </el-form-item>
            <el-form-item label="账户收支类型">
              <span>{{ props.row.accountIncomeExpenditureType }}</span>
            </el-form-item>
            <el-form-item label="外汇业务类型">
              <span>{{ props.row.foreignExchangeBusinessTypes }}</span>
            </el-form-item>
            <el-form-item label="账户有效期（开始）">
              <span>{{ props.row.accountValidityPeriodBeginDate }}</span>
            </el-form-item>
             <el-form-item label="账户有效期（结束）">
              <span>{{ props.row.accountValidityPeriodEndDate }}</span>
            </el-form-item>
            <el-form-item label="专用账户资金性质">
              <span>{{ props.row.natureFundsDedicatedAccounts }}</span>
            </el-form-item>
            <el-form-item label="是否票据账户">
              <span>{{ props.row.isBillAccount  === '1' ?'是':'否' }}</span>
            </el-form-item>
            <el-form-item label="票据账户类型">
              <span>{{ props.row.billAccountType  === '1' ?'是':'否' }}</span>
            </el-form-item>
            <el-form-item label="是否监管账户">
              <span>{{ props.row.isRegulatoryAccounts === '1' ?'是':'否' }}</span>
            </el-form-item>
            <el-form-item label="财务负责人">
              <span>{{ props.row.financialManager }}</span>
            </el-form-item>
            <el-form-item label="负责人人事编号">
              <span>{{ props.row.managerNo }}</span>
            </el-form-item>
            <el-form-item label="是否有支付密码器">
              <span>{{ props.row.hasPayPassword === '1' ?'是':'否' }}</span>
            </el-form-item>
            <el-form-item label="是否开通网银">
              <span>{{ props.row.hasOnlineBanking === '1' ?'是':'否' }}</span>
            </el-form-item>
            <el-form-item label="网银开通日期">
              <span>{{ props.row.onlineBankingDate }}</span>
            </el-form-item>
            <el-form-item label="客户经理姓名">
              <span>{{ props.row.customManagerName }}</span>
            </el-form-item>
            <el-form-item label="客户经理电话">
              <span>{{ props.row.customManagerNumber }}</span>
            </el-form-item>
            <el-form-item label="支付限额（日）">
              <span>{{ props.row.payLimitDay }}</span>
            </el-form-item>
            <el-form-item label="支付限额（笔）">
              <span>{{ props.row.payLimitTransaction }}</span>
            </el-form-item>
            <el-form-item label="提现限额（日）">
              <span>{{ props.row.withdrawalLimitDay }}</span>
            </el-form-item>
            <el-form-item label="提现限额（笔）">
              <span>{{ props.row.withdrawalLimitTransaction }}</span>
            </el-form-item>
            <el-form-item label="预留1">
              <span>{{ props.row.remark1 }}</span>
            </el-form-item>
            <el-form-item label="预留2">
              <span>{{ props.row.remark2 }}</span>
            </el-form-item>
            <el-form-item label="预留3">
              <span>{{ props.row.remark3 }}</span>
            </el-form-item>
            <el-form-item label="预留4">
              <span>{{ props.row.remark4 }}</span>
            </el-form-item>
            <el-form-item label="预留5">
              <span>{{ props.row.remark5 }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="机柜编码" align="center" prop="cabinetCode" />
      <el-table-column label="端口号" align="center" prop="usbPort" />
      <el-table-column label="金融机构" align="center" prop="financialInstitution" />
      <el-table-column label="开户机构" align="center" prop="accountOpenInstitution" />
      <el-table-column label="户名" align="center" prop="accountName" />
      <el-table-column label="账号" align="center" prop="accountNum" />
      <el-table-column
        prop="isOpenDevice"
        label="是否开启端口"
        width="100"
        filter-placement="bottom-end">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.isOpenDevice ? 'success' : 'primary  '"
            disable-transitions>{{scope.row.isOpenDevice ? '已开启':'关闭'}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width = "280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-eleme"
            @click="handleOpen(scope.row)"
          >开启端口</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-eleme"
            @click="handleClose(scope.row)"
          >关闭端口</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="登录密码" prop="loginPassword">
          <el-input v-model="form.loginPassword" placeholder="请输入登录密码" />
        </el-form-item>
        <el-form-item label="ukey密码" prop="keyPassword">
          <el-input v-model="form.keyPassword" placeholder="请输入ukey密码" />
        </el-form-item>
        <el-form-item label="机柜编码" prop="cabinetCode">
          <el-input v-model="form.cabinetCode" placeholder="请输入机柜编码" />
        </el-form-item>
        <el-form-item label="端口号" prop="usbPort">
          <el-input v-model.number="form.usbPort" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="网银代数" prop="bankAlgebra" >
          <el-input v-model="form.bankAlgebra" placeholder="请输入网银代数" maxlength="2"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="金融机构" prop="financialInstitution">
          <el-input v-model="form.financialInstitution" placeholder="请输入金融机构" />
        </el-form-item>
        <el-form-item label="开户机构" prop="accountOpenInstitution">
          <el-input v-model="form.accountOpenInstitution" placeholder="请输入开户机构" />
        </el-form-item>
        <el-form-item label="户名" prop="accountName">
          <el-input v-model="form.accountName" placeholder="请输入户名" />
        </el-form-item>
        <el-form-item label="账号" prop="accountNum">
          <el-input v-model="form.accountNum" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="开户日期" prop="accountOpenDate">
          <el-date-picker clearable
            v-model="form.accountOpenDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开户日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="联网方式" prop="networkingMethod">
          <el-input v-model="form.networkingMethod" placeholder="请输入联网方式" />
        </el-form-item>
        <el-form-item label="直连开通日期" prop="directConnectionDate">
          <el-date-picker clearable
            v-model="form.directConnectionDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择直连开通日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否多币种账户" prop="ismulticurrency">
          <el-select v-model="form.ismulticurrency" placeholder="请选择是否多币种账户">
            <el-option label="是" value="1"></el-option>
            <el-option label="否" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="币种" prop="currency">
          <el-input v-model="form.currency" placeholder="请输入币种" />
        </el-form-item>
        <el-form-item label="账户用途" prop="accountPurpose">
          <el-input v-model="form.accountPurpose" placeholder="请输入账户用途" />
        </el-form-item>
        <el-form-item label="账户性质" prop="accountNature">
          <el-input v-model="form.accountNature" placeholder="请输入账户性质" />
        </el-form-item>
        <el-form-item label="外汇业务类型" prop="foreignExchangeBusinessTypes">
          <el-input v-model="form.foreignExchangeBusinessTypes" placeholder="请输入外汇业务类型" />
        </el-form-item>
        <el-form-item label="账户有效期" prop="accountValidityPeriodBeginDate">
          <el-date-picker clearable
            v-model="form.accountValidityPeriodBeginDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择账户有效期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="账户有效期" prop="accountValidityPeriodEndDate">
          <el-date-picker clearable
            v-model="form.accountValidityPeriodEndDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择账户有效期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="专用账户资金性质" prop="natureFundsDedicatedAccounts">
          <el-input v-model="form.natureFundsDedicatedAccounts" placeholder="请输入专用账户资金性质" />
        </el-form-item>
        <el-form-item label="是否票据账户" prop="isBillAccount">
          <el-select v-model="form.isBillAccount" placeholder="请输入是否票据账户">
            <el-option label="是" value="1"></el-option>
            <el-option label="否" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否监管账户" prop="isRegulatoryAccounts">
          <el-select v-model="form.isRegulatoryAccounts" placeholder="请输入是否监管账户">
            <el-option label="是" value="1"></el-option>
            <el-option label="否" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="财务负责人" prop="financialManager">
          <el-input v-model="form.financialManager" placeholder="请输入财务负责人" />
        </el-form-item>
        <el-form-item label="负责人人事编号" prop="managerNo">
          <el-input v-model="form.managerNo" placeholder="请输入负责人人事编号" />
        </el-form-item>
        <el-form-item label="账户负责人" prop="accountManager">
          <el-input v-model="form.accountManager" placeholder="请输入账户负责人" />
        </el-form-item>
        <el-form-item label="账户负责人编号" prop="accountManagerNo">
          <el-input v-model="form.accountManagerNo" placeholder="请输入账户负责人编号" />
        </el-form-item>
        <el-form-item label="是否有支付密码器" prop="hasPayPassword">
          <el-select v-model="form.hasPayPassword" placeholder="请输入是否有支付密码器">
            <el-option label="是" value="1"></el-option>
            <el-option label="否" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否开通网银" prop="hasOnlineBanking">
          <el-select v-model="form.hasOnlineBanking" placeholder="请输入是否开通网银">
            <el-option label="是" value="1"></el-option>
            <el-option label="否" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="网银开通日期" prop="onlineBankingDate">
          <el-date-picker clearable
            v-model="form.onlineBankingDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择网银开通日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="客户经理姓名" prop="customManagerName">
          <el-input v-model="form.customManagerName" placeholder="请输入客户经理姓名" />
        </el-form-item>
        <el-form-item label="客户经理电话" prop="customManagerNumber">
          <el-input v-model.number="form.customManagerNumber" placeholder="请输入客户经理电话" />
        </el-form-item>
        <el-form-item label="支付限额（日）" prop="payLimitDay">
          <el-input v-model.number="form.payLimitDay" placeholder="请输入支付限额" />
        </el-form-item>
        <el-form-item label="支付限额（笔）" prop="payLimitTransaction">
          <el-input v-model.number="form.payLimitTransaction" placeholder="请输入支付限额" />
        </el-form-item>
        <el-form-item label="提现限额（日）" prop="withdrawalLimitDay">
          <el-input v-model.number="form.withdrawalLimitDay" placeholder="请输入提现限额" />
        </el-form-item>
        <el-form-item label="提现限额（笔）" prop="withdrawalLimitTransaction">
          <el-input v-model.number="form.withdrawalLimitTransaction" placeholder="请输入提现限额" />
        </el-form-item>
        <el-form-item label="预留1" prop="remark1">
          <el-input v-model="form.remark1" placeholder="请输入预留1" />
        </el-form-item>
        <el-form-item label="预留2" prop="remark2">
          <el-input v-model="form.remark2" placeholder="请输入预留2" />
        </el-form-item>
        <el-form-item label="预留3" prop="remark3">
          <el-input v-model="form.remark3" placeholder="请输入预留3" />
        </el-form-item>
        <el-form-item label="预留4" prop="remark4">
          <el-input v-model="form.remark4" placeholder="请输入预留4" />
        </el-form-item>
        <el-form-item label="预留5" prop="remark5">
          <el-input v-model="form.remark5" placeholder="请输入预留5" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInfo, getInfo, delInfo, addInfo, updateInfo,openDevice,closeDevice } from "@/api/system/info";

export default {
  name: "Info",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 【请填写功能名称】表格数据
      infoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        loginPassword: null,
        keyPassword: null,
        cabinetCode: null,
        usbPort: null,
        bankAlgebra: null,
        financialInstitution: null,
        accountOpenInstitution: null,
        accountName: null,
        accountNum: null,
        accountOpenDate: null,
        networkingMethod: null,
        directConnectionDate: null,
        ismulticurrency: null,
        currency: null,
        accountType: null,
        accountPurpose: null,
        accountNature: null,
        accountIncomeExpenditureType: null,
        foreignExchangeBusinessTypes: null,
        accountValidityPeriodBeginDate: null,
        accountValidityPeriodEndDate: null,
        natureFundsDedicatedAccounts: null,
        isBillAccount: null,
        billAccountType: null,
        isRegulatoryAccounts: null,
        financialManager: null,
        managerNo: null,
        accountManager: null,
        accountManagerNo: null,
        hasPayPassword: null,
        hasOnlineBanking: null,
        onlineBankingDate: null,
        customManagerName: null,
        customManagerNumber: null,
        payLimitDay: null,
        payLimitTransaction: null,
        withdrawalLimitDay: null,
        withdrawalLimitTransaction: null,
        remark1: null,
        remark2: null,
        remark3: null,
        remark4: null,
        remark5: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        loginPassword: [
          { required: true, message: "登录密码不能为空", trigger: "blur" }
        ],
        keyPassword: [
          { required: true, message: "ukey密码不能为空", trigger: "blur" }
        ],
        cabinetCode: [
          { required: true, message: "机柜编码不能为空", trigger: "blur" }
        ],
        usbPort: [
          { required: true, message: "端口号不能为空" },
          { type: 'number', message: '端口号必须为数字'}
        ],
        financialInstitution: [
          { required: true, message: "金融机构不能为空", trigger: "blur" }
        ],
        accountOpenInstitution: [
          { required: true, message: "开户机构不能为空", trigger: "blur" }
        ],
        accountName: [
          { required: true, message: "户名不能为空", trigger: "blur" }
        ],
        accountNum: [
          { required: true, message: "账号不能为空", trigger: "blur" }
        ],
        payLimitDay: [
          { type: 'number', message: '请输入日限额为数字'}
        ],
        payLimitTransaction: [
          { type: 'number', message: '请输入笔限额为数字'}
        ],
        withdrawalLimitDay: [
          { type: 'number', message: '请输入日提现限额为数字'}
        ],
        withdrawalLimitTransaction: [
          { type: 'number', message: '请输入笔提现限额为数字'}
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleClose(row) {
      if (row.usbPort === null || row.usbPort === '' || row.cabinetCode === null || row.cabinetCode === '') {
        this.$message({
          showClose: true,
          message: '机柜或端口信息不能为空',
          type: 'error'
        });
        return;
      }
      if (!row.isOpenDevice) {
        this.$message({
          showClose: true,
          message: '已经处于关闭状态',
          type: 'warn'
        });
        return;
      }
      closeDevice(row.cabinetCode, row.usbPort).then(
        response => {
          if (response.code === 200) {
            this.$message({
              showClose: true,
              message: '端口关闭成功' + response.message,
              type: 'success'
            })
          } else {
            this.$message({
            showClose: true,
            message: '端口关闭失败' + response.message,
            type: 'error'
            });
          }
        }
      )
    },
    handleOpen(row) {
      if (row.usbPort === null || row.usbPort === '' || row.cabinetCode === null || row.cabinetCode === '') {
        this.$message({
          showClose: true,
          message: '机柜或端口信息不能为空',
          type: 'error'
        });
        return;
      }
      if (row.isOpenDevice) {
        this.$message({
          showClose: true,
          message: '已经处于开启状态',
          type: 'warn'
        });
        return;
      }
      openDevice(row.cabinetCode, row.usbPort).then(
        response => {
          if (response.code === 200) {
            this.$message({
              showClose: true,
              message: '端口开启成功' + response.message,
              type: 'success'
            })
          } else {
            this.$message({
            showClose: true,
            message: '端口开启失败' + response.message,
            type: 'error'
            });
          }
        }
      )
      //  row.cabinetCode row.usbPort
    },
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listInfo(this.queryParams).then(response => {
        this.infoList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        loginPassword: null,
        keyPassword: null,
        cabinetCode: null,
        usbPort: null,
        bankAlgebra: null,
        remark: null,
        financialInstitution: null,
        accountOpenInstitution: null,
        accountName: null,
        accountNum: null,
        accountOpenDate: null,
        networkingMethod: null,
        directConnectionDate: null,
        ismulticurrency: null,
        currency: null,
        accountType: null,
        accountPurpose: null,
        accountNature: null,
        accountIncomeExpenditureType: null,
        foreignExchangeBusinessTypes: null,
        accountValidityPeriodBeginDate: null,
        accountValidityPeriodEndDate: null,
        natureFundsDedicatedAccounts: null,
        isBillAccount: null,
        billAccountType: null,
        isRegulatoryAccounts: null,
        financialManager: null,
        managerNo: null,
        accountManager: null,
        accountManagerNo: null,
        hasPayPassword: null,
        hasOnlineBanking: null,
        onlineBankingDate: null,
        customManagerName: null,
        customManagerNumber: null,
        payLimitDay: null,
        payLimitTransaction: null,
        withdrawalLimitDay: null,
        withdrawalLimitTransaction: null,
        remark1: null,
        remark2: null,
        remark3: null,
        remark4: null,
        remark5: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加账户信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改账户信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addInfo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除【请填写功能名称】编号为"' + ids + '"的数据项？').then(function() {
        return delInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
<style scoped>
  .demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-bottom: 0;
    width: 30%;
  }
</style>

