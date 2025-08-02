package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.CategoryCreateRequest;
import sanghun.project.howtouseai.dto.CategoryResponse;
import sanghun.project.howtouseai.dto.CategoryUpdateRequest;
import sanghun.project.howtouseai.dto.ResponseHelper;
import sanghun.project.howtouseai.service.CategoryService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestParam("name") String name,
            @RequestParam("iconFile") MultipartFile iconFile) throws IOException {
        
        log.info("카테고리 생성 API 호출: name={}, filename={}", name, iconFile.getOriginalFilename());
        
        try {
            // DTO 생성
            CategoryCreateRequest request = CategoryCreateRequest.builder()
                    .name(name)
                    .iconFile(iconFile)
                    .build();
            
            CategoryResponse categoryResponse = categoryService.createCategory(request);
            
            ApiResponse<CategoryResponse> response = ResponseHelper.success(
                categoryResponse,
                "카테고리가 성공적으로 생성되었습니다."
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("카테고리 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile) throws IOException {
        
        log.info("카테고리 수정 API 호출: categoryId={}, name={}, hasIconFile={}", 
                categoryId, name, iconFile != null);
        
        try {
            // DTO 생성
            CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                    .name(name)
                    .iconFile(iconFile)
                    .build();
            
            CategoryResponse categoryResponse = categoryService.updateCategory(categoryId, request);
            
            ApiResponse<CategoryResponse> response = ResponseHelper.success(
                categoryResponse,
                "카테고리가 성공적으로 수정되었습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("카테고리 수정 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }
} 