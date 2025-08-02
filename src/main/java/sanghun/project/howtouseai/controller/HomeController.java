package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        log.info("홈페이지 접속");
        return "AI 도구 가이드 웹사이트에 오신 것을 환영합니다!";
    }
} 