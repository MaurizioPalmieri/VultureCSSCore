
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
package com.dotmaui.vulturecss.models;

import com.helger.css.decl.CSSDeclaration;

/**
 * Represents a CSS declaration and its optimization metadata.
 *
 * @author .Maui
 * @version 1.1
 */
public class CarcassDeclaration {

    private boolean isImportant;
    private String selector;
    private CSSDeclaration declaration;

    /**
     * Creates a new declaration metadata object.
     *
     * @param isImportant whether the declaration uses !important
     * @param selector the CSS property or selector associated with the declaration
     * @param declaration the original CSS declaration
     */
    public CarcassDeclaration(
            boolean isImportant,
            String selector,
            CSSDeclaration declaration) {

        this.isImportant = isImportant;
        this.selector = selector;
        this.declaration = declaration;
    }

    /**
     * Returns whether the declaration is marked as !important.
     *
     * @return true if the declaration is important
     */
    public boolean isImportant() {
        return isImportant;
    }

    /**
     * Legacy getter retained for backward compatibility.
     *
     * @return true if the declaration is important
     */
    public boolean isIsImportant() {
        return isImportant;
    }

    /**
     * Updates the importance flag.
     *
     * @param isImportant the new importance value
     */
    public void setIsImportant(boolean isImportant) {
        this.isImportant = isImportant;
    }

    /**
     * Returns the associated selector or property identifier.
     *
     * @return the selector or property identifier
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Updates the associated selector or property identifier.
     *
     * @param selector the new selector or property identifier
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }

    /**
     * Returns the original CSS declaration.
     *
     * @return the CSS declaration
     */
    public CSSDeclaration getDeclaration() {
        return declaration;
    }

    /**
     * Updates the CSS declaration.
     *
     * @param declaration the new CSS declaration
     */
    public void setDeclaration(CSSDeclaration declaration) {
        this.declaration = declaration;
    }
}
