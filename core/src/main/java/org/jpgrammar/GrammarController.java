package org.jpgrammar;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class GrammarController {

    @PostMapping("/analyze")
    public R analyzeSentence(@RequestBody SentenceRequest request) {
        R resp = new R();
        resp.setResult(GrammarAnalyzer.analyze(request.getSentence()));
        return resp;
    }

    public static class SentenceRequest {
        private String sentence;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }
    }

    public static class R {
        private List<GrammarItem> result;

        public List<GrammarItem> getResult() {
            return result;
        }

        public void setResult(List<GrammarItem> result) {
            this.result = result;
        }
    }

}
