package com.ruoyi.rpa.util;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import com.ruoyi.system.domain.rpa.ConvertData;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
public class ExcelUtil {

    private static final String CASHIRE_PUSH_TEMPLATE_LIB = "lib/";

    private static final String CASHIRE_PUSH_TEMPLATE_FILE_NAME = "非直联账户明细维护批量导入-导入模板.xls";

    public static List<LinkedHashMap<String, Object>> importExcelData(Workbook workbook, Integer dataNo) {
        log.info("Handling Excel data");
        Sheet sheet = workbook.getSheetAt(0); // 获取excel的sheet
        if (sheet == null) {
            return null;
        }

        List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
        Row rowTitle = sheet.getRow(dataNo);

        // 解析列表头
        ArrayList<String> dataHeaderList = new ArrayList<>();
        Row dataHeaderRow = sheet.getRow(dataNo);
        if (Objects.isNull(dataHeaderRow)) {
            log.error("Unable to parse header data");
            return null;
        }

        for (int cellNum = 0; cellNum < rowTitle.getLastCellNum(); cellNum++) {
            Cell cell = dataHeaderRow.getCell(cellNum);
            if (cell == null) {
                continue;
            }

            try {
                dataHeaderList.add(StringUtils.trimAllWhitespace(getCellValue(cell).toString()));
            } catch (Exception e) {
                log.error("Error parsing data header: {}", e.getMessage());
            }
        }

        // 循环获取excel每一行
        for (int rowNum = dataNo + 1; rowNum < sheet.getLastRowNum() + 1; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }

            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            // 循环获取excel每一行的每一列
            for (int cellNum = 0; cellNum < rowTitle.getLastCellNum(); cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (cell == null) {
                    // 防止单元格为空
                    map.put(dataHeaderList.get(cellNum), "-");
                    continue;
                }

                try {
                    map.put(dataHeaderList.get(cellNum).trim(), Objects.isNull(getCellValue(cell)) || StringUtils.isEmpty(getCellValue(cell).toString()) ? "-" : getCellValue(cell));
                } catch (Exception e) {
                    log.error("Error getting cell value: {}", e.getMessage());
                }
            }

            mapList.add(map);
        }
        return mapList;
    }

    /*
     *  解析excel非列表内的数据
     *  title:开始解析的行
     *  @return:HashMap<row+","+cell,object>
     * */
    private static HashMap<String, Object> readExcelHead(Workbook xwb, Integer dataStart, Integer dataEnd) {
        HashMap<String, Object> excelIndexMap = new HashMap<>();

        if (xwb != null) {
            Sheet sheet = xwb.getSheetAt(0); // 获取excel的sheet
            if (sheet != null) {
                Row rowTitle = sheet.getRow(dataStart);

                for (int rowNum = dataStart; rowNum < dataEnd; rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }

                    // 循环获取excel每一行的每一列
                    for (int cellNum = 0; cellNum < rowTitle.getLastCellNum(); cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        if (cell == null) {
                            // 防止单元格为空
                            excelIndexMap.put(rowNum + "," + cellNum, "");
                            continue;
                        }

                        try {
                            Object cellValue = getCellValue(cell);
                            if (Objects.nonNull(cellValue)) {
                                excelIndexMap.put(rowNum + "," + cellNum, cellValue);
                            }
                        } catch (Exception e) {
                            log.error("获取单元格内容失败", e);
                        }
                    }
                }
            }
        }

        return excelIndexMap;
    }

    public static <T> HashMap<String, Object> readExcelIndexData(Workbook xwb, Class<T> clazz, Integer dataStart, Integer dataEnd) {
        // 解析表头的数据
        HashMap<String, Object> excelIndexMap = readExcelHead(xwb, dataStart, dataEnd);
        // 对应传入的实体类有用的数据
        HashMap<String, Object> excelHeadMap = new HashMap<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields
        ) {
            // 存在注解就需要去取值
            if (field.isAnnotationPresent(ExcelIndexAnno.class)) {
                int row = field.getAnnotation(ExcelIndexAnno.class).rowIndex();
                int cell = field.getAnnotation(ExcelIndexAnno.class).cellIndex();
                if (excelIndexMap.containsKey(row + "," + cell)) {
                    excelHeadMap.put(field.getName(), excelIndexMap.get(row + "," + cell));
                }
            }
        }
        return excelHeadMap;
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        DataFormatter dataFormatter = new DataFormatter();

        switch (cell.getCellType()) {
            case STRING:
                return dataFormatter.formatCellValue(cell);
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sdf.format(date);
                } else {
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return evaluateFormulaCell(cell);
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    // 标准版司库明细模板，根据各个银行明细转换
//    public static <T extends Transaction> String convertCashierPushTemplate(ConvertData convertData, List<T> fillList){
//        final String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmSS"));
//        ClassLoader classLoader = ExcelUtil.class.getClassLoader();
//        String resourcePath = CASHIRE_PUSH_TEMPLATE_LIB + CASHIRE_PUSH_TEMPLATE_FILE_NAME;
//        hasFolderAndCreate(convertData.getConvertCashire().getStoragePath());
//
//        String filePath = null;
//
//        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
//             Workbook workbook = new XSSFWorkbook(inputStream);
//             OutputStream outputStream = new FileOutputStream(convertData.getConvertCashire().getStoragePath() +
//                     (StringUtils.isEmpty(convertData.getTradeBankName())? (format  + CASHIRE_PUSH_TEMPLATE_FILE_NAME) : format + convertData.getTradeBankName() + ".xlsx") )) {
//             filePath = StringUtils.isEmpty(convertData.getTradeBankName())? (format  + CASHIRE_PUSH_TEMPLATE_FILE_NAME) : format + convertData.getTradeBankName() + ".xlsx" ;
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for (int i = 0; i < fillList.size(); i++) {
//                StandardTransaction transaction = (StandardTransaction) fillList.get(i);
//                Row row = sheet.createRow(i + 1);
//
//                row.createCell(0).setCellValue(i + 1);
//                row.createCell(1).setCellValue(replaceString(transaction.getTradeAccountNum()));
//                row.createCell(2).setCellValue(replaceString(transaction.getTransactionType().equals("D")?"借":"贷"));
//                row.createCell(3).setCellValue(replaceString(transaction.getTransactionType().equals("D")?transaction.getTransactionAmount():""));
//                row.createCell(4).setCellValue(replaceString(transaction.getTransactionType().equals("D")?"":transaction.getTransactionAmount()));
//                row.createCell(5).setCellValue(replaceString(transaction.getCounterpartyAccountNum()));
//                row.createCell(6).setCellValue(replaceString(transaction.getCounterpartyAccountName()));
//                row.createCell(7).setCellValue(replaceString(transaction.getCounterpartyBankName()));
//                row.createCell(8).setCellValue("人民币");
//                row.createCell(9).setCellValue(replaceString(transaction.getBalance()));
//                row.createCell(10).setCellValue("");
//                row.createCell(11).setCellValue("");
//                final List<String> dateAndTime = getDateAndTime(transaction.getTransactionTime());
//                row.createCell(12).setCellValue(replaceString(dateAndTime.get(0)));
//                row.createCell(13).setCellValue(replaceString(dateAndTime.get(1)));
//                row.createCell(14).setCellValue("");
//                row.createCell(15).setCellValue("");
//                row.createCell(16).setCellValue(replaceString(transaction.getRemark()));
//                row.createCell(17).setCellValue("");
//                row.createCell(18).setCellValue("");//银行流水号
//                row.createCell(19).setCellValue("");//外部流水号
//                row.createCell(20).setCellValue(replaceString(transaction.getSerialNumber()));//交易流水号
//            }
//
//            workbook.write(outputStream);
//        } catch (Exception e){
//            log.error(e.getMessage());
//        }
//        return filePath;
//    }

    private static String replaceString(String str){
        if (Objects.isNull(str)){
            return "";
        }
        if (str.equals("-")){
            return "";
        }
        return str;
    }

    private static List<String> getDateAndTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final LinkedList<String> dateList = new LinkedList<>();
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            dateList.add(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            dateList.add(dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }catch (Exception e){
            log.error("司库明细推送日期转换异常" + e.getMessage());
            dateList.clear();
            dateList.add("-");
            dateList.add("-");
            return dateList;
        }
        return dateList;
    }

    private static void hasFolderAndCreate(String directoryPath){
        try {
            Path path = Paths.get(directoryPath);
            Files.createDirectories(path);
            log.info("文件夹创建成功：" + path);
        } catch (IOException e) {
            log.error("文件夹创建失败：" + e.getMessage());
            throw new RuntimeException("文件夹创建失败：" + e.getMessage());
        }
    }

    private static Object evaluateFormulaCell(Cell cell) {
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);
        switch (cellValue.getCellType()) {
            case BOOLEAN:
                return cellValue.getBooleanValue();
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sdf.format(date);
                } else {
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
            case STRING:
                return cellValue.getStringValue();
            default:
                return null;
        }
    }


    private static void copyCell(Cell oldCell, Cell newCell) {
        if (oldCell != null) {
            // Copy cell value
            switch (oldCell.getCellType()) {
                case STRING:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                default:
                    break;
            }

            // Copy cell style
            CellStyle oldCellStyle = oldCell.getCellStyle();
            Workbook workbook = newCell.getSheet().getWorkbook();
            CellStyle newCellStyle = workbook.createCellStyle();
            DataFormat dataFormat = null;
            if (oldCellStyle instanceof XSSFCellStyle && newCellStyle instanceof HSSFCellStyle) {
                ((HSSFCellStyle) newCellStyle).cloneStyleFrom((XSSFCellStyle) oldCellStyle);
                dataFormat  = (HSSFDataFormat) workbook.createDataFormat();
                newCellStyle.setDataFormat(dataFormat.getFormat("@"));
            } else if (oldCellStyle instanceof HSSFCellStyle && newCellStyle instanceof XSSFCellStyle) {
                ((XSSFCellStyle) newCellStyle).cloneStyleFrom((HSSFCellStyle) oldCellStyle);
                dataFormat = (XSSFDataFormat) workbook.createDataFormat();
                newCellStyle.setDataFormat(dataFormat.getFormat("@"));
            }

            newCell.setCellStyle(newCellStyle);
        }
    }

    private static void copyRow(Row oldRow, Row newRow) {
        if (oldRow != null && newRow != null) {
            for (int k = 0; k < oldRow.getPhysicalNumberOfCells(); k++) {
                Cell oldCell = oldRow.getCell(k);
                Cell newCell = newRow.createCell(k);
                copyCell(oldCell, newCell);
            }
        }
    }

    private static void copySheet(Sheet oldSheet, Sheet newSheet) {
        if (oldSheet != null && newSheet != null) {
            for (int j = 0; j < oldSheet.getPhysicalNumberOfRows(); j++) {
                Row oldRow = oldSheet.getRow(j);
                Row newRow = newSheet.createRow(j);
                copyRow(oldRow, newRow);
            }
        }
    }

    private static void convertWorkbook(Workbook oldWorkbook, Workbook newWorkbook) {
        for (int i = 0; i < oldWorkbook.getNumberOfSheets(); i++) {
            Sheet oldSheet = oldWorkbook.getSheetAt(i);
            Sheet newSheet = newWorkbook.createSheet(oldSheet.getSheetName());
            copySheet(oldSheet, newSheet);
        }
    }

//    public static JsonResult convert(String path) {
//        Path filePath = Paths.get(path);
//        final String fileName = filePath.getFileName().toString();
//        if (Objects.isNull(fileName)){
//            return JsonResult.error("500","文件名称为空");
//        }
//        String outFileName = null;
//        int dotIndex = fileName.lastIndexOf('.');
//        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
//            return JsonResult.error("500","文件格式不正确");
//        }
//        boolean xlsToXlsx;
//        if (fileName.substring(dotIndex + 1).equals("xls")){
//            xlsToXlsx = true;
//            outFileName = path.replaceFirst(".xls",".xlsx");
//        } else if (fileName.substring(dotIndex + 1).equals("xlsx")) {
//            xlsToXlsx = false;
//            outFileName = path.replaceFirst(".xlsx",".xls");
//        }else {
//            return JsonResult.error("500","文件类型参数不匹配");
//        }
//        try (Workbook oldWorkbook = xlsToXlsx ? new HSSFWorkbook(new FileInputStream(path))
//                : new XSSFWorkbook(new FileInputStream(path));
//             FileOutputStream out = new FileOutputStream(outFileName)) {
//
//            Workbook newWorkbook = xlsToXlsx ? new XSSFWorkbook() : new HSSFWorkbook();
//            convertWorkbook(oldWorkbook, newWorkbook);
//            newWorkbook.write(out);
//        }catch (Exception e){
//            return JsonResult.error("500","转换文件格式失败" + e);
//        }
//        try {
//            Files.deleteIfExists(Paths.get(path));
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.error("源文件删除失败");
//        }
//        return JsonResult.success();
//    }

}
