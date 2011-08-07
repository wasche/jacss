package com.wickedspiral.jacss.lexer;

import com.wickedspiral.jacss.lexer.builder.CommentTokenBuilder;
import com.wickedspiral.jacss.lexer.builder.IdentifierTokenBuilder;
import com.wickedspiral.jacss.lexer.builder.NumberTokenBuilder;
import com.wickedspiral.jacss.lexer.builder.OpTokenBuilder;
import com.wickedspiral.jacss.lexer.builder.StringTokenBuilder;
import com.wickedspiral.jacss.lexer.builder.WhiteSpaceTokenBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author wasche
 * @since 2011.08.04
 */
public class Lexer implements ParserState
{
    private static WhiteSpaceTokenBuilder whiteSpaceTokenBuilder = new WhiteSpaceTokenBuilder();
    private static OpTokenBuilder opTokenBuilder = new OpTokenBuilder();
    private static IdentifierTokenBuilder identifierTokenBuilder = new IdentifierTokenBuilder();
    private static NumberTokenBuilder numberTokenBuilder = new NumberTokenBuilder();
    private static StringTokenBuilder stringTokenBuilder = new StringTokenBuilder();
    private static CommentTokenBuilder commentTokenBuilder = new CommentTokenBuilder();

    private static final Map<Character, TokenBuilder> TOKEN_MAP = new HashMap<Character, TokenBuilder>();
    private static final Map<Token, TokenBuilder> TOKEN_BUILDER_MAP = new HashMap<Token, TokenBuilder>();

    static {
        for (char t : "{}[]()+*=.,;:>~|\\%$#@^!".toCharArray())
        {
            TOKEN_MAP.put(t, opTokenBuilder);
        }
        for (char t : "_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
        {
            TOKEN_MAP.put(t, identifierTokenBuilder);
        }
        for (char t : "-.0123456789".toCharArray())
        {
            TOKEN_MAP.put(t, numberTokenBuilder);
        }
        TOKEN_MAP.put('\'', stringTokenBuilder);
        TOKEN_MAP.put('"', stringTokenBuilder);
        TOKEN_MAP.put('/', commentTokenBuilder);
        TOKEN_MAP.put(' ', whiteSpaceTokenBuilder);
        TOKEN_MAP.put('\t', whiteSpaceTokenBuilder);
        TOKEN_MAP.put('\n', whiteSpaceTokenBuilder);

        TOKEN_BUILDER_MAP.put(Token.OP, opTokenBuilder);
        TOKEN_BUILDER_MAP.put(Token.IDENTIFIER, identifierTokenBuilder);
        TOKEN_BUILDER_MAP.put(Token.NUMBER, numberTokenBuilder);
        TOKEN_BUILDER_MAP.put(Token.STRING, stringTokenBuilder);
        TOKEN_BUILDER_MAP.put(Token.COMMENT, commentTokenBuilder);
        TOKEN_BUILDER_MAP.put(Token.WHITESPACE, whiteSpaceTokenBuilder);
    }

    private List<TokenListener> tokenListeners;

    private CharBuffer token;
    private char lastChar;
    private String lastToken;
    private TokenBuilder builder;
    private long offset = 0;

    public Lexer()
    {
        tokenListeners = new LinkedList<TokenListener>();

        token = new CharBuffer();
    }

    public void addTokenListener(TokenListener listener)
    {
        tokenListeners.add(listener);
    }

    public void parse(InputStream in) throws IOException, UnrecognizedCharacterException
    {
        BufferedInputStream bis = new BufferedInputStream(in, 255);
        InputStreamReader reader = new InputStreamReader(bis);
        char c;
        while ((c = (char) reader.read()) != 65535)
        {
            tokenize(c);
            offset++;
        }

        // finish whatever is in the buffer
        if (builder != null)
        {
            builder.handle(this, '\0');
        }
        else if (token.length() > 0)
        {
            tokenFinished(Token.OP);
        }

        for (TokenListener listener : tokenListeners)
        {
            listener.end();
        }
    }

    public ParserState tokenize(char c) throws UnrecognizedCharacterException
    {
        if ('\0' == c) return this;
        if (builder == null)
        {
            builder = TOKEN_MAP.get(c);
            if (builder == null)
            {
                throw new UnrecognizedCharacterException("Unrecognized character at offset " + offset + ": " + c + " (" + ((int)c) +")");
            }
        }
        builder.handle(this, c);
        return this;
    }

    public ParserState tokenFinished(Token t)
    {
        String s = token.toString();
        for (TokenListener listener : tokenListeners)
        {
            listener.token(t, s);
        }
        token.clear();
        lastToken = s;
        return this;
    }

    public ParserState push(char c)
    {
        token.append(c);
        lastChar = c;
        return this;
    }

    public ParserState unsetTokenBuilder()
    {
        builder = null;
        return this;
    }

    public ParserState setTokenBuilder(Token token)
    {
        builder = TOKEN_BUILDER_MAP.get(token);
        return this;
    }

    public int getTokenLength()
    {
        return token.length();
    }

    public char getLastCharacter()
    {
        return lastChar;
    }

    public String getLastToken()
    {
        return lastToken;
    }

    public int get(int index)
    {
        return token.charAt(index);
    }
}
