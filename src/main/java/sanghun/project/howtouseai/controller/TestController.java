package sanghun.project.howtouseai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sanghun.project.howtouseai.repository.CardRepository;
import sanghun.project.howtouseai.repository.CategoryRepository;
import sanghun.project.howtouseai.repository.LikeRepository;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final CategoryRepository categoryRepository;
    private final CardRepository cardRepository;
    private final LikeRepository likeRepository;

    @GetMapping("/test")
    public String test() {
        log.info("GCP MySQL 데이터베이스 연결 테스트");
        log.info("Category count: {}", categoryRepository.count());
        log.info("Card count: {}", cardRepository.count());
        log.info("Like count: {}", likeRepository.count());
        return "GCP MySQL 데이터베이스 연결 성공!";
    }
} 