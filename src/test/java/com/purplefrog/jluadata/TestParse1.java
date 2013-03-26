package com.purplefrog.jluadata;

import junit.framework.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 3/18/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestParse1
    extends TestCase
{
    public void test1()
        throws IOException, ParseException
    {
        trivialParseTest("1.0", 1.0);
        trivialParseTest("7", 7);
        trivialParseTest("42", 42);
        trivialParseTest("-95", -95);

        trivialParseTest("77.", 77.0);
        trivialParseTest("77.34", 77.34);
        trivialParseTest("-0.77", -0.77);

        trivialParseTest("\"bacon\"", "bacon");
        trivialParseTest("\"quotes \\\" \\\\ \"", "quotes \" \\ ");
    }

    public static void trivialParseTest(String unparsedString, Object expectedValue)
        throws IOException, ParseException
    {
        Object o = luaParse(unparsedString);

        assertEquals(expectedValue, o);
    }

    private static Object luaParse(String unparsedString)
        throws IOException, ParseException
    {
        Reader r = new StringReader(unparsedString);

        PushbackReader r2 = new PushbackReader(r);

        LuaParser p = new LuaParser(r2);

        return p.parse();
    }

    public void test2()
        throws IOException, ParseException
    {
        testMap("{ [name]=\"bob\", [ x]=77, [y ]= -6, [\"diagnosis\"]=wtf }", "name", "bob", "x", 77, "y", -6, "diagnosis", "wtf");
    }

    public static void testMap(String unparsed, Object... kvArray)
        throws IOException, ParseException
    {
        Reader r = new StringReader(unparsed);
        LuaParser p = new LuaParser(r);
        Object o = p.parse();
        assertTrue(o instanceof Map);

        Map<String,Object> m = (Map<String, Object>) o;

        assertEquals(kvArray.length, m.size()*2);

        for (int i=0; i<kvArray.length; i+=2) {
            String key = (String) kvArray[i];
            Object value = kvArray[i+1];

            Object v2 = m.get(key);
            String message = "t[" + key + "] != " + value;
            assertEquals(message, value, v2);
        }
    }

    public static void test3()
        throws IOException, ParseException
    {
        InputStream s = TestParse1.class.getResourceAsStream("ArbitraryCommands.lua");

        Reader r = new InputStreamReader(s);
        LuaParser p = new LuaParser(r);

        Map<String,Object> dict = p.parseDictionary();

        assertEquals(1, dict.size());
        assertEquals("ArbitCommDB", dict.keySet().iterator().next());
        Object x = dict.get("ArbitCommDB");
//        System.out.println(x);
    }

    public void test4()
        throws IOException, ParseException
    {
        testMap("{ { [name]=\"bob\" }, [2] = \"hero\", [\"pants\"] = 77.6 }",
            "1", quickMap("name", "bob"),
            "2", "hero",
            "pants", 77.6);
    }

    private Map<String, Object> quickMap(Object... kvArray)
    {
        HashMap<String, Object> rval = new HashMap<String, Object>();
        for (int i=0; i< kvArray.length; i += 2) {
            rval.put((String) kvArray[i], kvArray[i+1]);
        }
        return rval;
    }

    public void test5()
        throws IOException, ParseException
    {
        Object o = luaParse("{ 3.14159,-- [1]\n" +
            " 2.178, --[2]\n" +
            "  \"bacon\", --[3]\n" +
            "  argh, --[4]\n" +
            "\n}");

        assertEquals(
//            quickMap("1", 3.14159, "2", 2.178, "3", "bacon", "4", "argh")
            Arrays.asList(3.14159, 2.178, "bacon", "argh")
            ,
            o);

    }
}
