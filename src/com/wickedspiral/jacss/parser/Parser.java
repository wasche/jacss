package com.wickedspiral.jacss.parser;

import com.wickedspiral.jacss.lexer.Token;
import com.wickedspiral.jacss.lexer.TokenListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author wasche
 * @since 2011.08.04
 */
public class Parser implements TokenListener
{
    private static final String MS_ALPHA = "progid:dximagetransform.microsoft.alpha(opacity=";
    private static final Collection<String> UNITS = new HashSet<String>(Arrays.asList("px", "em", "pt", "in", "cm", "mm", "pc", "ex", "%"));
    private static final Collection<String> KEYWORDS = new HashSet<String>(Arrays.asList("normal", "bold", "italic", "serif", "sans-serif", "fixed"));
    private static final Collection<String> BOUNDARY_OPS = new HashSet<String>(Arrays.asList("{", "}", ">", ";", ":", ",")); // or comment
    private static final Collection<String> NONE_PROPERTIES = new HashSet<String>();

    static
    {
        NONE_PROPERTIES.add("outline");
        for (String property : new String[] {"border", "margin", "padding"})
        {
            NONE_PROPERTIES.add(property);
            for (String edge : new String[]{"top", "left", "bottom", "right"})
            {
                NONE_PROPERTIES.add(property + "-" + edge);
            }
        }
    }

    private boolean inRule = false;
    private boolean space = false;
    private boolean charset = false;
    private boolean at = false;
    private boolean ie5mac = false;

    public void token(Token token, String value)
    {
    }

    public void end()
    {
    }
}
