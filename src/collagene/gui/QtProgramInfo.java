package collagene.gui;

import java.io.InputStream;
import java.util.Scanner;

import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QDesktopServices;


/**
 * 
 * Information about the software
 * 
 * @author Johan Henriksson
 *
 */
public class QtProgramInfo
	{

	public static String programName="Collagene";

	
	private static String version;
	private static String timestamp;
	
	static
	{
	InputStream is=QtProgramInfo.class.getResourceAsStream("timestamp.txt");
  if(is!=null)
    {
    Scanner scanner = new Scanner(is);
    timestamp=scanner.nextLine();
    scanner.close();
    }
  else
    timestamp="0";

  Scanner scanner = new Scanner(QtProgramInfo.class.getResourceAsStream("version.txt"));
  version=scanner.nextLine()+"."+timestamp;
  scanner.close();
	}
	

	/**
	 * Get the version of the software as a human readable string
	 */
	public static String getVersionString()
		{
		return version;
//		return ""+versionMajor+"."+versionMinor+"."+versionRelease;
		}

	

	private static String qtexlicense=
		"Qt example code, Copyright (C) 2010 Nokia Corporation and/or its subsidiary(-ies). All rights reserved. Contact: Nokia Corporation (qt-info@nokia.com)\n"+
		"Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n"+
		 " * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n"+
		 " * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n"+
		 " * Neither the name of Nokia Corporation and its Subsidiary(-ies) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n"+
		 "\n"+
		 "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";

	
	public static final String licenseText=
		"This software contains portions derived from other projects, under respective licenses:\n\n" + 
//		ClientInfo.clientLicenseText+
		"\n\n----------------------------------------\n\n"+
		qtexlicense;


	public static void openHelpURL(String topic)
		{
		if(topic==null)
			QDesktopServices.openUrl(new QUrl("http://www.collagene.org/"));//"+topic));
		else
			QDesktopServices.openUrl(new QUrl("http://www.collagene.org/manual.php?page="+topic));//"+topic));
		}
	
	}
