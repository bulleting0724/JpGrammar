package org.jpgrammar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GrammarController {

    @PostMapping("/analyze")
    public R analyzeSentence(@RequestBody SentenceRequest request) {
        R resp = new R();
        resp.setResult(GrammarAnalyzer.analyze(request.getSentence()));
        return resp;
    }

    @Setter
    @Getter
    public static class SentenceRequest {
        private String sentence;

    }

    @Data
    public class R {
        private List<GrammarItem> result;
    }

}
