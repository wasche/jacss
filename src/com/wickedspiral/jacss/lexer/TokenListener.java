package com.wickedspiral.jacss.lexer;

/**
 * @author wasche
 * @since 2011.08.04
 */
public interface TokenListener
{
    public void token(Token token, String value);
    public void end();
}
