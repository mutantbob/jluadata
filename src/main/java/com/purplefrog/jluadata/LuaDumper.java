package com.purplefrog.jluadata;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 3/26/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class LuaDumper
{
    public static String dumpAsLua(Map<String, Object> db)
    {
        StringBuilder rval = new StringBuilder();
        for (Map.Entry<String, Object> en : db.entrySet()) {
            String key = en.getKey();
            Object value = en.getValue();
            rval.append( "[" + quotedString(key) + "] = " + dumpAsLua(value)+",\n");
        }

        return "{\n"+indent("    ", rval)+'}';
    }

    public static String dumpAsLua(List<Object> list)
    {
        StringBuilder rval = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            rval.append(dumpAsLua(value) + ", -- [" + (i+1) + "]\n");
        }
        return "{\n"+indent("    ", rval)+'}';
    }

    public static String dumpAsLua(Object val)
    {
        if (val instanceof Map) {
            Map map = (Map) val;
            return dumpAsLua(map);
        } else if (val instanceof Number) {
            return val.toString();
        } else if (val instanceof List) {
            return dumpAsLua( (List) val);
        } else if (val instanceof CharSequence) {
            return quotedString(val.toString());
        } else {
            throw new IllegalArgumentException("I don't know how to dump a "+val.getClass().getName()+" as Lua");
        }
    }

    public static String indent(CharSequence prefix, CharSequence text)
    {
        StringBuilder rval = new StringBuilder();
        boolean atLineStart = true;
        for (int i=0; i<text.length(); i++) {
            if (atLineStart) {
                rval.append(prefix);
                atLineStart = false;
            }
            char ch = text.charAt(i);
            rval.append(ch);
            if (ch=='\n') {
                atLineStart = true;
            }
        }
        return rval.toString();
    }

    public static String quotedString(CharSequence src)
    {
        StringBuilder rval = new StringBuilder();
        rval.append('"');
        for (int i=0; i<src.length(); i++) {
            char ch = src.charAt(i);
            if ('"' == ch || '\\' == ch) {
                rval.append('\\');
                rval.append(ch);
            } else if ('\n' == ch) {
                rval.append("\\n");
            } else {
                rval.append(ch);
            }
        }
        rval.append('"');

        return rval.toString();
    }

    public static String dumpAsLuaDict(Map<String, Object> dict)
    {
        StringBuilder rval = new StringBuilder();

        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            rval.append(entry.getKey()+" = "+dumpAsLua(entry.getValue())+"\n\n");
        }

        return rval.toString();
    }
}
