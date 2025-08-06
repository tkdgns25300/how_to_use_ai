package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        log.info("Main page accessed");
        
        try {
            // 카드 데이터 로드 (첫 20개 카드)
            Pageable pageable = PageRequest.of(0, 20);
            var cardsResponse = cardService.getAllCards(pageable);
            model.addAttribute("cards", cardsResponse.getContent());
            
            log.info("Loaded {} cards", cardsResponse.getContent().size());
            
        } catch (Exception e) {
            log.error("Error loading card data: {}", e.getMessage(), e);
            // 오류 발생 시 빈 리스트로 초기화
            model.addAttribute("cards", java.util.Collections.emptyList());
        }
        
        return "index";
    }

    @GetMapping("/card/new")
    public String newCardForm(Model model) {
        log.info("Card creation page accessed");
        
        try {
            // 카테고리 목록 로드
            var categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            
            log.info("Loaded {} categories", categories.size());
            
        } catch (Exception e) {
            log.error("Error loading category data: {}", e.getMessage(), e);
            // 오류 발생 시 빈 리스트로 초기화
            model.addAttribute("categories", java.util.Collections.emptyList());
        }
        
        return "card-form";
    }

    @GetMapping("/card/{cardId}")
    public String cardDetail(@PathVariable Long cardId, Model model) {
        log.info("Card detail page accessed: cardId={}", cardId);
        
        try {
            // 카드 상세 정보 로드
            var cardResponse = cardService.getCardById(cardId);
            model.addAttribute("card", cardResponse);
            
            log.info("Card detail loaded: id={}, title={}", cardResponse.getId(), cardResponse.getTitle());
            
        } catch (Exception e) {
            log.error("Error loading card detail: {}", e.getMessage(), e);
            // 오류 발생 시 에러 페이지로 리다이렉트
            return "redirect:/?error=card_not_found";
        }
        
        return "card-detail";
    }

    @GetMapping("/card/{cardId}/edit")
    public String cardEdit(@PathVariable Long cardId, Model model) {
        log.info("Card edit page accessed: cardId={}", cardId);
        
        try {
            // 카드 상세 정보 로드
            var cardResponse = cardService.getCardById(cardId);
            model.addAttribute("card", cardResponse);
            
            // 카테고리 목록 로드
            var categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            
            log.info("Card edit form loaded: id={}, title={}", cardResponse.getId(), cardResponse.getTitle());
            
        } catch (Exception e) {
            log.error("Error loading card edit form: {}", e.getMessage(), e);
            // 오류 발생 시 에러 페이지로 리다이렉트
            return "redirect:/?error=card_not_found";
        }
        
        return "card-edit";
    }
} 