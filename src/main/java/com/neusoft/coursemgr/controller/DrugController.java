package com.neusoft.coursemgr.controller;

import com.alibaba.excel.EasyExcel;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateDrugRequest;
import com.neusoft.coursemgr.domain.Drug;
import com.neusoft.coursemgr.domain.DrugExcelVO;
import com.neusoft.coursemgr.domain.UpdateDrugRequest;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.service.DrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drugs")
@Tag(name = "药品管理")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping
    @Operation(summary = "分页查询药品列表")
    public ApiResponse<PageResult<Drug>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;
        return ApiResponse.ok(drugService.list(keyword, category, status, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询单个药品")
    public ApiResponse<Drug> getById(@PathVariable Integer id) {
        return ApiResponse.ok(drugService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增药品")
    public ApiResponse<String> create(@Valid @RequestBody CreateDrugRequest req) {
        drugService.create(req);
        return ApiResponse.ok("created", "success");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新药品")
    public ApiResponse<String> update(@PathVariable Integer id,
                                      @Valid @RequestBody UpdateDrugRequest req) {
        drugService.update(id, req);
        return ApiResponse.ok("updated", "success");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除药品（软删除）")
    public ApiResponse<String> delete(@PathVariable Integer id) {
        drugService.delete(id);
        return ApiResponse.ok("deleted", "success");
    }

    @GetMapping("/export")
    @Operation(summary = "导出药品列表为 Excel")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) Integer status) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("药品列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName + ".xlsx");

            List<Drug> drugs = drugService.listAll(keyword, category, status);
            List<DrugExcelVO> voList = drugs.stream()
                    .map(DrugController::toDrugExcelVO)
                    .collect(Collectors.toList());

            EasyExcel.write(response.getOutputStream(), DrugExcelVO.class)
                    .sheet("药品列表")
                    .doWrite(voList);
        } catch (IOException e) {
            throw new BizException(500, "导出失败: " + e.getMessage());
        }
    }

    /**
     * 从 Excel 导入药品或效期批次。
     * <p>
     * 使用 Apache POI 直接读取：
     * <ul>
     *   <li>.xls → HSSFWorkbook（POI 从文件 Codepage 记录自动识别 GBK 编码）</li>
     *   <li>.xlsx → XSSFWorkbook（XML 内部 UTF-8，无编码问题）</li>
     * </ul>
     * 自动识别格式：表头含"有效期"列 → 效期批次表，否则 → 药品档案表。
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "从 Excel 导入药品（支持药品档案表/效期批次表两种格式）")
    public ApiResponse<String> importExcel(
            @RequestPart("file") MultipartFile file) throws IOException {
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
        // .xls 后缀但不是 .xlsx 才走 HSSF（老格式）
        boolean isXls = lower.endsWith(".xls") && !lower.endsWith(".xlsx");

        try (Workbook workbook = isXls
                ? new HSSFWorkbook(file.getInputStream())
                : new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            // 读取第一行表头，建立列索引 → 列名 的映射
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BizException(400, "表头行为空，无法识别格式");
            }
            DataFormatter formatter = new DataFormatter();
            Map<Integer, String> idxToName = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String name = formatter.formatCellValue(cell).trim();
                if (!name.isEmpty()) {
                    idxToName.put(cell.getColumnIndex(), name);
                }
            }
            if (idxToName.isEmpty()) {
                throw new BizException(400, "表头为空，无法识别格式");
            }

            // 遍历数据行，构建 List<Map<列名, 值>>
            List<Map<String, String>> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (Map.Entry<Integer, String> entry : idxToName.entrySet()) {
                    Cell cell = row.getCell(entry.getKey(),
                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    rowMap.put(entry.getValue(), cellValue(cell, formatter));
                }
                rows.add(rowMap);
            }

            // 表头含"有效期"列 → 格式二（效期批次表）
            boolean isBatchFormat = idxToName.containsValue("有效期");
            if (isBatchFormat) {
                drugService.importBatches(rows);
            } else {
                drugService.importDrugs(rows);
            }
        }
        return ApiResponse.ok("imported", "success");
    }

    // -------------------------------------------------------------------------
    // 导出辅助
    // -------------------------------------------------------------------------

    private static DrugExcelVO toDrugExcelVO(Drug drug) {
        DrugExcelVO vo = new DrugExcelVO();
        vo.setDrugCode(drug.getDrugCode());
        vo.setDrugName(drug.getDrugName());
        vo.setCommonName(drug.getCommonName());
        vo.setCategory(drug.getCategory());
        vo.setUnit(drug.getUnit());
        vo.setSpec(drug.getSpec());
        vo.setManufacturer(drug.getManufacturer());
        vo.setApprovalNo(drug.getApprovalNo());
        vo.setBarcode(drug.getBarcode());
        vo.setCostPrice(drug.getCostPrice());
        vo.setRetailPrice(drug.getRetailPrice());
        vo.setStockMin(drug.getStockMin());
        return vo;
    }

    // -------------------------------------------------------------------------
    // POI 单元格读取
    // -------------------------------------------------------------------------

    /**
     * 将 POI Cell 转为字符串：
     * <ul>
     *   <li>字符串单元格：直接返回原始文本</li>
     *   <li>数字日期单元格：转为 ISO 日期字符串（yyyy-MM-dd），
     *       确保 Service 层的 parseDate() 能正确解析</li>
     *   <li>普通数字单元格：整数去掉 .0，小数保留</li>
     *   <li>公式单元格：用 DataFormatter 取计算结果文本</li>
     *   <li>空单元格：返回 null</li>
     * </ul>
     */
    private static String cellValue(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: {
                String s = cell.getStringCellValue();
                return s.isBlank() ? null : s;
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期格式单元格直接取 LocalDate，统一输出 ISO 格式
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double d = cell.getNumericCellValue();
                // 整数去掉 .0（如库存 10.0 → "10"）
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
}
