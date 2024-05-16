package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.web.service.IDeviceService;
import com.ruoyi.rpa.web.service.IHistoryBalanceService;
import com.ruoyi.rpa.web.service.IMechanicalArmService;
import com.ruoyi.system.domain.rpa.AccountDetailsPageDto;
import com.ruoyi.system.domain.rpa.cabinet.CabinCode;
import com.ruoyi.system.domain.rpa.cabinet.CloseCabinetInfo;
import com.ruoyi.system.domain.rpa.transaction.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/websocket")
@Api(tags = "数据上传控制类", description = "开关机箱，推送数据接口")
public class JavaClientController {
    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IHistoryBalanceService historyBalanceService;

    @Autowired
    private IMechanicalArmService mechanicalArmService;

    @GetMapping("/health")
    public String health(){
        return "hello";
    }

    //开1082机柜
    @ApiOperation("开启对应机柜端口")
    @GetMapping("/openDevice/{ip_index}/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机柜id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "ip_index", value = "IP地址索引", required = true, dataType = "Integer", paramType = "path")
    })
    public AjaxResult openDevice1(@PathVariable("id") Long id, @PathVariable("ip_index") Integer serverIpIndex) {
        return AjaxResult.success(deviceService.openDevice(id, serverIpIndex));
    }

    @ApiOperation("开启对应机柜端口")
    @GetMapping("/openDevice1/{cabinetCode}/{id}")
    public AjaxResult openDevice(@PathVariable("id") Long id, @PathVariable("cabinetCode") String cabinetCode) {
        return deviceService.openDevice(id, cabinetCode);
    }


    @ApiOperation("开启对应机柜端口")
    @GetMapping("/closeDevice1/{cabinetCode}/{id}")
    public AjaxResult closeDevice(@PathVariable("id") Long id, @PathVariable("cabinetCode") String cabinetCode) {
        return deviceService.closeDevice(id, cabinetCode);
    }

    //关1082机柜对应口子
    @GetMapping("/closeDevice/{ip_index}/{id}")
    @ApiOperation(value = "关闭机柜端口", response = AjaxResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机柜id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "ip_index", value = "IP地址索引", required = true, dataType = "Integer", paramType = "path")
    })
    public AjaxResult closeDevice1(@PathVariable("id") Long id,@PathVariable("ip_index") Integer serverIpIndex) {
        return AjaxResult.success(deviceService.closeDevice(id, serverIpIndex));
    }

    @PostMapping("/closeDevice")
    @ApiOperation(value = "批量关闭机柜端口", response = AjaxResult.class)
    public AjaxResult batchCloseDevice(@RequestBody List<CloseCabinetInfo> closeCabinetInfoList) {
        final String message = deviceService.batchCloseDevice(closeCabinetInfoList);
        if (message.contains("无法获取ter_id信息") || message.contains("关闭机柜信息不能为空")){
            return AjaxResult.error("关闭信息有误，已关闭所有可关闭的端口");
        }
        return AjaxResult.success(message);
    }

    @ApiOperation(value = "通过机柜编码获取所有已开启端口信息", response = AjaxResult.class)
    @PostMapping("/getCabinetInfo")
    public AjaxResult getCabinetInfo(@RequestBody List<CabinCode> cabinCodes) {
        return AjaxResult.success(deviceService.getCabinetInfo(cabinCodes));
    }

    @PostMapping("/compareCloseDevice")
    @ApiOperation(value = "批量关闭已经开启的机柜端口", response = AjaxResult.class)
    public AjaxResult compareCloseDevice(@RequestBody List<CloseCabinetInfo> closeCabinetInfoList) {
        return deviceService.compareCloseDevice(closeCabinetInfoList);
    }

    @PostMapping("/historyBalance")
    @ApiOperation(value = "余额推送", response = AjaxResult.class)
    public AjaxResult historyBalance(@RequestBody BalanceDto balanceDto) throws IOException {
        return historyBalanceService.historyBalance(balanceDto);
    }

    @PostMapping("/electronicEntry")
    @ApiOperation(value = "回单详情推送", response = AjaxResult.class)
    public AjaxResult ElectronicEntry(@RequestBody ElectronicTransactionDto electronicTransactionDto) throws IOException {
        return historyBalanceService.electronicTransaction(electronicTransactionDto);
    }

    @PostMapping("/electronicFile")
    @ApiOperation(value = "回单文件推送", response = AjaxResult.class)
    public AjaxResult ElectronicFile(@RequestBody ElectronicFileDto electronicFileDto) throws IOException {
        return AjaxResult.success(historyBalanceService.electronicFile(electronicFileDto));
    }

    @PostMapping("/transaction")
    @ApiOperation(value = "明细推送", response = AjaxResult.class)
    public AjaxResult transaction(@RequestBody TransactionDto transactionDto) throws IOException {
        return historyBalanceService.transaction(transactionDto.getStandardTransaction());
    }

    @PostMapping("/BatchTransaction")
    @ApiOperation(value = "批量明细推送", response = AjaxResult.class)
    public AjaxResult transaction(@RequestBody BatchStrandTransaction transactionDtoList) throws IOException {
        return historyBalanceService.transactionList(transactionDtoList);
    }

    @GetMapping("/mechanicalArm/{ip_index}/{id}")
    @ApiOperation(value = "机械臂按压", response = AjaxResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机柜id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "ip_index", value = "IP地址索引", required = true, dataType = "Integer", paramType = "path")
    })
    public AjaxResult mechanicalArm( @PathVariable("ip_index") Integer ipIndex, @PathVariable("id") Long id) {
        return AjaxResult.success(mechanicalArmService.mechanicalArm(id, ipIndex));
    }

    @PostMapping({"/transactionDetails/listByPage"})
    @ApiOperation(value="账户明细分页查询", response=AjaxResult.class)
    public AjaxResult accountDetailsPage(@RequestBody AccountDetailsPageDto accountDetailsPageDto) throws IOException {
        return AjaxResult.success(historyBalanceService.accountDetailsPage(accountDetailsPageDto));
    }

}