
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

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSImportRule;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ICSSTopLevelRule;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides conservative CSS rule merging methods.
 *
 * Only consecutive style rules with identical selector lists are merged.
 * This preserves the original cascade order and avoids moving declarations
 * across unrelated CSS rules.
 *
 * Other rule types are preserved without modification.
 *
 * @author .Maui
 * @version 1.2
 */
public final class VultureCSSCoreMergify {

    private VultureCSSCoreMergify() {
        // Utility class.
    }

    /**
     * Merges consecutive CSS style rules with identical selectors.
     *
     * The original stylesheet and its rules are not modified.
     *
     * @param rules the CSS top-level rules to process
     * @return a stylesheet containing the merged rules
     */
    public static CascadingStyleSheet MergeRules(
            ICommonsList<ICSSTopLevelRule> rules) {

        CascadingStyleSheet result = new CascadingStyleSheet();

        if (rules == null || rules.isEmpty()) {
            return result;
        }

        int importRulesIndex = 0;
        int index = 0;

        while (index < rules.size()) {

            ICSSTopLevelRule currentRule = rules.get(index);

            if (currentRule == null) {
                index++;
                continue;
            }

            if (currentRule instanceof CSSStyleRule styleRule) {

                String selectorKey = getSelectorKey(styleRule);

                if (selectorKey == null) {
                    result.addRule(styleRule);
                    index++;
                    continue;
                }

                List<CSSStyleRule> consecutiveRules = new ArrayList<>();
                consecutiveRules.add(styleRule);

                int nextIndex = index + 1;

                // Merge only immediately consecutive rules with identical selectors.
                while (nextIndex < rules.size()) {

                    ICSSTopLevelRule nextRule = rules.get(nextIndex);

                    if (!(nextRule instanceof CSSStyleRule nextStyleRule)) {
                        break;
                    }

                    String nextSelectorKey = getSelectorKey(nextStyleRule);

                    if (!selectorKey.equals(nextSelectorKey)) {
                        break;
                    }

                    consecutiveRules.add(nextStyleRule);
                    nextIndex++;
                }

                if (consecutiveRules.size() == 1) {

                    result.addRule(styleRule);

                } else {

                    CSSStyleRule mergedRule = mergeConsecutiveStyleRules(
                            consecutiveRules);

                    result.addRule(mergedRule);
                }

                index = nextIndex;

            } else if (currentRule instanceof CSSImportRule importRule) {

                result.addImportRule(importRulesIndex, importRule);
                importRulesIndex++;

                index++;

            } else {

                // Preserve media queries, supports rules and all other rules.
                result.addRule(currentRule);
                index++;
            }
        }

        return result;
    }

    /**
     * Merges the declarations of consecutive style rules.
     *
     * Selector and declaration order are preserved.
     *
     * @param rules the consecutive style rules
     * @return a new merged style rule
     */
    private static CSSStyleRule mergeConsecutiveStyleRules(
            List<CSSStyleRule> rules) {

        CSSStyleRule mergedRule = new CSSStyleRule();

        if (rules == null || rules.isEmpty()) {
            return mergedRule;
        }

        CSSStyleRule firstRule = rules.get(0);

        // Copy selectors from the first rule.
        for (CSSSelector selector : firstRule.getAllSelectors()) {
            mergedRule.addSelector(selector);
        }

        // Append declarations in their original cascade order.
        for (CSSStyleRule rule : rules) {

            for (CSSDeclaration declaration : rule.getAllDeclarations()) {
                mergedRule.addDeclaration(declaration);
            }
        }

        return mergedRule;
    }

    /**
     * Creates a stable representation of a style rule's selector list.
     *
     * @param rule the style rule
     * @return the selector representation, or null if no selectors exist
     */
    private static String getSelectorKey(CSSStyleRule rule) {

        if (rule == null || rule.getAllSelectors().isEmpty()) {
            return null;
        }

        StringBuilder key = new StringBuilder();

        for (CSSSelector selector : rule.getAllSelectors()) {

            if (selector == null) {
                return null;
            }

            if (!key.isEmpty()) {
                key.append(',');
            }

            key.append(selector.getAsCSSString());
        }

        return key.toString();
    }
}
