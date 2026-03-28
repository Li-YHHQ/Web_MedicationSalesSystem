package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.StockSyncPreview;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.service.StockSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stock/sync")
@Tag(name = "库存同步")
public class StockSyncController {

    private final StockSyncService stockSyncService;

    public StockSyncController(StockSyncService stockSyncService) {
        this.stockSyncService = stockSyncService;
    }

    /**
     * 预览库存同步结果，不写入数据库。
     * 接收 Excel 文件（.xls/.xlsx），读取"药品编码"和"药品库存"两列，
     * 返回每条药品的变化类型及差值。
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "预览库存同步（不写库）")
    public ApiResponse<StockSyncPreview> preview(
            @RequestPart("file") MultipartFile file,
            @RequestParam("syncDate") String syncDate) throws IOException {

        Map<String, Integer> drugStockMap = parseExcel(file);
        return ApiResponse.ok(stockSyncService.preview(drugStockMap, syncDate));
    }

    /**
     * 执行库存同步，写入数据库。
     * 流程与 preview 一致，区别在于调用 confirm 进行实际写入。
     */
    @PostMapping(value = "/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "执行库存同步（写库）")
    public ApiResponse<String> confirm(
            @RequestPart("file") MultipartFile file,
            @RequestParam("syncDate") String syncDate) throws IOException {

        Map<String, Integer> drugStockMap = parseExcel(file);
        stockSyncService.confirm(drugStockMap, syncDate);
        return ApiResponse.ok("synced", "success");
    }

    // -------------------------------------------------------------------------
    // Excel 解析（使用 Apache POI 直接读取，正确处理 .xls GBK 编码）
    // -------------------------------------------------------------------------

    /**
     * 读取 Excel 第一个 Sheet，提取"药品编码"→"药品库存"的映射。
     * <ul>
     *   <li>.xls → HSSFWorkbook（POI 从 Codepage 记录自动识别编码）</li>
     *   <li>.xlsx → XSSFWorkbook</li>
     *   <li>药品编码若为纯数字，补零到 6 位（防止 Excel 丢失前导零）</li>
     * </ul>
     */
    private static Map<String, Integer> parseExcel(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BizException(400, "文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BizException(400, "无法识别文件格式");
        }
        String lower = filename.toLowerCase();
        if (!lower.endsWith(".xls") && !lower.endsWith(".xlsx")) {
            throw new BizException(400, "仅支持 .xls 或 .xlsx 格式");
        }
        boolean isXls = lower.endsWith(".xls") && !lower.endsWith(".xlsx");

        try (Workbook workbook = isXls
                ? new HSSFWorkbook(file.getInputStream())
                : new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BizException(400, "表头行为空");
            }

            // 建立列索引 → 列名映射
            DataFormatter formatter = new DataFormatter();
            Map<Integer, String> idxToName = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String name = formatter.formatCellValue(cell).trim();
                if (!name.isEmpty()) {
                    idxToName.put(cell.getColumnIndex(), name);
                }
            }

            // 找到"药品编码"和"药品库存"所在列索引
            Integer codeIdx = null, qtyIdx = null;
            for (Map.Entry<Integer, String> e : idxToName.entrySet()) {
                if ("药品编码".equals(e.getValue())) codeIdx = e.getKey();
                if ("药品库存".equals(e.getValue())) qtyIdx  = e.getKey();
            }
            if (codeIdx == null) throw new BizException(400, "Excel 缺少\"药品编码\"列");
            if (qtyIdx  == null) throw new BizException(400, "Excel 缺少\"药品库存\"列");

            // 遍历数据行
            Map<String, Integer> result = new LinkedHashMap<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell codeCell = row.getCell(codeIdx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Cell qtyCell  = row.getCell(qtyIdx,  Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                String drugCode = cellString(codeCell, formatter);
                String qtyStr   = cellString(qtyCell,  formatter);

                if (drugCode == null || drugCode.isBlank()) continue;
                if (qtyStr   == null || qtyStr.isBlank())   continue;

                // 纯数字编码补零到 6 位（防止 Excel 丢失前导零）
                drugCode = padDrugCode(drugCode.trim());

                int qty;
                try {
                    qty = (int) Double.parseDouble(qtyStr.trim());
                } catch (NumberFormatException ex) {
                    continue;   // 无法解析的数量行直接跳过
                }
                result.put(drugCode, qty);
            }
            return result;
        }
    }

    /**
     * 读取单元格值为字符串：
     * STRING → 原始文本；NUMERIC 日期 → ISO 日期；整数去 .0；FORMULA → DataFormatter；其余 null。
     */
    private static String cellString(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: {
                String s = cell.getStringCellValue();
                return s.isBlank() ? null : s;
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d) && !Double.isInfinite(d)) {
                    return String.valueOf((long) d);
                }
                return String.valueOf(d);
            }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return formatter.formatCellValue(cell);
            case BLANK:
            default:
                return null;
        }
    }

    /**
     * 若药品编码为纯数字（Excel 可能丢失前导零），补零到 6 位。
     * 非纯数字编码保持原样。
     */
    private static String padDrugCode(String code) {
        try {
            long num = Long.parseLong(code);
            return String.format("%06d", num);
        } catch (NumberFormatException e) {
            return code;
        }
    }
}
