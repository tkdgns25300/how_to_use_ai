package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String index(Model model) {
        log.info("메인 페이지 접속");
        
        // TODO: 카드 데이터 로드
        // TODO: 카테고리 데이터 로드
        
        return "index";
    }
} 