
# VultureCSS Core

**Remove unused CSS from your stylesheets.**

VultureCSS Core is a Java library and command-line tool designed to analyze HTML and CSS code and remove unused CSS rules.

By identifying the styles actually used by an HTML document, VultureCSS Core helps reduce stylesheet size and improve website performance while preserving the original appearance of your website.

VultureCSS Core can be used as a standalone executable JAR or integrated directly into your Java applications through its API.

**Current version:** 0.4.0 BETA

## Features

- **Unused CSS removal:** Identify and remove CSS rules that are not used by your HTML documents.
- **Multiple input methods:** Process local files, remote URLs, or HTML pages with externally linked stylesheets.
- **CSS optimization:** Merge and optimize CSS stylesheets.
- **Java API:** Easily integrate VultureCSS Core into your own Java applications.
- **Command-line interface:** Process HTML and CSS directly from the terminal.

## Requirements

- Java 17 or later.

## Usage

### 1. Command-line interface

Process a local HTML file and its CSS stylesheet:

```bash
java -jar VultureCSSCore.jar -css style.css -html index.html -out style.min.css
```

Process HTML and CSS files hosted online:

```bash
java -jar VultureCSSCore.jar -css https://example.com/style.css -html https://example.com
```

### 2. Java API – Process HTML and CSS

You can integrate VultureCSS Core directly into your Java application.

```java
URL htmlUrl = new URL("https://dotmaui.com");
URL cssUrl = new URL("https://dotmaui.com/style.css");

VultureCSSCore v = new VultureCSSCore(cssUrl, htmlUrl);

List<Carcass> carcasses = v.Process();

carcasses.forEach(c -> {
    System.out.println(c.getUsedCSS());
});
```

### 3. Process an HTML page and its linked stylesheets

You can also provide only an HTML URL. VultureCSS Core will automatically discover and process the linked CSS stylesheets.

**Command line:**

```bash
java -jar VultureCSSCore.jar -html https://dotmaui.com
```

**Java API:**

```java
URL url = new URL("https://dotmaui.com");

VultureCSSCore v = new VultureCSSCore();
v.setHtmlUrl(url);

List<Carcass> carcasses = v.Process();

carcasses.forEach(c -> {
    System.out.println(c.getUsedCSS());
});
```

### 4. Merge and optimize CSS from multiple URLs

VultureCSS Core also provides a method to download, merge, and optimize multiple CSS stylesheets.

```java
List<String> urls = new ArrayList<>();

urls.add("https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css");
urls.add("https://dotmaui.com/assets/css/production/dotmaui.com_app_min.css?v=2");

String result = VultureCSSCore.mergeAndOptimizeCSSFromUrls(urls);

System.out.println(result);
```

## Third-party libraries

VultureCSS Core relies on the following open-source libraries:

- [jsoup](https://github.com/jhy/jsoup) – HTML parsing and manipulation.
- [HtmlUnit](https://github.com/HtmlUnit/htmlunit) – Browser simulation and rendered page retrieval.
- [ph-css](https://github.com/phax/ph-css) – CSS parsing and writing.
- [jStyleParser](https://github.com/radkovo/jStyleParser) – CSS parsing and correction.

## Support and bug reports

For bug reports, support, or customization requests, feel free to contact us via Telegram:

**[@DotMaui](https://t.me/DotMaui)**

## License

VultureCSS Core is released under the [MIT License](https://opensource.org/license/mit/).
