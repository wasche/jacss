package com.wickedspiral.jacss.lexer;

/**
 * @author wasche
 * @since 2011.08.04
 */
public class UnrecognizedCharacterException extends Exception
{
    public UnrecognizedCharacterException(String s)
    {
        super(s);
    }
}
