package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sanghun.project.howtouseai.dto.ApiResponse;
import sanghun.project.howtouseai.dto.CategoryResponse;
import sanghun.project.howtouseai.dto.ResponseHelper;
import sanghun.project.howtouseai.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("카테고리 목록 조회 API 호출");
        
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            
            ApiResponse<List<CategoryResponse>> response = ResponseHelper.success(
                categories,
                "카테고리 목록을 성공적으로 조회했습니다."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("카테고리 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }
} 