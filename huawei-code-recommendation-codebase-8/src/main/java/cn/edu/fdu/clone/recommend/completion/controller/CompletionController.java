package cn.edu.fdu.clone.recommend.completion.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api/v1/recommend/completion")
public class CompletionController {

    @GetMapping("")
    public ResponseEntity<?> recommend() {

        return null;
    }
}
