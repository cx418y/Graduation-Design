package cn.edu.fdu.clone.recommend.reconstruct.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/recommend/reconstruct")
public class ReconstructController {

    @PostMapping("")
    public ResponseEntity<?> recommend() {

        return null;
    }
}
