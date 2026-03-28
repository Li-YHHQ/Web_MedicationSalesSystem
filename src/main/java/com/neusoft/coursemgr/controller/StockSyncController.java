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
     * 只需"药品编码"和"药品库存"两列做对比，其余列忽略。
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "预览库存同步（不写库）")
    public ApiResponse<StockSyncPreview> preview(
            @RequestPart("file") MultipartFile file,
            @RequestParam("syncDate") String syncDate) throws IOException {

        Map<String, Integer> drugStockMap = parseExcelSimple(file);
        return ApiResponse.ok(stockSyncService.preview(drugStockMap, syncDate));
    }

    /**
     * 执行库存同步，写入数据库。
     * 读取 Excel 所有字段，NEW 分支可写入完整药品档案。
     */
    @PostMapping(value = "/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "执行库存同步（写库）")
    public ApiResponse<String> confirm(
            @RequestPart("file") MultipartFile file,
            @RequestParam("syncDate") String syncDate) throws IOException {

        Map<String, Map<String, String>> fullDataMap = parseExcelFull(file);
        stockSyncService.confirm(fullDataMap, syncDate);
        return ApiResponse.ok("synced", "success");
    }

    // -------------------------------------------------------------------------
    // Excel 解析
    // -------------------------------------------------------------------------

    /**
     * preview 专用：只提取"药品编码"→"药品库存"两列，构建 Map&lt;String, Integer&gt;。
     * 要求 Excel 必须同时包含这两列。
     */
    private static Map<String, Integer> parseExcelSimple(MultipartFile file) throws IOException {
        Map<String, Map<String, String>> full = parseExcelFull(file);

        // 校验"药品库存"列存在（至少有一行包含该 key）
        boolean hasQtyCol = full.values().stream()
                .anyMatch(row -> row.containsKey("药品库存"));
        if (!hasQtyCol) {
            throw new BizException(400, "Excel 缺少\"药品库存\"列");
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, String>> e : full.entrySet()) {
            String qtyStr = e.getValue().getOrDefault("药品库存", "");
            if (qtyStr.isBlank()) continue;
            try {
                result.put(e.getKey(), (int) Double.parseDouble(qtyStr.trim()));
            } catch (NumberFormatException ignored) {
                // 无法解析的数量行跳过
            }
        }
        return result;
    }

    /**
     * confirm 专用：读取 Excel 所有列，构建 Map&lt;药品编码, Map&lt;列名, 值&gt;&gt;。
     * <ul>
     *   <li>.xls → HSSFWorkbook（POI 从 Codepage 记录自动识别 GBK 编码）</li>
     *   <li>.xlsx → XSSFWorkbook</li>
     *   <li>药品编码若为纯数字，补零到 6 位（防止 Excel 丢失前导零）</li>
     *   <li>单元格值缺失时存空字符串，调用方用 getOrDefault 取值即可</li>
     * </ul>
     */
    private static Map<String, Map<String, String>> parseExcelFull(MultipartFile file)
            throws IOException {
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

            // "药品编码"列必须存在
            Integer codeIdx = null;
            for (Map.Entry<Integer, String> e : idxToName.entrySet()) {
                if ("药品编码".equals(e.getValue())) {
                    codeIdx = e.getKey();
                    break;
                }
            }
            if (codeIdx == null) {
                throw new BizException(400, "Excel 缺少\"药品编码\"列");
            }

            // 遍历数据行，每行读取所有已知列
            Map<String, Map<String, String>> result = new LinkedHashMap<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell codeCell = row.getCell(codeIdx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                String drugCode = cellString(codeCell, formatter);
                if (drugCode == null || drugCode.isBlank()) continue;

                // 纯数字编码补零到 6 位（防止 Excel 丢失前导零）
                drugCode = padDrugCode(drugCode.trim());

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (Map.Entry<Integer, String> e : idxToName.entrySet()) {
                    Cell cell = row.getCell(e.getKey(),
                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String val = cellString(cell, formatter);
                    rowMap.put(e.getValue(), val != null ? val : "");
                }
                result.put(drugCode, rowMap);
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
