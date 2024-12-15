package org.jpgrammar;

import lombok.Data;

import java.util.List;

@Data
public class JmDict {
    private List<BaeldungArticle> articleList;
    // usual getters and setters
    private List<Entry> entryList;
}
