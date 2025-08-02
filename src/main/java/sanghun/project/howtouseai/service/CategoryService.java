package sanghun.project.howtouseai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sanghun.project.howtouseai.domain.Category;
import sanghun.project.howtouseai.dto.CategoryCreateRequest;
import sanghun.project.howtouseai.dto.CategoryResponse;
import sanghun.project.howtouseai.exception.CategoryAlreadyExistsException;
import sanghun.project.howtouseai.repository.CategoryRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    public List<CategoryResponse> getAllCategories() {
        log.info("모든 카테고리 조회 요청");
        
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        log.info("카테고리 조회 완료: count={}", categories.size());
        
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) throws IOException {
        log.info("카테고리 생성 요청: name={}", request.getName());

        // 중복 카테고리명 체크
        if (categoryRepository.existsByName(request.getName())) {
            log.warn("중복된 카테고리명으로 생성 시도: {}", request.getName());
            throw new CategoryAlreadyExistsException(
                String.format("이미 존재하는 카테고리명입니다: %s", request.getName())
            );
        }

        // 이미지 파일 업로드
        String iconUrl = fileService.uploadCategoryIcon(request.getIconFile());
        log.info("이미지 업로드 완료: iconUrl={}", iconUrl);

        // 카테고리 엔티티 생성
        Category category = Category.builder()
                .name(request.getName())
                .iconUrl(iconUrl)
                .build();

        // 저장
        Category savedCategory = categoryRepository.save(category);
        log.info("카테고리 생성 완료: id={}, name={}", savedCategory.getId(), savedCategory.getName());

        // 응답 DTO로 변환
        return convertToResponse(savedCategory);
    }

    private CategoryResponse convertToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .iconUrl(category.getIconUrl())
                .createdAt(category.getCreatedAt())
                .build();
    }
} 