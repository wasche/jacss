package com.wickedspiral.jacss.lexer;

import java.util.Arrays;

/**
 * @author wasche
 * @since 2011.08.07
 */
public class CharBuffer implements CharSequence, Appendable, Comparable<CharBuffer>
{
    private static final int GROW_SIZE = 16;

    private char[] array;
    private int length;

    public CharBuffer()
    {
        this(255);
    }

    public CharBuffer(int len)
    {
        array = new char[len];
        length = 0;
    }

    public int length()
    {
        return length;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        CharBuffer that = (CharBuffer) o;

        return length == that.length && Arrays.equals(array, that.array);

    }

    @Override
    public int hashCode()
    {
        int result = array != null ? Arrays.hashCode(array) : 0;
        result = 31 * result + length;
        return result;
    }

    public char charAt(int i)
    {
        if (i < 0 || i >= length) throw new IndexOutOfBoundsException("Invalid offset: " + i);
        return array[i];
    }

    public CharSequence subSequence(int start, int end)
    {
        return null;
    }

    public Appendable append(CharSequence charSequence)
    {
        return append(charSequence, 0, charSequence.length());
    }

    public Appendable append(CharSequence charSequence, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            append(charSequence.charAt(i));
        }
        return this;
    }

    public Appendable append(char c)
    {
        if (array.length == length)
        {
            array = Arrays.copyOf(array, length + GROW_SIZE);
        }
        array[length++] = c;
        return this;
    }

    public int compareTo(CharBuffer charBuffer)
    {
        for (int i = 0, l = Math.min(length, charBuffer.length); i < l; i++)
        {
            if (array[i] == charBuffer.array[i]) continue;
            return array[i] < charBuffer.array[i] ? -1 : 1;
        }
        return 0;
    }

    public void clear()
    {
        length = 0;
    }

    @Override
    public String toString()
    {
        return new String(array, 0, length);
    }
}
