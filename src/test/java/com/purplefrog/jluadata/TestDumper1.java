package com.purplefrog.jluadata;

import junit.framework.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 3/26/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDumper1
    extends TestCase
{

    public void test1()
    {
        Object i = 7;
        assertEquals("7", LuaDumper.dumpAsLua(i));

        Object f = 4.3;
        assertEquals("4.3", LuaDumper.dumpAsLua(f));

        Object bacon = "bacon";
        assertEquals("\"bacon\"", LuaDumper.dumpAsLua(bacon));
    }

    public void test2()
    {

        Object m = sampleMap1();

        assertEquals("{\n" +
            "    [\"bacon\"] = \"bacon\",\n" +
            "    [\"f\"] = 4.3,\n" +
            "    [\"i\"] = 7,\n" +
            "}", LuaDumper.dumpAsLua(m));
    }

    public static Map<String, Object> sampleMap1()
    {
        Map<String,Object> m1 = new TreeMap<String, Object>();
        m1.put("i", 7);
        m1.put("f", 4.3);
        m1.put("bacon", "bacon");
        return m1;
    }

    public static Map<String, Object> sampleMap2()
    {
        Map<String,Object> m1 = new HashMap<String, Object>();
        m1.put("hail", "bob");
        return m1;
    }

    public void test3()
    {
        assertEquals("\"0123456789\\\\\\\"abcz\"", LuaDumper.quotedString("0123456789\\\"abcz"));

        assertEquals("\"hail\\nbob\\n\"", LuaDumper.quotedString("hail\nbob\n"));
    }

    public void test4()
    {
        List l1 = Arrays.asList(4.3, 7, "bacon", sampleMap2());

        Object l = l1;

        assertEquals("{\n" +
            "    4.3, -- [1]\n" +
            "    7, -- [2]\n" +
            "    \"bacon\", -- [3]\n" +
            "    {\n" +
            "        [\"hail\"] = \"bob\",\n" +
            "    }, -- [4]\n" +
            "}", LuaDumper.dumpAsLua(l));
    }

    public void test5()
        throws IOException, ParseException
    {
        String lua = "Alex_Polansky = 1337";

        LuaParser parser = new LuaParser(new StringReader(lua));
        Map<String, Object> a = parser.parseDictionary();
        assertEquals(1337, a.get("Alex_Polansky"));
    }

}
