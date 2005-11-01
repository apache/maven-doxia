# Copyright (c) 2000 Pixware. 
# This file is part of the Pixware APT tools.
# For conditions of distribution and use, see the accompanying legal.txt file.

#x
#
#Package help
#

package provide help 1.0.0
namespace eval ::help {
}

#x
#  {findHelp} returns the help document named <name> if this document 
#  is found or the empty string otherwise.
#
#  More precisely, for an HTML based help system, if the application 
#  is installed in directory <dir> (see {{appHome}}), findHelp first 
#  searches for a file called <dir>/docs/<name>/<name>.html
#  then for a file called <dir>/../docs/<name>/<name>.html.
#  If one of these two files exists, findHelp returns the URL of its
#  directory, or the empty string otherwise.
#
#  If findHelp returns a non empty string, it may used as the first
#  argument of {{openHelp}}.
#
#------
#% findHelp styledit
#file:///usr/local/ange/pixware/docs/styledit
#------
#
proc findHelp {name} {
}

#x
#=============================================================================
#
#~~x
#~~x
#~~x
#
#  {openHelp} first tries to request a running help browser to display
#  topic <topic> (or the beginning of chapter <chapter> if <topic> is not 
#  specified) of chapter <chapter> of help document <document>.
#  If no running help browser is found, openHelp starts a new one and tries
#  to make it display <topic>.
#
#  For an HTML based help system, <document>\ <chapter>\ <topic> corresponds
#  to <document>/<chapter>#<mangled_topic>.
#
#  <mangled_topic> is string <topic> where all characters different from
#  a-zA-Z0-9 are replaced by %<xx> (character '%' followed by two hexadecimal 
#  digits which represent the ISO\ 8859\ 1 code of the character to be 
#  replaced).
#  
#------
#% openHelp file:///usr/local/ange/pixware/docs/styledit styledit Introduction
#------
#
proc openHelp {document chapter {topic ""}} {
}

	  #x
	  #  {closeHelp} requests the help browser to exit if it 
	  #  has been started by {{openHelp}}.
          #
          #  List:
          #
          #      * Item 1.
          #
          #      * Item 2.
          #
          #      * Item 3.
          #
          #  ~~x
	  proc closeHelp {} {
	      for {set i 0} {i < 4} {incr i} {
		  if {$foo == "bar"} {
		      break;
		  }
	      }
	}


#x  Single line 1.

	#x  Single line 2.
	proc foo {arg1 {arg2 1} args} {
	}
