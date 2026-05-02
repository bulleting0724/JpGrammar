# JpGrammar Project Overview

## 项目定位

JpGrammar 目标是对日语句子做自动拆解，并给出词形、读音、罗马音、英文释义等分析结果。当前实现是一个 Java/Spring Boot REST 服务，核心逻辑集中在 `core` Maven 模块中。

根目录 `README.md` 的描述是：自动拆分日语句子，并翻译为英文和中文。现有代码中主要实现了分词、词典查询、复合语法识别和 REST API 包装；中文翻译能力尚未看到独立实现。

## 仓库结构

```text
.
├── README.md
├── .agents/
│   └── overview.md
└── core/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/org/jpgrammar/
        │   │   ├── GrammarApplication.java
        │   │   ├── GrammarController.java
        │   │   ├── GrammarAnalyzer.java
        │   │   ├── CompositeGrammarDetector.java
        │   │   ├── JMDictLoader.java
        │   │   └── GrammarItem.java
        │   └── resources/
        │       ├── application.properties
        │       ├── composite_grammar.txt
        │       ├── JMdict_e.xml
        │       ├── JMdict_e_examp
        │       └── kanjidic2.xml
        └── test/
            ├── java/org/jpgrammar/AppTest.java
            └── resources/
                ├── composite_grammar.txt
                ├── JMdict_e.xml
                └── staff.xml
```

## 技术栈

- Java 20：`core/pom.xml` 中设置 `maven.compiler.source` 和 `target` 为 `20`。
- Spring Boot 3.2.5：用于启动 Web 服务和暴露 REST API。
- Kuromoji IPADIC 0.9.0：用于日语分词和词性、原形、活用形分析。
- Lombok 1.18.34：用于 DTO/getter/setter/log 注解。
- JUnit 3.8.1：当前测试仍是旧式 `TestCase` 风格。
- Maven：项目通过 `core/pom.xml` 构建；仓库中没有 Maven Wrapper。

## 运行入口与 API

应用入口是 `org.jpgrammar.GrammarApplication`，标准 Spring Boot 启动类。

主要接口在 `GrammarController`：

```http
POST /api/analyze
Content-Type: application/json

{
  "sentence": "..."
}
```

响应结构大致为：

```json
{
  "result": [
    {
      "type": "...",
      "surface": "...",
      "baseForm": "...",
      "kana": "...",
      "romaji": "...",
      "meanings": ["..."],
      "form": "..."
    }
  ]
}
```

如果本机安装了 Maven，可在 `core` 目录运行：

```bash
mvn spring-boot:run
```

然后调用：

```bash
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"sentence":"日本語の文"}'
```

## 核心流程

1. `GrammarController.analyzeSentence` 接收 JSON 请求，读取 `sentence`。
2. 调用 `GrammarAnalyzer.analyze(sentence)`。
3. `GrammarAnalyzer` 使用 Kuromoji `Tokenizer` 将句子切分为 tokens。
4. 分析每个 token：
   - 先调用 `CompositeGrammarDetector.matchCompositeGrammar` 检查当前位置是否匹配复合语法规则。
   - 若匹配，则合并多个 token 作为一个复合语法项输出。
   - 若不匹配，则按词性处理助词、动词、形容词和其他词。
5. `JMDictLoader` 从 `JMdict_e.xml` 加载词典，并通过 base form 或 surface 做 fallback 查询。
6. 最终返回 `List<GrammarItem>`，由 Spring MVC 序列化为 JSON。

## 关键类说明

### `GrammarAnalyzer`

主分析器。它在静态初始化阶段创建 `JMDictLoader` 并加载 `JMdict_e.xml`，之后每次请求使用 Kuromoji 分词并构造 `GrammarItem`。

需要注意：

- 词典在类加载时一次性解析完整 XML，启动成本和内存占用可能较高。
- 当前每次分析都会新建 `Tokenizer`，后续可考虑复用或注入为 Spring bean。
- 助词含义有少量上下文规则，但覆盖范围有限。

### `CompositeGrammarDetector`

复合语法检测器。启动时从 `composite_grammar.txt` 加载规则，每条规则会转换为正则表达式。目前最多向后拼接 6 个 token 的 surface 文本来尝试匹配。

规则文件支持 `*` 通配符，代码中会把它转换为 `.*`。

### `JMDictLoader`

JMdict XML 加载器。它解析每个 `<entry>`，提取：

- `keb`：汉字表记
- `reb`：假名读音
- `gloss`：英文释义

然后将表记和读音都放入内存字典。它还包含一个简易 kana-to-romaji 转换函数。

### `GrammarItem`

分析结果的数据结构。字段均为 public：

- `type`：词性或语法类型
- `surface`：原文表层形
- `baseForm`：原形
- `kana`：假名读音
- `romaji`：罗马音
- `meanings`：释义列表
- `form`：活用形信息

## 数据资源

`core/src/main/resources` 中包含较大的语言资源：

- `JMdict_e.xml`：约 63 MB，英文释义词典。
- `JMdict_e_examp`：约 72 MB，示例相关资源，当前代码未看到直接读取。
- `kanjidic2.xml`：约 16 MB，汉字词典资源，当前代码未看到直接读取。
- `composite_grammar.txt`：复合语法规则表。

`core/src/test/resources` 也包含一份约 63 MB 的 `JMdict_e.xml`，会显著增加仓库体积。

## 测试现状

测试类是 `core/src/test/java/org/jpgrammar/AppTest.java`：

- `testApp()` 只做 `assertTrue(true)`。
- `testGrammarAnalyzer()` 调用 `GrammarAnalyzer.analyze(...)` 并打印日志，但没有断言具体结果。

当前测试更像 smoke test，尚不能防止分析结果回归。

## 构建与验证记录

本次梳理时尝试在 `core` 目录执行 `mvn -v`，当前环境提示找不到 `mvn` 命令。因此尚未实际执行 Maven 构建或测试。

建议后续在安装 Maven 或添加 Maven Wrapper 后验证：

```bash
cd core
mvn test
mvn spring-boot:run
```

## 已知风险与维护建议

- 当前读取到的部分 Java 源码注释、字符串和测试句子呈现明显乱码；需要确认是终端编码显示问题，还是文件内容已经发生编码损坏。如果是源码实际损坏，项目可能无法编译。
- `pom.xml` 中也存在中文注释乱码迹象，建议统一用 UTF-8 重新保存相关文件。
- 项目没有 Maven Wrapper，其他机器上需要自行安装 Maven。
- 词典 XML 在启动时完整 DOM 解析，数据量大，未来可能遇到启动慢和内存压力；可以考虑 SAX/StAX 流式解析、预构建索引或缓存文件。
- `JMdict_e.xml` 在 main/test resources 中重复存放，仓库和构建产物都会变大。
- API 层直接调用静态分析器，后续如果要扩展配置、缓存、监控或测试，建议逐步改为 Spring bean。
- 测试缺少对 token 类型、词典释义、复合语法匹配、助词上下文规则的断言。
