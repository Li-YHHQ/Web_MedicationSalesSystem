package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.auth.AdminGuard;
import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.DeleteFileRequest;
import com.neusoft.coursemgr.domain.FileRecord;
import com.neusoft.coursemgr.domain.UploadFileResponse;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.FileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin/files")
@Tag(name = "管理员-文件管理")
public class AdminFileController {

    private final FileMapper fileMapper;

    public AdminFileController(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "管理员上传图片（药品封面/轮播图）")
    public ApiResponse<UploadFileResponse> uploadImage(
            @Parameter(description = "图片文件，form-data字段名=file", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        Long adminId = AdminGuard.requireAdminUserId();
        if (file == null || file.isEmpty()) {
            throw new BizException(400, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BizException(400, "仅支持图片文件");
        }

        String original = file.getOriginalFilename();

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new BizException(500, "读取文件失败");
        }

        FileRecord fr = new FileRecord();
        fr.setContentType(contentType);
        fr.setOriginalFilename(original);
        fr.setSize(file.getSize());
        fr.setData(bytes);
        fr.setUploaderAdminId(adminId);
        fileMapper.insert(fr);

        String url = "/api/files/" + fr.getId();

        UploadFileResponse resp = new UploadFileResponse();
        resp.setUrl(url);
        resp.setOriginalFilename(original);
        resp.setSize(file.getSize());
        resp.setContentType(contentType);
        resp.setUploaderAdminId(adminId);
        return ApiResponse.ok("uploaded", resp);
    }

    @DeleteMapping
    @Operation(summary = "管理员删除已上传文件（根据url）")
    public ApiResponse<String> delete(@Valid @RequestBody DeleteFileRequest req) {
        AdminGuard.requireAdminUserId();
        String url = req.getUrl().trim();

        Long id = parseFileId(url);
        if (id == null) {
            throw new BizException(400, "url格式不正确，期望 /api/files/{id}");
        }

        fileMapper.deleteById(id);

        return ApiResponse.ok("deleted", "success");
    }

    private static Long parseFileId(String url) {
        if (url == null) {
            return null;
        }
        String prefix = "/api/files/";
        if (!url.startsWith(prefix)) {
            return null;
        }
        String rest = url.substring(prefix.length());
        if (rest.isBlank()) {
            return null;
        }
        int q = rest.indexOf('?');
        if (q >= 0) {
            rest = rest.substring(0, q);
        }
        try {
            return Long.parseLong(rest);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
