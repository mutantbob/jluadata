This git repository hosts java source code for a library that can parse text that is formatted as Lua data assignments and literals.

The original application was parsing variables from World Of Warcraft's WTF/Accounts/*/SavedVariables/ directory (where WoW Lua add-ons store their configurations and data).

As of March 2013 it is not heavily tested for use with all legal Lua syntax.  If you find a legal Lua syntax it can't handle I can work with you to add a unit test and fix it.

The two idioms for using this library are

    Map<String,Object> dictionary = new LuaParser(new StringReader(lua)).parseDictionary(); // file with x=y syntax
    Object o = new LuaParser(new StringReader(lua)).parse(); // file with a single object like { ["a"] = 7 }
  


This project is Copyright (c) Robert Forsman 2013

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


Project Founder:
Robert Forsman <jluadata@thoth.purplefrog.com>
