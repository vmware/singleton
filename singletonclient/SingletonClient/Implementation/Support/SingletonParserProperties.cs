/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;

namespace SingletonClient.Implementation.Support
{
    public sealed class SingletonParserProperties : ISourceParser
    {
        private Hashtable kvTable = new Hashtable();

        public Hashtable Parse(string text)
        {
            Load(new LineReader(text.ToCharArray()));
            return kvTable;
        }

        private string Put(string key, string message)
        {
            string oldMessage = (string)kvTable[key];
            kvTable[key] = message;
            return oldMessage;
        }

        private void Load(LineReader lr)
        {
            char[] convtBuf = new char[1024];
            int limit;
            int keyLen;
            int valueStart;
            char c;
            bool hasSep;
            bool precedingBackslash;

            while ((limit = lr.ReadLine()) >= 0)
            {
                c = '\0';
                keyLen = 0;
                valueStart = limit;
                hasSep = false;

                precedingBackslash = false;
                while (keyLen < limit)
                {
                    c = lr.lineBuf[keyLen];
                    //need check if escaped.
                    if ((c == '=' || c == ':') && !precedingBackslash)
                    {
                        valueStart = keyLen + 1;
                        hasSep = true;
                        break;
                    }
                    else if ((c == ' ' || c == '\t' || c == '\f') && !precedingBackslash)
                    {
                        valueStart = keyLen + 1;
                        break;
                    }
                    if (c == '\\')
                    {
                        precedingBackslash = !precedingBackslash;
                    }
                    else
                    {
                        precedingBackslash = false;
                    }
                    keyLen++;
                }
                while (valueStart < limit)
                {
                    c = lr.lineBuf[valueStart];
                    if (c != ' ' && c != '\t' && c != '\f')
                    {
                        if (!hasSep && (c == '=' || c == ':'))
                        {
                            hasSep = true;
                        }
                        else
                        {
                            break;
                        }
                    }
                    valueStart++;
                }
                string key = LoadConvert(lr.lineBuf, 0, keyLen, convtBuf);
                string value = LoadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
                Put(key, value);
            }
        }

        private string LoadConvert(char[] inText, int off, int len, char[] convtBuf)
        {
            if (convtBuf.Length < len)
            {
                int newLen = len * 2;
                convtBuf = new char[newLen];
            }
            char aChar;
            char[] outText = convtBuf;
            int outLen = 0;
            int end = off + len;

            while (off < end)
            {
                aChar = inText[off++];
                if (aChar == '\\')
                {
                    aChar = inText[off++];
                    if (aChar == 'u')
                    {
                        // Read the xxxx
                        int value = 0;
                        for (int i = 0; i < 4; i++)
                        {
                            aChar = inText[off++];
                            switch (aChar)
                            {
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                    value = (value << 4) + aChar - '0';
                                    break;
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                    value = (value << 4) + 10 + aChar - 'a';
                                    break;
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                    value = (value << 4) + 10 + aChar - 'A';
                                    break;
                                default:
                                    throw new System.ArgumentException("Malformed \\uxxxx encoding.");
                            }
                        }
                        outText[outLen++] = (char)value;
                    }
                    else
                    {
                        if (aChar == 't') aChar = '\t';
                        else if (aChar == 'r') aChar = '\r';
                        else if (aChar == 'n') aChar = '\n';
                        else if (aChar == 'f') aChar = '\f';
                        outText[outLen++] = aChar;
                    }
                }
                else
                {
                    outText[outLen++] = aChar;
                }
            }
            return new string(outText, 0, outLen);
        }
    }

    public sealed class LineReader
    {
        private const int MAX_LINE_BUFFER = 1024;

        public LineReader(char[] inCharBuf)
        {
            this.inCharBuf = inCharBuf;
            if (inCharBuf != null)
            {
                inLimit = inCharBuf.Length;
            }
        }

        public char[] lineBuf = new char[MAX_LINE_BUFFER];

        private char[] inCharBuf;
        private int inLimit = 0;
        private int inOff = 0;

        public int ReadLine()
        {
            int len = 0;
            char c = '\0';

            bool skipWhiteSpace = true;
            bool isCommentLine = false;
            bool isNewLine = true;
            bool appendedLineBegin = false;
            bool precedingBackslash = false;
            bool skipLF = false;

            while (true)
            {
                if (inOff >= inLimit)
                {
                    if (len == 0 || isCommentLine)
                    {
                        return -1;
                    }
                    if (precedingBackslash)
                    {
                        len--;
                    }
                    return len;
                }

                //The line below is equivalent to calling a
                //ISO8859-1 decoder.
                c = inCharBuf[inOff++];

                if (skipLF)
                {
                    skipLF = false;
                    if (c == '\n')
                    {
                        continue;
                    }
                }
                if (skipWhiteSpace)
                {
                    if (c == ' ' || c == '\t' || c == '\f')
                    {
                        continue;
                    }
                    if (!appendedLineBegin && (c == '\r' || c == '\n'))
                    {
                        continue;
                    }
                    skipWhiteSpace = false;
                    appendedLineBegin = false;
                }
                if (isNewLine)
                {
                    isNewLine = false;
                    if (c == '#' || c == '!')
                    {
                        isCommentLine = true;
                        continue;
                    }
                }

                if (c != '\n' && c != '\r')
                {
                    lineBuf[len++] = c;
                    if (len == lineBuf.Length)
                    {
                        int newLength = lineBuf.Length * 2;
                        char[] buf = new char[newLength];
                        lineBuf.CopyTo(buf, 0);
                        lineBuf = buf;
                    }
                    //flip the preceding backslash flag
                    if (c == '\\')
                    {
                        precedingBackslash = !precedingBackslash;
                    }
                    else
                    {
                        precedingBackslash = false;
                    }
                }
                else
                {
                    // reached EOL
                    if (isCommentLine || len == 0)
                    {
                        isCommentLine = false;
                        isNewLine = true;
                        skipWhiteSpace = true;
                        len = 0;
                        continue;
                    }
                    if (inOff >= inLimit)
                    {
                        if (precedingBackslash)
                        {
                            len--;
                        }
                        return len;
                    }
                    if (precedingBackslash)
                    {
                        len -= 1;
                        //skip the leading whitespace characters in following line
                        skipWhiteSpace = true;
                        appendedLineBegin = true;
                        precedingBackslash = false;
                        if (c == '\r')
                        {
                            skipLF = true;
                        }
                    }
                    else
                    {
                        return len;
                    }
                }
            }
        }
    }
}

