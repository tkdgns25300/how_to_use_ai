package sanghun.project.howtouseai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sanghun.project.howtouseai.domain.Category;
import sanghun.project.howtouseai.dto.CategoryCreateRequest;
import sanghun.project.howtouseai.dto.CategoryResponse;
import sanghun.project.howtouseai.dto.CategoryUpdateRequest;
import sanghun.project.howtouseai.exception.CategoryAlreadyExistsException;
import sanghun.project.howtouseai.exception.CategoryNotFoundException;
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

    /**
     * 모든 카테고리를 이름 오름차순으로 조회합니다.
     *
     * @return 카테고리 응답 DTO 리스트
     */
    public List<CategoryResponse> getAllCategories() {
        log.info("모든 카테고리 조회 요청");
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        log.info("카테고리 조회 완료: count={}", categories.size());
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        log.info("카테고리 조회 요청: categoryId={}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카테고리 조회 시도: categoryId={}", categoryId);
                    return new CategoryNotFoundException(
                        String.format("카테고리를 찾을 수 없습니다: ID %d", categoryId)
                    );
                });
        
        log.info("카테고리 조회 완료: categoryId={}, name={}", categoryId, category.getName());
        return convertToResponse(category);
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

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) throws IOException {
        log.info("카테고리 수정 요청: categoryId={}, name={}, hasIconFile={}", 
                categoryId, request.getName(), request.getIconFile() != null);

        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카테고리 수정 시도: categoryId={}", categoryId);
                    return new CategoryNotFoundException(
                        String.format("카테고리를 찾을 수 없습니다: ID %d", categoryId)
                    );
                });

        String newName = request.getName();
        String newIconUrl = null;

        // 이름 업데이트 처리
        if (newName != null && !newName.trim().isEmpty()) {
            // 중복 카테고리명 체크 (자신 제외)
            if (!category.getName().equals(newName) && 
                categoryRepository.existsByName(newName)) {
                log.warn("중복된 카테고리명으로 수정 시도: {}", newName);
                throw new CategoryAlreadyExistsException(
                    String.format("이미 존재하는 카테고리명입니다: %s", newName)
                );
            }
        } else {
            newName = category.getName(); // 기존 이름 유지
        }

        // 이미지 파일 업데이트 처리
        if (request.getIconFile() != null && !request.getIconFile().isEmpty()) {
            newIconUrl = fileService.uploadCategoryIcon(request.getIconFile());
            log.info("이미지 업로드 완료: iconUrl={}", newIconUrl);
        } else {
            newIconUrl = category.getIconUrl(); // 기존 아이콘 URL 유지
        }

        // 카테고리 정보 업데이트
        category.updateInfo(newName, newIconUrl);
        
        // 저장
        Category updatedCategory = categoryRepository.save(category);
        log.info("카테고리 수정 완료: id={}, name={}", updatedCategory.getId(), updatedCategory.getName());

        // 응답 DTO로 변환
        return convertToResponse(updatedCategory);
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