package com.neusoft.coursemgr.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "从 Excel 导入药品")
    public ApiResponse<String> importExcel(
            @RequestPart("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BizException(400, "文件不能为空");
        }

        DrugImportListener listener = new DrugImportListener();
        EasyExcel.read(file.getInputStream(), DrugExcelVO.class, listener)
                .sheet()
                .doRead();

        List<Drug> drugs = listener.getList().stream()
                .map(DrugController::fromExcelVO)
                .collect(Collectors.toList());

        drugService.importDrugs(drugs);
        return ApiResponse.ok("imported", "success");
    }

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

    private static Drug fromExcelVO(DrugExcelVO vo) {
        Drug drug = new Drug();
        drug.setDrugCode(vo.getDrugCode());
        drug.setDrugName(vo.getDrugName());
        drug.setCommonName(vo.getCommonName());
        drug.setCategory(vo.getCategory());
        drug.setUnit(vo.getUnit());
        drug.setSpec(vo.getSpec());
        drug.setManufacturer(vo.getManufacturer());
        drug.setApprovalNo(vo.getApprovalNo());
        drug.setBarcode(vo.getBarcode());
        drug.setCostPrice(vo.getCostPrice());
        drug.setRetailPrice(vo.getRetailPrice());
        drug.setStockMin(vo.getStockMin());
        return drug;
    }

    private static class DrugImportListener implements ReadListener<DrugExcelVO> {
        private final List<DrugExcelVO> list = new ArrayList<>();

        @Override
        public void invoke(DrugExcelVO data, AnalysisContext context) {
            list.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 所有行解析完成，数据在 list 中
        }

        public List<DrugExcelVO> getList() {
            return list;
        }
    }
}
