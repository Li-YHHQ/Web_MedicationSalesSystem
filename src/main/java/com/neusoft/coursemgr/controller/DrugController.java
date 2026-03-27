package com.neusoft.coursemgr.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
     * 自动识别格式：读取第一行表头后：
     * <ul>
     *   <li>包含"有效期"列 → 格式二（效期批次表），调用 importBatches</li>
     *   <li>否则             → 格式一（药品档案表），调用 importDrugs</li>
     * </ul>
     * 两种格式均通过列名匹配，与列顺序无关。
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "从 Excel 导入药品（支持药品档案表/效期批次表两种格式）")
    public ApiResponse<String> importExcel(
            @RequestPart("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BizException(400, "文件不能为空");
        }

        FlexibleImportListener listener = new FlexibleImportListener();
        // .xls 文件内部字符串按工作簿 Codepage（GBK/936）存储；
        // 显式指定 GBK 让 EasyExcel 透传给 POI 的字符串解码器，
        // 避免用 ISO-8859-1 解码 GBK 字节导致中文列名乱码。
        // 对 .xlsx 文件无影响（XML 自描述编码，不使用此参数）。
        EasyExcel.read(file.getInputStream())
                .charset(Charset.forName("GBK"))
                .registerReadListener(listener)
                .sheet()
                .doRead();

        if (listener.getNameToIdx().isEmpty()) {
            throw new BizException(400, "Excel 表头为空，无法识别格式");
        }

        if (listener.isBatchFormat()) {
            drugService.importBatches(listener.getNameToIdx(), listener.getRows());
        } else {
            drugService.importDrugs(listener.getNameToIdx(), listener.getRows());
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
    // 按列名动态解析的 EasyExcel Listener
    // -------------------------------------------------------------------------

    /**
     * 读取任意列顺序的 Excel，在 invokeHead 回调中建立"列名 → 列索引"映射，
     * 在 invoke 回调中以 Map&lt;Integer, String&gt; 存储每行原始数据。
     */
    private static class FlexibleImportListener extends AnalysisEventListener<Map<Integer, String>> {

        /** 列名 → 列索引 */
        private final Map<String, Integer> nameToIdx = new LinkedHashMap<>();
        /** 所有数据行（列索引 → 单元格字符串值） */
        private final List<Map<Integer, String>> rows = new ArrayList<>();

        @Override
        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
            headMap.forEach((idx, cell) -> {
                String name = cell.getStringValue();
                if (name != null && !name.isBlank()) {
                    nameToIdx.put(name.trim(), idx);
                }
            });
        }

        @Override
        public void invoke(Map<Integer, String> data, AnalysisContext context) {
            rows.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 全部解析完毕，数据已在 rows 中
        }

        /** 是否为效期批次表格式（含"有效期"列） */
        public boolean isBatchFormat() {
            return nameToIdx.containsKey("有效期");
        }

        public Map<String, Integer> getNameToIdx() { return nameToIdx; }

        public List<Map<Integer, String>> getRows() { return rows; }
    }
}
