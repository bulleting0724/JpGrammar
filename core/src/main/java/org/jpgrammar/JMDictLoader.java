package org.jpgrammar;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public class JMDictLoader {

    private final Map<String, List<String>> dict = new HashMap<>();

    public void load(String resourcePath) throws Exception {
// 通过类加载器读取 resources 目录中的 jmdict.xml
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("资源未找到: " + resourcePath);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 设置实体展开数量上限（设置为无限大或较大值）
        try {
            factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "1000000");
        } catch (IllegalArgumentException e) {
            // 某些 JDK 不支持这个属性，不处理即可
            System.err.println("JAXP 安全属性设置失败，忽略。");
        }
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        doc.getDocumentElement().normalize();

        NodeList entryList = doc.getElementsByTagName("entry");

        for (int i = 0; i < entryList.getLength(); i++) {
            Element entry = (Element) entryList.item(i);
            List<String> words = new ArrayList<>();

            NodeList kebList = entry.getElementsByTagName("keb");
            for (int j = 0; j < kebList.getLength(); j++) {
                words.add(kebList.item(j).getTextContent());
            }

            NodeList rebList = entry.getElementsByTagName("reb");
            for (int j = 0; j < rebList.getLength(); j++) {
                words.add(rebList.item(j).getTextContent());
            }

            List<String> glosses = new ArrayList<>();
            NodeList glossList = entry.getElementsByTagName("gloss");
            for (int j = 0; j < glossList.getLength(); j++) {
                glosses.add(glossList.item(j).getTextContent());
            }

            for (String word : words) {
                dict.put(word, glosses);
            }
        }
    }

    public List<String> getMeanings(String word) {
        return dict.getOrDefault(word, Collections.emptyList());
    }
}
