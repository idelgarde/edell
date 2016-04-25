
/*
This software is part of the NxtJLib library.
It is Open Source Free Software, so you may
- run the code for any purpose
- study how the code works and adapt it to your needs
- integrate all or parts of the code in your own programs
- redistribute copies of the code
- improve the code and release your improvements to the public
However the use of the code is entirely your responsibility.
 */

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.awt.event.*;

/**
 * Class to read properties from the NxtJLib property file.
 * Many library options are defined in a property file 'nxtjlib.properties' that
 * may be modified as needed. 
 */ 
public class KinectPropriedades extends Properties
{
	private String propFile = "KinectGig.properties";
	private String userDir = System.getProperty("user.dir");
	private String userHome = System.getProperty("user.home");
	private String fileSeparator = System.getProperty("file.separator");
	private String propertiesResourcePath = "properties/KinectGig.properties";
	private int propLocation;

	/**
	 * Searches the property file.
	 * The property file is searched in the following order:<br>
	 * - Application directory (user.dir)<br>
	 * - Home directory (user.home)<br>
	 * - KinectGid.jar (distribution)<br><br> 
	 * As soon as the property file is found, the search is canceled. This allows
	 * to use a personalized property file without deleting or modifying the distributed
	 * file in KinectGig.jar. Be careful to keep the original formatting.
	 */
	public KinectPropriedades()
	{
		InputStream is =  null;
		// First search in current dir  
		File pFile = new File(userDir + fileSeparator + propFile);
		if (!pFile.isFile())
		{
			// Search in user's home dir  
			pFile = new File(userHome + fileSeparator + propFile);
			if (!pFile.isFile())  // Search in distributed properties file
			{
				is = getClass().getClassLoader().getResourceAsStream(propertiesResourcePath);
				if (is == null)
					KinectArduino.exibirErro("Could not find property file\n'" + propFile + "'");
				else
					propLocation = 3;
			}
			else
				propLocation = 2;
		}
		else
			propLocation = 1;

		try
		{
			if (is == null)
				is = new FileInputStream(pFile);
			load(is);
		}
		catch (IOException ex)
		{
			KinectArduino.exibirErro("Could not read property file '" + propFile + "'");
		}
	}

	/**
	 * Returns the location of the property file.
	 * @return the path of the property file found by running the constructor.
	 */
	public String getResourceInfo()
	{
		String s = "";
		switch (propLocation)
		{
		case 1:
			s = userDir;
			break;
		case 2:
			s = userHome;
			break;
		case 3:
			s = "nxtjlib.jar";
			break;
		default:
			s = "unknown";
		break;
		}     
		return "\nProperties from 'nxtjlib.properties' in\n" + s;
	}

	/**
	 * Gets the property value with the given key as string.
	 * An error dialog is shown, if the property does not exist.
	 * @param key the key of the property
	 * @return the value of the property or empty string, if the property is not found
	 */
	public String getStringValue(String key)
	{
		String value = getProperty(key);
		if (value == null)
		{
			KinectArduino.exibirErro("Property '" + key +
					"'\nnot found in file '" + propFile + "'");
			return "";
		}
		return value;
	}

	/**
	 * Gets the property value with the given key as int.
	 * An error dialog is shown, if the property does not exist or has bad format.
	 * @param key the key of the property
	 * @return the value of the property or 0, 
	 * if the property is not found or does not have integer format
	 */
	public int getIntValue(String key)
	{
		String value = getProperty(key);
		if (value == null){
			KinectArduino.exibirErro("Property '" + key +
					"'\nnot found in file '" + propFile + "'");
			return 0;
		}
		int intValue;
		try	{
			intValue = Integer.parseInt(value);
		}
		catch (NumberFormatException ex){
			KinectArduino.exibirErro("Illegal integer format of property '" + key +
					"'\nin file '" + propFile + "'");
			return 0;
		}
		return intValue;
	}

	/**
	 * Gets the property value with the given key as double.
	 * An error dialog is shown, if the property does not exist or has bad format.
	 * @param key the key of the property
	 * @return the value of the property or 0, 
	 * if the property is not found or does not have double format
	 */
	public double getDoubleValue(String key)
	{
		String value = getProperty(key);
		if (value == null){
			KinectArduino.exibirErro("Property '" + key +
					"'\nnot found in file '" + propFile + "'");
			return 0;
		}
		double doubleValue;
		try{
			doubleValue = Double.parseDouble(value);
		}
		catch (NumberFormatException ex){
			KinectArduino.exibirErro("Illegal double format of property '" + key +
					"'\nin file '" + propFile + "'");
			return 0;
		}
		return doubleValue;
	}

	// Get keyCode by reflection
	public int getKeyCode(String keyCodeStr)
	{
		Class clazz = KeyEvent.class;
		int keyCode = 0;
		try	{
			Field field = clazz.getField(keyCodeStr);
			Object value = field.get(new Object());
			keyCode = (Integer)value;
		}
		catch (Exception ex){
			KinectArduino.exibirErro("Illegal key code " + keyCodeStr + 
					"'\nin file '" + propFile + "'");
		}
		return keyCode;
	}
}
