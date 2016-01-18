package com.purplefrog.jluadata;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 3/18/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class LuaParser
{
    private final PushbackReader r;

    public LuaParser(Reader r)
    {
        if (r instanceof PushbackReader) {
            this.r = (PushbackReader) r;
        } else {
            this.r = new PushbackReader(r);
        }

    }

    public Object parse()
        throws IOException, ParseException
    {
        while (true) {
            int ch_ = r.read();
            if (ch_<0)
                return null;

            char ch = (char) ch_;

            if (Character.isWhitespace(ch))
                continue;

            if ('-'==ch) {
                if (maybeDevourComment()) continue;
            }

            if (Character.isDigit(ch) || '-'==ch) {
                return parseNumber(ch);
            }

            if ('"' == ch)
                return parseRemainingQuotedString();

            if ('{' == ch)
                return parseRemainingTable();


            return parseRemainingUnquotedString(ch);
        }
    }

    private boolean maybeDevourComment()
        throws IOException
    {
        int c2 = r.read();
        if (c2<0) {
            // sigh
        } else if ('-' == c2) {
            eatRemainingComment();
            return true;
        } else {
            r.unread(c2);
        }
        return false;
    }

    private void eatRemainingComment()
        throws IOException
    {
        while (true) {
            int ch_ = r.read();
            if (ch_<0)
                return;

            char ch = (char) ch_;
            if (ch == '\n')
                return;
        }
    }

    private Object parseRemainingTable()
        throws IOException, ParseException
    {
        Map<String, Object> rval = new HashMap<String, Object>();

        int index=1;

        List<Object> rvalL = new ArrayList<Object>();

        // look for an assignment or close }
        for  (; ; index++) {


            while (true) {
                int ch_ = r.read();
                if (ch_<0)
                    throw new ParseException("unclosed table", -1);

                char ch = (char) ch_;

                if ('-' == ch) {
                    if (maybeDevourComment())
                        continue;
                }

                if ('[' == ch) {
                    Object key = parseRemainingIndex();
                    eatEquals();
                    Object value = parse();
                    if (key.equals(index) && null != rvalL) {
                        rvalL.add(value);
                    } else {
                        rvalL = null;
                    }

                    rval.put(key.toString(), value);

                    break;

                } else if ('{' == ch) {
                    Object val = parseRemainingTable();
                    appendBareValue(rval, index, rvalL, val);

                    break;

                } else if ('}' == ch) {
                    return either(rvalL, rval);

                } else if (Character.isWhitespace(ch)) {
                    // no big deal

                } else if ('-'==ch || Character.isDigit(ch)) {
                    Object val = parseNumber(ch);

                    appendBareValue(rval, index, rvalL, val);
                    break;

                } else if ('\"'==ch) {
                    Object val = parseRemainingQuotedString();

                    appendBareValue(rval, index, rvalL, val);
                    break;

                } else if (Character.isLetterOrDigit(ch)) {
                    Object val = parseRemainingUnquotedString(ch);

                    appendBareValue(rval, index, rvalL, val);
                    break;

                } else {
                    throw new ParseException("bogus character inside table : "+ch, errorOffset());
                }

            }

//            System.out.println(rval.size());

            // we got a key=value or value item above.

            while (true) {

                int ch_ = r.read();
                if (ch_<0)
                    throw new ParseException("unclosed table", -1);

                char ch = (char) ch_;

                if (Character.isWhitespace(ch)) {
                    // no big deal
                } else if (',' == ch) {
                    break; // we got a comma, next iteration
                } else if ('}' == ch) {
                    // ah, ending the table

                    return either(rvalL, rval);
                } else {
                    throw new ParseException("garbage between table entries : "+ch, errorOffset());
                }
            }
        }

    }

    private Object either(List<Object> listVersion, Map<String, Object> mapVersion)
    {
        if (null != listVersion)
            return listVersion;


        return mapVersion;
    }

    public static void appendBareValue(Map<String, Object> mapVersion, int index, List<Object> listVersion, Object valueToAppend)
    {
        if (listVersion != null) {
            listVersion.add(valueToAppend);
        }

        mapVersion.put(Integer.toString(index), valueToAppend);
    }

    private void eatEquals()
        throws IOException, ParseException
    {
        while (true) {
            int ch_ = r.read();
            if (ch_ < 0) {
                throw new EOFException("premature EOF while parsing table key");
            }

            char ch = (char) ch_;

            if ('=' == ch) {
                return;
            } else if (Character.isWhitespace(ch)) {
                continue;
            } else {
                throw new ParseException("missing = after table [key] : '"+ch+"'", errorOffset());
            }
        }

    }

    private Object parseRemainingIndex()
        throws IOException, ParseException
    {
        while (true) {
            int ch_ = r.read();
            if (ch_ < 0) {
                throw new EOFException("premature EOF while parsing table key");
            }

            char ch = (char) ch_;

            if (Character.isWhitespace(ch)) {
                continue;
            } else if ('\"' == ch) {
                String rval = parseRemainingQuotedString();
                demandClosingSquareBracket();
                return rval;
            } else if (Character.isDigit(ch)) {
                Number rval = parseNumber(ch);
                demandClosingSquareBracket();
                return rval;
            } else {
                String rval = parseRemainingUnquotedString(ch);
                demandClosingSquareBracket();
                return rval;
            }
        }
    }

    private String parseRemainingUnquotedString(char firstChar)
        throws IOException, ParseException
    {
        StringBuilder rval = new StringBuilder();
        rval.append(firstChar);

        while (true) {

            int ch_ = r.read();
            if (ch_ < 0) {
                break;
            }

            char ch = (char) ch_;
            if (Character.isLetterOrDigit(ch) || ch == '_') {
                rval.append(ch);
            } else if (Character.isWhitespace(ch)) {
                break;
            } else {
                r.unread(ch);
                break;
            }
        }
        return rval.toString();
    }

    private void demandClosingSquareBracket()
        throws IOException, ParseException
    {
        while (true) {
            int ch_ = r.read();
            if (ch_ < 0) {
                throw new EOFException("premature EOF while parsing table key");
            }

            char ch = (char) ch_;

            if (']' == ch) {
                return;
            } else if (Character.isWhitespace(ch)) {
                continue;
            } else {
                throw new ParseException("garbage after table [key] : '"+ch+"'", errorOffset());
            }
        }
    }

    private int errorOffset()
    {
        return -1;// XXX should give the library user something useful
    }

    private String parseRemainingQuotedString()
        throws IOException, ParseException
    {
        StringBuilder rval = new StringBuilder();

        while (true) {
            int ch_ = r.read();
            if (ch_<0)
                throw new EOFException("quoted string missing closing quote");

            char ch = (char) ch_;

            if (ch=='"')
                return rval.toString();

            if ('\\' == ch) {
                ch_ = r.read();

                if (ch_<0)
                    throw new EOFException("EOF after \\ , dangit");

                ch = (char) ch_;
                if (ch=='n') {
                    ch = '\n';
                } else if ('\\' == ch || '"' == ch) {
                    // no change
                } else {
                    throw new ParseException("bad character after \\ : "+ch, errorOffset());
                }
            }

            rval.append(ch);
        }
    }

    public Number parseNumber(char firstDigit)
        throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(firstDigit);

        while (true) {
            int ch_ = r.read();
            if (ch_<0)
                break;

            char ch = (char) ch_;
            if (Character.isDigit(ch)) {
                buffer.append(ch);
            }else

            if ('.'==ch) {
                buffer.append(ch);
                return parseRemainingDouble(buffer);
            } else {
                r.unread(ch);
                break;
            }
        }

        return Integer.parseInt(buffer.toString());
    }

    public Double parseRemainingDouble(StringBuilder buffer)
        throws IOException
    {
        while (true) {
            int ch_ = r.read();
            if (ch_<0)
                break;

            char ch = (char) ch_;
            if (Character.isDigit(ch)) {
                buffer.append(ch);
            } else {
                r.unread(ch);
                break;
            }
        }

        return Double.parseDouble(buffer.toString());
    }

    public Map<String, Object> parseDictionary()
        throws IOException, ParseException
    {
        HashMap<String, Object> rval = new HashMap<String, Object>();

        while (true) {

            int ch_ = r.read();
            if (ch_<0)
                break;

            char ch = (char) ch_;

            if (Character.isWhitespace(ch)) {
                continue;
            } else if (Character.isLetterOrDigit(ch)) {
                String key = parseRemainingUnquotedString(ch);
                eatEquals();
                Object value = parse();

                rval.put(key, value);
            } else {
                throw new ParseException("failed to find variable name in dictionary list", errorOffset());
            }
        }
        return rval;
    }

    private class CommentDestroyer
        extends Reader
    {
        char[] stash = new char[10];
        int stashTail=0;
        private final Reader base;

        public CommentDestroyer(Reader r)
        {
            this.base = r;
        }

        @Override
        public int read(char[] cbuf, int off, int len)
            throws IOException
        {
            if (len<1)
                return 0;

            int ch = read();
            if (ch<0)
                return -1;

            cbuf[off] = (char) ch;
            return 1;
        }

        @Override
        public int read()
            throws IOException
        {
            if (stashTail>0)
                return getFromStash();

            int rval = base.read();

            throw new RuntimeException("NYI");
        }

        private int getFromStash()
        {
            throw new RuntimeException("NYI");
        }

        @Override
        public void close()
            throws IOException
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
