package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sanghun.project.howtouseai.service.CardService;
import sanghun.project.howtouseai.service.CategoryService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final CardService cardService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String index(Model model) {
        log.info("메인 페이지 접속");
        
        try {
            // 카드 데이터 로드 (첫 20개 카드)
            Pageable pageable = PageRequest.of(0, 20);
            var cardsResponse = cardService.getAllCards(pageable);
            model.addAttribute("cards", cardsResponse.getContent());
            
            log.info("카드 {}개 로드 완료", cardsResponse.getContent().size());
            
        } catch (Exception e) {
            log.error("카드 데이터 로드 중 오류 발생: {}", e.getMessage(), e);
            // 오류 발생 시 빈 리스트로 초기화
            model.addAttribute("cards", java.util.Collections.emptyList());
        }
        
        return "index";
    }

    @GetMapping("/card/new")
    public String newCardForm(Model model) {
        log.info("카드 등록 페이지 접속");
        
        try {
            // 카테고리 목록 로드
            var categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            
            log.info("카테고리 {}개 로드 완료", categories.size());
            
        } catch (Exception e) {
            log.error("카테고리 데이터 로드 중 오류 발생: {}", e.getMessage(), e);
            // 오류 발생 시 빈 리스트로 초기화
            model.addAttribute("categories", java.util.Collections.emptyList());
        }
        
        return "card-form";
    }
} 