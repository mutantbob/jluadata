package com.purplefrog.jluadata;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 3/27/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestParse2
{
    public static void main(String[] argv)
        throws IOException, RecognitionException
    {

        InputStream s = TestParse1.class.getResourceAsStream("ArbitraryCommands.lua");

        CharStream cs = new ANTLRInputStream(s);


         CommonTokenStream tokenStream = new CommonTokenStream();

        luaLexer lexer = new luaLexer(cs);
        tokenStream.setTokenSource(lexer);
        luaParser x = new luaParser(tokenStream);

        luaParser.dictionary_return y = x.dictionary();

        y.getTree();

        CommonTree ast = (CommonTree) y.getTree();
        if (false) {
            int n = ast.getChildCount();
            Tree tree = ast.getChild(0);
            Tree tree1 = ast.getChild(1);
        }
        System.out.print(dumpTree(ast));
    }

    private static String dumpTree(Tree node)
    {
        StringBuilder children = new StringBuilder();
        for (int i=0; i<node.getChildCount(); i++) {
            children.append(dumpTree(node.getChild(i)));
        }

        return node.getText()+"\n" + LuaDumper.indent(" -  ", children);
    }
}
