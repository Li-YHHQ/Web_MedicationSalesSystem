package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.domain.FileRecord;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.FileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@Tag(name = "文件")
public class FileController {

    private final FileMapper fileMapper;

    public FileController(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文件内容（用于图片显示）")
    public ResponseEntity<byte[]> get(
            @Parameter(description = "文件ID", required = true) @PathVariable Long id
    ) {
        FileRecord fr = fileMapper.selectDataById(id);
        if (fr == null || fr.getData() == null) {
            throw new BizException(404, "文件不存在");
        }

        String ct = fr.getContentType();
        MediaType mt;
        try {
            mt = ct != null ? MediaType.parseMediaType(ct) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            mt = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mt)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .body(fr.getData());
    }
}
