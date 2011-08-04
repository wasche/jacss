package com.wickedspiral.jacss.lexer.builder;

import com.wickedspiral.jacss.lexer.ParserState;
import com.wickedspiral.jacss.lexer.Token;
import com.wickedspiral.jacss.lexer.TokenBuilder;
import com.wickedspiral.jacss.lexer.UnrecognizedCharacterException;

/**
 * @author wasche
 * @since 2011.08.04
 */
public class WhiteSpaceTokenBuilder implements TokenBuilder
{
    public void handle(ParserState state, char c) throws UnrecognizedCharacterException
    {
        switch (c)
        {
            case ' ':
            case '\t':
            case '\n':
                state.push(c);
                break;
            default:
                state.tokenFinished(Token.WHITESPACE)
                     .unsetTokenBuilder()
                     .tokenize(c);
        }
    }
}
