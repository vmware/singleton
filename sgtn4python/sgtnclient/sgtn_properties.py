# coding=utf-8
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

from collections import OrderedDict

from sgtn_py_base import pybase


MAX_LINE_BUFFER = 1024


class LineReader:

    def __init__(self, inCharBuf):
        self.lineBuf = [None] * MAX_LINE_BUFFER
        self.inLimit = 0
        self.inOff = 0

        self.inCharBuf = inCharBuf
        if self.inCharBuf:
            self.inLimit = len(self.inCharBuf)

    def read_line(self):
        length = 0
        c = 0

        skipWhiteSpace = True
        isCommentLine = False
        isNewLine = True
        appendedLineBegin = False
        precedingBackslash = False
        skipLF = False

        while True:
            if self.inOff >= self.inLimit:
                if length == 0 or isCommentLine:
                    return -1
                if precedingBackslash:
                    length -= 1
                return length

            #The line below is equivalent to calling a ISO8859-1 decoder.
            c = self.inCharBuf[self.inOff]
            self.inOff += 1

            if skipLF:
                skipLF = False
                if c == '\n':
                    continue

            if skipWhiteSpace:
                if c == ' ' or c == '\t' or c == '\f':
                    continue

                if not appendedLineBegin and (c == '\r' or c == '\n'):
                    continue

                skipWhiteSpace = False
                appendedLineBegin = False

            if isNewLine:
                isNewLine = False
                if c == '#' or c == '!':
                    isCommentLine = True
                    continue

            if c != '\n' and c != '\r':
                self.lineBuf[length] = c
                length += 1
                if length == len(self.lineBuf):
                    buf = [None] * length
                    self.lineBuf.extend(buf)

                #flip the preceding backslash flag
                if c == '\\':
                    precedingBackslash = not precedingBackslash
                else:
                    precedingBackslash = False
            else:
                #reached end of line
                if isCommentLine or length == 0:
                    isCommentLine = False
                    isNewLine = True
                    skipWhiteSpace = True
                    length = 0
                    continue

                if self.inOff >= self.inLimit:
                    if precedingBackslash:
                        length -= 1
                    return length

                if precedingBackslash:
                    length -= 1
                    #skip the leading whitespace characters in following line
                    skipWhiteSpace = True
                    appendedLineBegin = True
                    precedingBackslash = False
                    if c == '\r':
                        skipLF = True
                else:
                    return length


class Properties:

    def __init__(self):
        self.kvTable = None

    def parse(self, text):
        self.kvTable = OrderedDict()

        reader = LineReader(text)
        self.load(reader)
        return self.kvTable

    def put(self, key, value):
        oldValue = self.kvTable.get(key)
        self.kvTable[key] = value
        return oldValue

    def load(self, lr):
        convtBuf = [None] * MAX_LINE_BUFFER

        while True:
            limit = lr.read_line()
            if limit < 0:
                break

            c = 0
            keyLen = 0
            valueStart = limit
            hasSep = False

            precedingBackslash = False
            while True:
                if keyLen >= limit:
                    break

                c = lr.lineBuf[keyLen]
                #need check if escaped.
                if (c == '=' or c == ':') and not precedingBackslash:
                    valueStart = keyLen + 1
                    hasSep = True
                    break
                elif (c == ' ' or c == '\t' or c == '\f') and not precedingBackslash:
                    valueStart = keyLen + 1
                    break

                if c == '\\':
                    precedingBackslash = not precedingBackslash
                else:
                    precedingBackslash = False

                keyLen += 1

            while True:
                if valueStart >= limit:
                    break

                c = lr.lineBuf[valueStart]
                if c != ' ' and c != '\t' and c != '\f':
                    if not hasSep and (c == '=' or c == ':'):
                        hasSep = True
                    else:
                        break

                valueStart += 1

            key = self.load_convert(lr.lineBuf, 0, keyLen, convtBuf)
            value = self.load_convert(lr.lineBuf, valueStart, limit-valueStart, convtBuf)
            self.put(key, value)

    def load_convert(self, inText, off, length, convtBuf):
        if len(convtBuf) < length:
            newLen = length * 2
            convtBuf = [None] * newLen

        outText = convtBuf
        outLen = 0
        end = off + length

        while True:
            if off >= end:
                break

            aChar = inText[off]
            off += 1
            if aChar == '\\':
                aChar = inText[off]
                off += 1
                if aChar == 'u':
                    #Read the unicode after \u
                    value = 0
                    for i in range(4):
                        aChar = inText[off]
                        off += 1

                        if aChar >= '0' and aChar <= '9':
                            value = (value << 4) + ord(aChar) - ord('0')
                        elif aChar >= 'a' and aChar <= 'f':
                            value = (value << 4) + 10 + ord(aChar) - ord('a')
                        elif aChar >= 'A' and aChar <= 'F':
                            value = (value << 4) + 10 + ord(aChar) - ord('A')
                        else:
                            return None

                    outText[outLen] = pybase.int_to_unicode(value)
                    outLen += 1
                else:
                    if aChar == 't':
                        aChar = '\t'
                    elif aChar == 'r':
                        aChar = '\r'
                    elif aChar == 'n':
                        aChar = '\n'
                    elif aChar == 'f':
                        aChar = '\f'

                    outText[outLen] = aChar
                    outLen += 1
            else:
                outText[outLen] = aChar
                outLen += 1

        return ''.join(outText[:outLen])
