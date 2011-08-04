package com.wickedspiral.jacss.lexer;

/**
 * @author wasche
 * @since 2011.08.04
 */
public interface TokenBuilder
{
    public void handle(ParserState state, char c) throws UnrecognizedCharacterException;
}
