package com.wickedspiral.jacss.lexer;

/**
 * @author wasche
 * @since 2011.08.04
 */
public interface ParserState
{
    public ParserState tokenFinished(Token token);
    public ParserState unsetTokenBuilder();
    public ParserState tokenize(char c) throws UnrecognizedCharacterException;
    public ParserState push(char c);
    public int getTokenLength();
    public char getLastCharacter();
    public int get(int index);
    public ParserState setTokenBuilder(Token token);
    public String getLastToken();
}
