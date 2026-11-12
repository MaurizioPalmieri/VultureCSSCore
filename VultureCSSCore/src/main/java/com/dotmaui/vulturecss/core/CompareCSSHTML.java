
/*
 * The MIT License
 *
 * Copyright 2026 .Maui | dotmaui.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 */
package com.dotmaui.vulturecss.core;

import static com.dotmaui.vulturecss.core.VultureCSSCoreParser.getUsedRulesFromMediaRule;
import static com.dotmaui.vulturecss.core.VultureCSSCoreParser.getUsedRulesFromSupportsRule;

import com.dotmaui.vulturecss.jstyleparser.VultureCSSWithjStyleParser;
import com.dotmaui.vulturecss.models.VultureCSSOptions;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ICSSTopLevelRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriter;
import com.helger.css.writer.CSSWriterSettings;
import cz.vutbr.web.css.CSSException;
import java.io.IOException;

/**
 * Compares CSS rules against HTML content and generates a stylesheet
 * containing the CSS rules used by the provided HTML document.
 *
 * If the initial CSS parsing fails, jStyleParser is used to attempt
 * to correct the stylesheet before parsing it again.
 *
 * @author .Maui
 * @version 1.2
 */
public final class CompareCSSHTML {

    private CompareCSSHTML() {
        // Utility class.
    }

    /**
     * Processes the provided HTML and CSS and returns a stylesheet
     * containing the rules used by the HTML document.
     *
     * @param html the HTML content used to check selector usage
     * @param css the CSS stylesheet to process
     * @param options the CSS processing options
     * @return the filtered CSS stylesheet
     * @throws IOException if an I/O error occurs during CSS correction
     * @throws CSSException if CSS parsing fails
     * @throws IllegalArgumentException if the CSS input is invalid
     */
    public static String process(
            String html,
            String css,
            VultureCSSOptions options) throws IOException, CSSException {

        if (css == null) {
            throw new IllegalArgumentException("CSS input must not be null.");
        }

        if (options == null) {
            options = new VultureCSSOptions();
        }

        CascadingStyleSheet initialCSS = parseCSS(css);

        if (initialCSS == null) {
            throw new IllegalArgumentException(
                    "Unable to parse the provided CSS stylesheet.");
        }

        CascadingStyleSheet finalCSS = new CascadingStyleSheet();

        if (html == null || html.isBlank()) {
            return writeCSS(finalCSS, options);
        }

        VultureCSSCoreHTMLChecker htmlChecker =
                new VultureCSSCoreHTMLChecker(html, options);

        ICommonsList<ICSSTopLevelRule> rules = initialCSS.getAllRules();

        for (ICSSTopLevelRule rule : rules) {

            if (rule == null) {
                continue;
            }

            if (rule instanceof CSSStyleRule styleRule) {

                addStyleRuleIfUsed(finalCSS, styleRule, htmlChecker);

            } else if (rule instanceof CSSMediaRule mediaRule) {

                CSSMediaRule finalMediaRule =
                        getUsedRulesFromMediaRule(mediaRule, htmlChecker);

                if (finalMediaRule != null) {
                    finalCSS.addRule(finalMediaRule);
                }

            } else if (rule instanceof CSSSupportsRule supportsRule) {

                CSSSupportsRule finalSupportsRule =
                        getUsedRulesFromSupportsRule(supportsRule, htmlChecker);

                if (finalSupportsRule != null) {
                    finalCSS.addRule(finalSupportsRule);
                }

            } else {

                // Preserve all other CSS rules.
                finalCSS.addRule(rule);
            }
        }

        return writeCSS(finalCSS, options);
    }

    /**
     * Parses a CSS stylesheet and attempts correction with jStyleParser
     * if the initial parser does not return a stylesheet.
     *
     * @param css the CSS stylesheet
     * @return the parsed stylesheet
     * @throws IOException if CSS correction fails
     * @throws CSSException if parsing fails
     */
    private static CascadingStyleSheet parseCSS(
            String css) throws IOException, CSSException {

        CascadingStyleSheet initialCSS =
                CSSReader.readFromString(css, ECSSVersion.LATEST);

        if (initialCSS != null) {
            return initialCSS;
        }

        String correctedCSS = VultureCSSWithjStyleParser.ParseCSS(css);

        if (correctedCSS == null || correctedCSS.isBlank()) {
            return null;
        }

        return CSSReader.readFromString(
                correctedCSS,
                ECSSVersion.LATEST);
    }

    /**
     * Adds a style rule only if at least one of its selectors is used
     * by the provided HTML document.
     *
     * @param finalCSS the destination stylesheet
     * @param styleRule the style rule to check
     * @param htmlChecker the HTML selector checker
     */
    private static void addStyleRuleIfUsed(
            CascadingStyleSheet finalCSS,
            CSSStyleRule styleRule,
            VultureCSSCoreHTMLChecker htmlChecker) {

        ICommonsList<CSSSelector> selectors =
                styleRule.getAllSelectors();

        if (selectors.isEmpty()) {
            return;
        }

        if (selectors.size() > 1) {

            CSSStyleRule resultRule =
                    htmlChecker.multiSelectorControl(styleRule);

            if (resultRule != null
                    && resultRule.hasDeclarations()
                    && resultRule.hasSelectors()) {

                finalCSS.addRule(resultRule);
            }

            return;
        }

        String selector = selectors.get(0).getAsCSSString();

        if (htmlChecker.isSelectorUsed(selector)) {
            finalCSS.addRule(styleRule);
        }
    }

    /**
     * Serializes the resulting stylesheet.
     *
     * @param finalCSS the stylesheet to serialize
     * @param options the CSS processing options
     * @return the serialized CSS
     */
    private static String writeCSS(
            CascadingStyleSheet finalCSS,
            VultureCSSOptions options) {

        CSSWriterSettings writerSettings =
                new CSSWriterSettings(ECSSVersion.LATEST, false);

        if (options.isMinifyCSSOutput()) {
            writerSettings.setOptimizedOutput(true);
            writerSettings.setRemoveUnnecessaryCode(true);
        }

        CSSWriter cssWriter = new CSSWriter(writerSettings);
        cssWriter.setHeaderText("");

        return cssWriter.getCSSAsString(finalCSS);
    }
}
