package com.wickedspiral.jacss.lexer.builder;

import com.wickedspiral.jacss.lexer.ParserState;
import com.wickedspiral.jacss.lexer.Token;
import com.wickedspiral.jacss.lexer.TokenBuilder;
import com.wickedspiral.jacss.lexer.UnrecognizedCharacterException;

/**
 * @author wasche
 * @since 2011.08.04
 */
public class OpTokenBuilder implements TokenBuilder
{
    public void handle(ParserState state, char c) throws UnrecognizedCharacterException
    {
        if ('=' == c)
        {
            state.push(c)
                 .tokenFinished(Token.EQUALS)
                 .unsetTokenBuilder();
        }
        else if (state.getTokenLength() == 0)
        {
            state.push(c);
        }
        else
        {
            state.tokenFinished(Token.getByCharacter(state.getLastCharacter()))
                 .unsetTokenBuilder()
                 .tokenize(c);
        }
    }
}
