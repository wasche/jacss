package com.wickedspiral.jacss.lexer.builder;

import com.wickedspiral.jacss.lexer.ParserState;
import com.wickedspiral.jacss.lexer.Token;
import com.wickedspiral.jacss.lexer.TokenBuilder;

/**
 * @author wasche
 * @since 2011.08.04
 */
public class StringTokenBuilder implements TokenBuilder
{
    public void handle(ParserState state, char c)
    {
        if (state.getTokenLength() == 0 || c != state.get(0) || state.getLastCharacter() == '\\')
        {
            state.push(c);
        }
        else
        {
            state.push(c)
                 .tokenFinished(Token.STRING)
                 .unsetTokenBuilder();
        }
    }
}
