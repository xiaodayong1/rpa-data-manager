<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="明细流水号" prop="serialNumber">
        <el-input
          v-model="queryParams.serialNumber"
          placeholder="请输入明细流水号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账号筛选" prop="tradeAccountNum">
        <el-select v-model="queryParams.tradeAccountNum" placeholder="请选择账号" filterable clearable
          :style="{width: '100%'}">
          <el-option v-for="(item, index) in accountOption" :key="index" :label="item.label"
            :value="item.value" :disabled="item.disabled"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开户行" prop="tradeBankName">
        <el-select v-model="queryParams.tradeBankName" placeholder="请选择开户行" filterable clearable
          :style="{width: '100%'}">
          <el-option v-for="(item, index) in bankOption" :key="index" :label="item.label"
            :value="item.value" :disabled="item.disabled"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="交易日期" prop="transactionTime">
        <el-date-picker clearable
          v-model="queryParams.transactionTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择交易发生时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="查看错误数据" prop="sync_type" label-width="100px">
        <el-select v-model="queryParams.syncType" placeholder="请选择账号" filterable clearable
          :style="{width: '100%'}">
          <el-option v-for="(item, index) in showWaringOption" :key="index" :label="item.value"
            :value="item.name" :disabled="item.disabled"></el-option>
        </el-select>
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
          v-hasPermi="['system:verify1:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
            <el-button
              type="info"
              plain
              icon="el-icon-upload2"
              size="mini"
              @click="handleImport"
            >导入</el-button>
          </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-c-scale-to-original"
          size="mini"
          @click="handleCheck"
        >全量校验</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-c-scale-to-original"
          size="mini"
          @click="handleOneAccountCheck"
        >校验当前账号</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-c-scale-to-original"
          size="mini"
          @click="handleSplitReceipt"
        >回单拆分</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="verify1List"
    :row-class-name="showErrorData" height = "550" max-height="800" resizable = true>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="id">
              <span>{{ props.row.id }}</span>
            </el-form-item>
            <el-form-item label="账号">
              <span>{{ props.row.tradeAccountNum }}</span>
            </el-form-item>
            <el-form-item label="流水号">
              <span>{{ props.row.serialNumber }}</span>
            </el-form-item>
            <el-form-item label="户名">
              <span>{{ props.row.tradeAccountName }}</span>
            </el-form-item>
            <el-form-item label="开户行">
              <span>{{ props.row.tradeBankName }}</span>
            </el-form-item>
            <el-form-item label="对方账号">
              <span>{{ props.row.counterpartyAccountNum }}</span>
            </el-form-item>
            <el-form-item label="对方户名">
              <span>{{ props.row.counterpartyAccountName }}</span>
            </el-form-item>
            <el-form-item label="对方开户行">
              <span>{{ props.row.counterpartyBankName }}</span>
            </el-form-item>
            <el-form-item label="交易金额">
              <span>{{ props.row.transactionAmount }}</span>
            </el-form-item>
            <el-form-item label="交易时间">
              <span>{{ props.row.transactionTime }}</span>
            </el-form-item>
            <el-form-item label="交易类型">
              <span>{{ props.row.transactionType }}</span>
            </el-form-item>
            <el-form-item label="备注">
              <span>{{ props.row.remark }}</span>
            </el-form-item>
            <el-form-item label="币种">
              <span>{{ props.row.currency }}</span>
            </el-form-item>
            <el-form-item label="余额">
              <span>{{ props.row.balance }}</span>
            </el-form-item>
            <el-form-item label="创建时间">
              <span>{{ props.row.createTime }}</span>
            </el-form-item>
            <el-form-item label="修改时间">
              <span>{{ props.row.lastModifyTime }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="id" width="60" align="center" prop="id"></el-table-column>
      <el-table-column label="账号" width="120" align="center" prop="tradeAccountNum" show-overflow-tooltip></el-table-column>
      <el-table-column label="流水号" width="120" align="center" prop="serialNumber" show-overflow-tooltip></el-table-column>
      <el-table-column label="户名" width="120" align="center" prop="tradeAccountName" show-overflow-tooltip></el-table-column>
      <el-table-column label="开户行" width="120" align="center" prop="tradeBankName" show-overflow-tooltip></el-table-column>
      <el-table-column label="对方账号" width="120" align="center" prop="counterpartyAccountNum" show-overflow-tooltip></el-table-column>
      <el-table-column label="对方户名" width="120" align="center" prop="counterpartyAccountName" show-overflow-tooltip></el-table-column>
      <el-table-column label="对方开户行" width="120" align="center" prop="counterpartyBankName" show-overflow-tooltip></el-table-column>
      <el-table-column label="交易金额" width="100" align="center" prop="transactionAmount" />
      <el-table-column label="交易时间" align="center" prop="transactionTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.transactionTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易类型" align="center" prop="transactionType" />
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip></el-table-column>
      <el-table-column label="币种" align="center" prop="currency" />
      <el-table-column label="余额" align="center" prop="balance" show-overflow-tooltip></el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="修改时间" align="center" prop="lastModifyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastModifyTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:verify1:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleSetBaseLine(scope.row)"
          >设置基线</el-button>
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:verify1:remove']"
          >删除</el-button> -->
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

    <!-- 添加或修改明细详情校验宽对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="明细流水号" prop="serialNumber">
          <el-input v-model="form.serialNumber" placeholder="请输入明细流水号" />
        </el-form-item>
        <el-form-item label="银行账号" prop="tradeAccountNum">
          <el-input v-model="form.tradeAccountNum" placeholder="请输入银行账号" />
        </el-form-item>
        <el-form-item label="户名" prop="tradeAccountName">
          <el-input v-model="form.tradeAccountName" placeholder="请输入户名" />
        </el-form-item>
        <el-form-item label="开户行" prop="tradeBankName">
          <el-input v-model="form.tradeBankName" placeholder="请输入开户行" />
        </el-form-item>
        <el-form-item label="对方账号" prop="counterpartyAccountNum">
          <el-input v-model="form.counterpartyAccountNum" placeholder="请输入对方账号" />
        </el-form-item>
        <el-form-item label="对方户名" prop="counterpartyAccountName">
          <el-input v-model="form.counterpartyAccountName" placeholder="请输入对方户名" />
        </el-form-item>
        <el-form-item label="对方银行" prop="counterpartyBankName">
          <el-input v-model="form.counterpartyBankName" placeholder="请输入对方银行" />
        </el-form-item>
        <el-form-item label="交易金额" prop="transactionAmount">
          <el-input v-model="form.transactionAmount" placeholder="请输入交易金额" />
        </el-form-item>
        <el-form-item label="交易类型" prop="transactionType">
          <el-input v-model="form.transactionType" placeholder="请输入交易类型" />
        </el-form-item>
        <el-form-item label="交易时间" prop="transactionTime">
          <el-date-picker clearable
            v-model="form.transactionTime"
            type="datetime"
            step="1"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择交易发生时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="币种" prop="currency">
          <el-input v-model="form.currency" placeholder="请输入币种" />
        </el-form-item>
        <el-form-item label="余额" prop="balance">
          <el-input v-model="form.balance" placeholder="请输入余额" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="1700px" append-to-body >
      <el-form ref="uploadForm" :model="uploadForm" :rules="uploadRules" label-width="150px" class="two-columns-form">
        <el-form-item label="回单明细id" prop="multipleSelection" >
          <el-input v-model="uploadForm.multipleSelection" :disabled="true" placeholder="请选择回单对应的明细 *注：需要选择回单对应的所有明细数据，条数和顺序不一致都会产生数据对应准确性问题" />
        </el-form-item>
        <el-form-item label="每页回单的数量" prop="type">
          <el-input v-model.number="uploadForm.type" type="number" :min="1" :max="5" placeholder="请输入每页回单中的数量（1-5）" />
        </el-form-item>
        <el-form-item label="回单裁剪上边距" prop="topMargin">
          <el-input v-model.number="uploadForm.topMargin" type="number" placeholder="回单裁剪上边距" />
        </el-form-item>
        <el-form-item label="回单裁剪下边距" prop="bottomMargin">
          <el-input v-model.number="uploadForm.bottomMargin" placeholder="回单裁剪下边距" />
        </el-form-item>
        <el-form-item label="回单顶端文字匹配" prop="topText">
          <el-tag
            :key="tag"
            v-for="tag in dynamicTags"
            closable
            :disable-transitions="false"
            @close="handleClose(tag)">
            {{tag}}
          </el-tag>
          <el-input
            class="input-new-tag"
            v-if="inputVisible"
            v-model="inputValue"
            ref="saveTagInput"
            size="small"
            @keyup.enter.native="handleInputConfirm"
            @blur="handleInputConfirm"
          >
          </el-input>
          <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>
        </el-form-item>
        <el-form-item label="回单结尾文字匹配" prop="topText">
          <el-tag
            :key="tag"
            v-for="tag in dynamicEndTags"
            closable
            :disable-transitions="false"
            @close="handleEndClose(tag)">
            {{tag}}
          </el-tag>
          <el-input
            class="input-new-tag"
            v-if="inputEndVisible"
            v-model="inputValue"
            ref="saveEndTagInput"
            size="small"
            @keyup.enter.native="handleInputEndConfirm"
            @blur="handleInputEndConfirm"
          >
          </el-input>
          <el-button v-else class="button-new-tag" size="small" @click="showEndInput">+ New Tag</el-button>
        </el-form-item>
        <!-- <el-form-item label="是否上传司库" prop="isPushFlag">
          <el-switch prop="isPushFlag"
            v-model="uploadForm.isPushFlag"
            active-color="#13ce66"
            inactive-color="#ff4949">
          </el-switch>
        </el-form-item> -->
        <!-- <el-form-item label="是否是倒序排列数据" prop="isDesc">
          <el-switch prop="isDesc"
            v-model="uploadForm.isDesc"
            active-color="#13ce66"
            inactive-color="#ff4949">
          </el-switch>
        </el-form-item> -->
        <el-form-item label="上传对应银行明细文件" prop="isPushFlag">
          <el-upload
            ref="upload"
            :limit="1"
            accept=".zip, .pdf"
            :file-list="fileList"
            :headers="upload.headers"
            :action="upload.url"
            :disabled="upload.isUploading"
            :on-progress="handleFileUploadProgress"
            :auto-upload="false"
            :on-change="uploadFile"
            :data="uploadForm"
            :on-remove="handleRemove"
            >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传xls/xlsx文件</div>
          </el-upload>
        </el-form-item>

      </el-form>
      <el-table ref="multipleTable" v-loading="loading" :data="verify1List" height = "550" max-height="800" resizable = true @selection-change="handleSelectionChange" row-key="id">
        <el-table-column type="selection" width="55" reserve-selection></el-table-column>
        <el-table-column label="id" width="60" align="center" prop="id"></el-table-column>
        <el-table-column label="账号" width="120" align="center" prop="tradeAccountNum" show-overflow-tooltip></el-table-column>
        <el-table-column label="流水号" width="120" align="center" prop="serialNumber" show-overflow-tooltip></el-table-column>
        <el-table-column label="户名" width="120" align="center" prop="tradeAccountName" show-overflow-tooltip></el-table-column>
        <el-table-column label="开户行" width="120" align="center" prop="tradeBankName" show-overflow-tooltip></el-table-column>
        <el-table-column label="对方账号" width="120" align="center" prop="counterpartyAccountNum" show-overflow-tooltip></el-table-column>
        <el-table-column label="对方户名" width="120" align="center" prop="counterpartyAccountName" show-overflow-tooltip></el-table-column>
        <el-table-column label="对方开户行" width="120" align="center" prop="counterpartyBankName" show-overflow-tooltip></el-table-column>
        <el-table-column label="交易金额" width="100" align="center" prop="transactionAmount" />
        <el-table-column label="交易时间" align="center" prop="transactionTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.transactionTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="交易类型" align="center" prop="transactionType" />
        <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip></el-table-column>
        <el-table-column label="币种" align="center" prop="currency" />
        <el-table-column label="余额" align="center" prop="balance" show-overflow-tooltip></el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="修改时间" align="center" prop="lastModifyTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.lastModifyTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="cancelUpload">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listVerify, listAccount,listBank,insertTransaction,updateTransaction,getVerifyByid,setBaseLine,checkTask,checkTransaction } from "@/api/system/verify";
import { getToken } from "@/utils/auth";
import axios from 'axios'
import json from "highlight.js/lib/languages/json";
export default {
  name: "receipt",
  data() {
    return {
      dynamicTags: [],
      inputVisible: false,
      inputValue: '',
      dynamicEndTags: [],
      inputEndVisible: false,
      fileList: [],
      showWaringOption: [
        { name: 1, value:'是' }, { name: 0, value: '否' }, {name:null,value: '全部'}
      ],
      uploadRules: {
        handleType: [
          { required: true, message: "解析类型不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: '此项为必填', trigger: 'blur' }, // 必填验证
          { type: 'number', min: 1, max: 5, message: '请输入1到5之间的数字', trigger: 'blur' } // 数字范围验证
        ]
      },
      uploadForm: {
        type: '',
        topMargin: 0,
        bottomMargin: 0,
        topText: [],
        bottomText: [],
        fileStream: null,
        order: null,
        multipleSelection: []
      },
      uf: {},
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
      // 明细详情校验宽表格数据
      verify1List: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serialNumber: null,
        tradeAccountNum: null,
        tradeAccountName: null,
        tradeBankName: null,
        counterpartyAccountNum: null,
        counterpartyAccountName: null,
        counterpartyBankName: null,
        transactionAmount: null,
        transactionTime: null,
        transactionType: null,
        currency: null,
        balance: null,
        detailSqlNo: null,
        syncType: null,
        lastModifyTime: null,
        isActive: null
      },
      accountOption: [],
      bankOption: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        serialNumber: [
          { required: true, message: "明细流水号不能为空", trigger: "blur" }
        ],
        tradeAccountNum: [
          { required: true, message: "银行账号不能为空", trigger: "blur" }
        ],
        tradeBankName: [
          { required: true, message: "开户行不能为空", trigger: "blur" }
        ],
        tradeAccountName: [
          { required: true, message: "户名不能为空", trigger: "blur" }
        ],
        transactionAmount: [
          { required: true, message: "交易金额不能为空", trigger: "blur" }
        ],
        transactionTime: [
          { required: true, message: "交易发生时间不能为空", trigger: "blur" }
        ],
        transactionType: [
          { required: true, message: "交易类型不能为空", trigger: "change" }
        ],
        currency: [
          { required: true, message: "币种不能为空", trigger: "blur" }
        ],
        balance: [
          { required: true, message: "余额不能为空", trigger: "blur" }
        ],

      },
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { "content-type": "multipart/form-data", Authorization: "Bearer " + getToken()},
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/convert/detail/upload"
      },

    };
  },
  created() {
    this.getList();
    this.getListAccount();
    this.getListBank();
  },
  methods: {
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },
    handleEndClose(tag){
      this.dynamicEndTags.splice(this.dynamicEndTags.indexOf(tag),1)
    },

    showInput() {
      this.inputVisible = true;
        this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    showEndInput() {
      this.inputEndVisible = true;
        this.$nextTick(_ => {
        this.$refs.saveEndTagInput.$refs.input.focus();
      });
    },

    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.dynamicTags.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    },
    handleInputEndConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.dynamicTags.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    },
    handleSelectionChange(val) {
      if(val.length > 0){
        this.uploadForm.multipleSelection = val.map(item => item.id);
      }
    },
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    handleSplitReceipt() {
      this.reset();
      this.upload.title = "回单拆分";
      this.upload.open = true;
    },
    handleRemove() {
      this.fileList = []
    },
    cancelUpload() {
      this.fileList = [];
      this.uploadForm = {
        tradeAccountName: null,
        tradeAccountNum: null,
        tradeBankName: null,
        handleType: null,
        isPushFlag: true,
        resJsonType: "回单",
        isDesc: false,
      }
      this.upload.open = false;
      this.upload.isUploading = false;
    },
    uploadFile(file) {
      this.fileList = [];
      this.fileList.push(file);
    },
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    submitFileForm() {
      this.$refs["uploadForm"].validate(valid => {
        if (valid) {
          const formData = new FormData();
          if (this.fileList.length == 0) {
            alert("请选择上传的文件")
            return
          }
          formData.append('file', this.fileList[0].raw);
          formData.append('tradeAccountName', this.uploadForm.tradeAccountName);
          formData.append('tradeAccountNum', this.uploadForm.tradeAccountNum);
          formData.append('tradeBankName', this.uploadForm.tradeBankName);
          formData.append('handleType', this.uploadForm.handleType);
          formData.append('isPushFlag', this.uploadForm.isPushFlag);
          formData.append('resJsonType', this.uploadForm.resJsonType);
          formData.append('isDesc', this.uploadForm.isDesc);
          const config = {
            headers: this.upload.headers // 将请求头配置放入一个对象中
          };
          axios.post(this.upload.url,formData ,config)
          .then(response => {
            // 文件上传成功后的处理逻辑
            this.fileList = []
            this.upload.open = false;
            this.upload.isUploading = false;
            this.$refs.upload.clearFiles();
            this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.data.message + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
            this.getList();
          })
          .catch(error => {
            // 文件上传失败后的处理逻辑
            alert('文件上传失败:', error)
          });
        }
      });
    },
    handleRemove(file, fileList) {
        console.log(file, fileList);
      },
      handlePreview(file) {
        console.log(file);
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      beforeRemove(file, fileList) {
        return this.$confirm(`确定移除 ${ file.name }？`);
      },
    showErrorData({row, rowIndex}){
       if(row.syncType === 1){
        return 'warning-row';
       }
       return '';
    },
    convertTransactionData(data){
      return {
        "standardTransaction": data
      }
    },
    getListBank() {
        listBank().then(response => {
          this.bankOption = response.data.map((item,index) => ({
            lable: item,
            value: item
          }))
        })
    },
    getListAccount(){
        listAccount().then(response => {
          this.accountOption = response.data.map((item,index) => ({
            lable: item,
            value: item
          }))
        });
    },
    /** 查询明细详情校验宽列表 */
    getList() {
      this.loading = true;
      listVerify(this.queryParams).then(response => {
        this.verify1List = response.rows;
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
        serialNumber: null,
        tradeAccountNum: null,
        tradeAccountName: null,
        tradeBankName: null,
        counterpartyAccountNum: null,
        counterpartyAccountName: null,
        counterpartyBankName: null,
        transactionAmount: null,
        transactionTime: null,
        transactionType: null,
        remark: null,
        currency: null,
        balance: null,
        syncType: null
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加明细详情";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getVerifyByid(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改明细详情";
      });
    },
    handleSetBaseLine(row) {
      this.reset();
      setBaseLine({tradeAccountNum:row.tradeAccountNum,id:row.id}).then(response => {
        debugger
        if(response.code == 200){
          this.$modal.msgSuccess("设置基线成功");
          this.getList();
        }else{
          this.$notify.error({
            title: '错误',
            message: this.queryParams.tradeAccountNum + '单账号校验失败' + response.message
          });
        }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTransaction(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            insertTransaction(this.convertTransactionData(this.form)).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleCheck(){
      checkTask().then(response => {
        if (response.code == 200) {
          this.$notify({
            title: '成功',
            message: '全量账号校验成功',
            type: 'success'
          });
          this.getList()
        } else {
          this.$notify.error({
            title: '错误',
            message: response.data.message
          });
        }
      })
    },
    handleOneAccountCheck() {
      if (this.queryParams.tradeAccountNum == null || this.queryParams.tradeAccountNum == "") {
        this.$notify.info({
          title: '消息',
          message: '请在筛选条件中选择账号'
        });
        return
      }
      checkTransaction(this.queryParams.tradeAccountNum).then(response => {
        if (response.code == 200) {
          this.$notify({
            title: '成功',
            message: this.queryParams.tradeAccountNum + '账号校验成功',
            type: 'success'
          });
          this.getList();
          return;
        }
        this.$notify.error({
          title: '错误',
          message: this.queryParams.tradeAccountNum + '单账号校验失败' + response.message
        });
      })
    },
    handleImport() {
      this.upload.title = "明细导入";
      this.upload.open = true;
    },
  }
};
</script>
<style>
  .el-table .warning-row {
    background: rgb(238, 250, 3);
  }
  .demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
  .two-columns-form {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
  }

  .two-columns-form .el-form-item {
    flex-basis: calc(50% - 10px); /* 减去间隔，可根据需要调整 */
    margin-bottom: 10px;
  }

  /* 如果需要调整特定表单项的宽度，可以单独设置 */
  .two-columns-form .el-form-item.width-full {
    flex-basis: 100%;
  }
</style>
