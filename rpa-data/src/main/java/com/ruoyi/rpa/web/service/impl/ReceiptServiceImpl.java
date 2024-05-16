package com.ruoyi.rpa.web.service.impl;

import com.ruoyi.rpa.web.service.ReceiptService;
import com.ruoyi.system.domain.rpa.transaction.ReceiptDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {
    @Override
    public void pushRecepit(ReceiptDto receiptDto) throws IOException {

    }

    @Override
    public void splitReceiptPDF(ReceiptDto receiptDto) throws Exception {

    }

//    @Autowired
//    private IHistoryBalanceService historyBalanceService;
//
//    @Override
//    public void pushRecepit(ReceiptDto receiptDto) throws IOException {
//        final String filePath = receiptDto.getFilePath();
//        if (StringUtils.isEmpty(filePath)){
//            log.error("文件路径不能为空");
//            throw new GlobalException(FILE_PATH_EMPTY_ERROR);
//        }
//        // 扫描出zip文件 并解出来
//        final TreeMap<String, byte[]> receiptMap = FileUtil.readZipFile(FileUtil.scanZipFiles(receiptDto.getFilePath()).get(0));
//        if (Objects.isNull(receiptMap)){
//            log.error("解析回单文件失败" + receiptDto.getFilePath());
//        }
//        // 拿出来json文件 解析
//        final byte[] receiptByte = Files.readAllBytes(Paths.get(receiptDto.getFilePath() + RECEIPT_FILE_JSON_NAME));
//        final ReceiptDto receiptObject = JsonMapper.getObjectMapper().readValue(receiptByte, ReceiptDto.class);
//        if (Objects.isNull(receiptObject)){
//            log.error("解析回单json文件失败，请检查上传文件");
//            throw new RuntimeException("解析回单json文件失败");
//        }
//        // 调用文件拆分方法 并且处理掉拆分后冗余的页
//        BeanUtils.copyProperties(receiptDto,receiptObject,"electronicList");
//        final ArrayList<byte[]> spiltFiles = checkAndHandlePDF(receiptMap, receiptObject);
//        // 存储拆分后的文件 用于后续调接口使用
//        saveByteArraysAsZip(spiltFiles, receiptDto.getSavePath(), receiptObject);
//        // 调用上传接口
//        if (receiptDto.isUpload()){
//            uploadCashier(receiptObject,receiptDto.getSavePath());
//        }
//    }
//
//    @Override
//    public void splitReceiptPDF(ReceiptDto receiptDto) throws Exception {
//        String filePath = receiptDto.getFilePath();
//        checkPdfFile(filePath);
//        //生成pdf文件的字节数组
//        byte[] pdfBytes = FileUtil.convertToByteArray(filePath);
//        ArrayList<byte[]> splitPdfBytes;
//        final OcrSplit ocrSplit = new OcrSplit();
//        BeanUtils.copyProperties(receiptDto,ocrSplit);
//        // 解析拆分pdf
//        if (Integer.valueOf(receiptDto.getType()) == 1){
//            splitPdfBytes = PDFSplitUtil.splitByPage(1, pdfBytes, 0, 0);
//        } else {
//            splitPdfBytes = PaddleMain.getSplitPdfZip(filePath, pdfBytes,ocrSplit);
//        }
//        // 写拆分后文件
//        checkPdfAndReceiptList(splitPdfBytes,receiptDto.getStandardTransactionList());
//        if (receiptDto.getOrder().equals(SystemConstant.ORDER_DESC_FLAG)){
//            Collections.reverse(receiptDto.getStandardTransactionList());
//        }
//        final Zips zips = new Zips();
//        for (int i = 0; i < receiptDto.getStandardTransactionList().size(); i++) {
//            String fileName = receiptDto.getStandardTransactionList().get(i).getSerialNumber() +(Integer.valueOf(receiptDto.getType()) == 1? ".pdf":".png");
//            zips.writeEntry(fileName, splitPdfBytes.get(i));
//        }
//        try (FileOutputStream fileOutputStream = new FileOutputStream(Objects.isNull(receiptDto.getSavePath())?"default/receipt":receiptDto.getSavePath())) {
//            fileOutputStream.write(zips.toByteArray());
//        }
//        // 推送
//        if (receiptDto.isUpload()){
//            uploadCashier(receiptDto,receiptDto.getSavePath());
//        }
//    }
//
//    private void checkPdfAndReceiptList(ArrayList<byte[]> splitPdfBytes, List<StandardTransaction> StandardTransactionList) {
//        if (CollectionUtils.isEmpty(splitPdfBytes)){
//            throw new GlobalException(PARSING_PDF_FILE_IS_EMPTY);
//        }
//        if (CollectionUtils.isEmpty(StandardTransactionList)){
//            throw new GlobalException(PARAMETER_IS_NULL_ERROR);
//        }
//        if (StandardTransactionList.size() < splitPdfBytes.size()){
//            throw new GlobalException(THE_QUANTITY_OF_RECEIPTS_NOT_MATCH_ERROR_MORE);
//        }
//        if (StandardTransactionList.size() > splitPdfBytes.size()){
//            throw new GlobalException(THE_QUANTITY_OF_RECEIPTS_NOT_MATCH_ERROR_LESS);
//        }
//    }
//
//    private void checkPdfFile(String filePath){
//        if (StringUtils.isEmpty(filePath) || !filePath.toLowerCase().endsWith(".pdf")) {
//            log.error("文件路径不能为空且必须为pdf文件");
//            throw new GlobalException(FILE_PATH_EMPTY_ERROR);
//        }
//    }
//
//    private ArrayList<byte[]> checkAndHandlePDF(TreeMap<String,byte[]> receiptMap, ReceiptDto receiptObject) {
//        final Integer numberOfSpilt = Integer.valueOf(receiptObject.getType());
//        final int numberOfReceipt = receiptObject.getStandardTransactionList().size();
//        if (Objects.isNull(numberOfSpilt) || Objects.isNull(numberOfReceipt) || numberOfReceipt == 0 || numberOfSpilt == 0){
//            log.error("拆分参数不合法");
//            throw new RuntimeException("拆分参数不合法");
//        }
//        // 存储生成的文件流
//        final ArrayList<byte[]> spiltFilesList = new ArrayList<>();
//        for (Map.Entry<String, byte[]> entry : receiptMap.entrySet()) {
//            spiltFilesList.addAll(PDFSplitUtil.splitByPage(numberOfSpilt, entry.getValue(), (float) receiptObject.getTopMargin(), (float) receiptObject.getBottomMargin()));
//        }
//        if (CollectionUtils.isEmpty(spiltFilesList)){
//            throw new GlobalException(PARSING_PDF_FILE_IS_EMPTY);
//        }
//        // TODO: 2024/1/30 这块逻辑有问题， // 不确定是否有问题，把多拆出来的处理掉 这种情况无法判断是否是数据问题
//        if (spiltFilesList.size() < numberOfReceipt){
//            // 证明回单少了 这种情况肯定有问题
//            throw new GlobalException(THE_QUANTITY_OF_RECEIPTS_NOT_MATCH_ERROR_LESS);
//        } else if (spiltFilesList.size() > numberOfReceipt){
//            // 解析出的回单文件比列表给的多 ，不确定是不是pdf有空白
//            if ((spiltFilesList.size() - numberOfReceipt) > numberOfSpilt){
//                //空白页大于拆分数 这种也肯定有问题
//                throw new GlobalException(THE_QUANTITY_OF_RECEIPTS_NOT_MATCH_ERROR_MORE);
//            }else {
//                // 不确定是否有问题，这种情况就兜底，把小于50kb的给舍弃掉然后再处理一次
//                Iterator<byte[]> iterator = spiltFilesList.iterator();
//                while (iterator.hasNext()) {
//                    byte[] byteArray = iterator.next();
//                    long fileSize = byteArray.length / 1024; // 转换为KB
//
//                    if (fileSize < 50) {
//                        iterator.remove();
//                    }
//                }
//                final int size = spiltFilesList.size();
//                if(size != numberOfReceipt){
//                    throw new GlobalException(THE_QUANTITY_OF_RECEIPTS_NOT_MATCH_ERROR_MORE);
//                }
//            }
//        }
//        return spiltFilesList;
//    }
//
//    private static void saveByteArraysAsZip(ArrayList<byte[]> byteArrayArrayList, String zipFilePath,ReceiptDto receiptObject) {
//        final List<StandardTransaction> electronicGroup = receiptObject.getStandardTransactionList();
//        if (receiptObject.getOrder().equals(SystemConstant.ORDER_DESC_FLAG)){
//            Collections.reverse(electronicGroup);
//        }
//        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
//            ZipOutputStream zipOut = new ZipOutputStream(fos)) {
//
//            for (int i = 0; i < byteArrayArrayList.size(); i++) {
//                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
////                byteOut.reset();
//                byte[] byteArray = byteArrayArrayList.get(i);
//                String entryName = electronicGroup.get(i).getSerialNumber()+ ".pdf";
//
//                byteOut.write(byteArray);
//                ZipEntry zipEntry = new ZipEntry(entryName);
//                zipOut.putNextEntry(zipEntry);
//                zipOut.write(byteOut.toByteArray());
//                zipOut.closeEntry();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 上传收银信息
//     *
//     * @param receiptDto 收据数据传输对象，包含需要上传的收银交易信息
//     * @param path 文件路径，用于指定待上传的电子文件位置
//     * @throws IOException 如果处理过程中发生文件读写错误，则抛出IOException
//     */
//    public void uploadCashier(ReceiptDto receiptDto, String path) throws IOException {
//        // 初始化批量交易对象并设置交易列表
//        BatchStrandTransaction batchStrandTransaction = new BatchStrandTransaction();
//        batchStrandTransaction.setStandardTransactionList(receiptDto.getStandardTransactionList());
//
//        // 调用历史余额服务，处理交易列表，并获取处理结果
//        String s = (String)this.historyBalanceService.transactionList(batchStrandTransaction.getStandardTransactionList());
//
//        // 解析处理结果
//        JSONObject jsonObject = JSONObject.parseObject(s);
//
//        // 根据处理结果的代码判断是否成功，若成功则上传电子文件
//        if (jsonObject.getString("code").equals("SUCCESS")) {
//            ElectronicFileDto electronicFileDto = new ElectronicFileDto();
//            electronicFileDto.setFile(path); // 设置电子文件路径
//            this.historyBalanceService.electronicFile(electronicFileDto); // 上传电子文件
//        }
//    }

}

