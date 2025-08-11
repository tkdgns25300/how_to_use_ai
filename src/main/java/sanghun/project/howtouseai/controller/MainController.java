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

import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final CardService cardService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        log.info("Main page accessed");
        
        String userUuid = getOrCreateUserUuid(session);

        try {
            Pageable pageable = PageRequest.of(0, 20);
            var cards = cardService.getCardsForHomePage(pageable, userUuid);
            model.addAttribute("cards", cards);
            
            log.info("Loaded {} cards", cards.size());
            
        } catch (Exception e) {
            log.error("Error loading card data: {}", e.getMessage(), e);
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
    public String cardDetail(@PathVariable Long cardId, Model model, HttpSession session) {
        log.info("Card detail page accessed: cardId={}", cardId);
        
        String userUuid = getOrCreateUserUuid(session);

        try {
            var cardResponse = cardService.getCardById(cardId, userUuid);
            model.addAttribute("card", cardResponse);
            
            log.info("Card detail loaded: id={}, title={}", cardResponse.getId(), cardResponse.getTitle());
            
        } catch (Exception e) {
            log.error("Error loading card detail: {}", e.getMessage(), e);
            return "redirect:/?error=card_not_found";
        }
        
        return "card-detail";
    }

    @GetMapping("/card/{cardId}/edit")
    public String cardEdit(@PathVariable Long cardId, Model model, HttpSession session) {
        log.info("Card edit page accessed: cardId={}", cardId);
        
        String userUuid = getOrCreateUserUuid(session);

        try {
            // 카드 상세 정보 로드
            var cardResponse = cardService.getCardById(cardId, userUuid);
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
    
    private String getOrCreateUserUuid(HttpSession session) {
        String uuid = (String) session.getAttribute("uuid");
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
            session.setAttribute("uuid", uuid);
            log.info("New user UUID created: {}", uuid);
        }
        return uuid;
    }
} 