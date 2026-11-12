
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

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.ICSSTopLevelRule;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides conservative CSS optimization methods.
 *
 * The optimizer removes only declarations that are exact duplicates.
 * It preserves declaration order, selector order, media queries and
 * unsupported nested rules to avoid changing the original CSS behavior.
 *
 * @author .Maui
 * @version 1.2
 */
public final class VultureCSSCoreOptimize {

    private VultureCSSCoreOptimize() {
        // Utility class.
    }

    /**
     * Optimizes the style rules contained in a media rule.
     *
     * All media queries and nested rules are preserved.
     *
     * @param cssMediaRule the media rule to optimize
     * @return a new optimized media rule, or null if the input is null
     */
    public static CSSMediaRule optimizeMediaRule(CSSMediaRule cssMediaRule) {

        if (cssMediaRule == null) {
            return null;
        }

        CSSMediaRule optimizedMediaRule = new CSSMediaRule();

        // Preserve all media queries.
        for (var mediaQuery : cssMediaRule.getAllMediaQueries()) {
            optimizedMediaRule.addMediaQuery(mediaQuery);
        }

        // Preserve the original rule order and optimize only style rules.
        for (ICSSTopLevelRule rule : cssMediaRule.getAllRules()) {

            if (rule instanceof CSSStyleRule styleRule) {

                List<CSSStyleRule> optimizedRules =
                        optimizeCSSStyleRules(List.of(styleRule));

                for (CSSStyleRule optimizedRule : optimizedRules) {
                    optimizedMediaRule.addRule(optimizedRule);
                }

            } else if (rule != null) {

                // Preserve nested and unsupported CSS rules.
                optimizedMediaRule.addRule(rule);
            }
        }

        return optimizedMediaRule;
    }

    /**
     * Removes exact duplicate declarations from CSS style rules.
     *
     * The original declaration order is preserved. Only declarations
     * with identical serialized CSS representations are considered
     * duplicates.
     *
     * This conservative approach preserves fallback declarations,
     * shorthand/longhand combinations and !important precedence.
     *
     * @param rules the style rules to optimize
     * @return a list of optimized style rules
     */
    public static List<CSSStyleRule> optimizeCSSStyleRules(
            List<CSSStyleRule> rules) {

        List<CSSStyleRule> optimizedStyleRules = new ArrayList<>();

        if (rules == null || rules.isEmpty()) {
            return optimizedStyleRules;
        }

        for (CSSStyleRule cssStyleRule : rules) {

            if (cssStyleRule == null) {
                continue;
            }

            List<CSSDeclaration> originalDeclarations =
                    cssStyleRule.getAllDeclarations();

            CSSStyleRule optimizedStyleRule = new CSSStyleRule();

            // Preserve all selectors in their original order.
            for (CSSSelector selector : cssStyleRule.getAllSelectors()) {
                optimizedStyleRule.addSelector(selector);
            }

            // Find duplicate declarations starting from the end.
            // This ensures that the last identical declaration is kept.
            Set<String> seenDeclarations = new HashSet<>();
            boolean[] keepDeclaration =
                    new boolean[originalDeclarations.size()];

            for (int i = originalDeclarations.size() - 1; i >= 0; i--) {

                CSSDeclaration declaration = originalDeclarations.get(i);

                if (declaration == null) {
                    continue;
                }

                String declarationText =
                        declaration.getAsCSSString().trim();

                if (seenDeclarations.add(declarationText)) {
                    keepDeclaration[i] = true;
                }
            }

            // Rebuild the rule in its original declaration order.
            for (int i = 0; i < originalDeclarations.size(); i++) {

                if (keepDeclaration[i]) {
                    optimizedStyleRule.addDeclaration(
                            originalDeclarations.get(i));
                }
            }

            optimizedStyleRules.add(optimizedStyleRule);
        }

        return optimizedStyleRules;
    }
}
