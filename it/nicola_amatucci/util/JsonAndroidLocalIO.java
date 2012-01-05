/*    
    Copyright (C) 2011  Nicola Amatucci

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    NOTE: read README first
*/

package it.nicola_amatucci.util;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JsonAndroidLocalIO {
	public static <T> T loadData(Context context, String filename, Class<T> obj)
	{
		StringBuilder strContent = new StringBuilder("");
	    try {
	    	BufferedInputStream in = new BufferedInputStream(context.openFileInput(filename));
	        
	        int ch;
	        while( (ch = in.read()) != -1)
	            strContent.append((char)ch);
	        
	        in.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    if (strContent.length() > 0)
	    {
	    	try {
	    		Log.i("TAG",strContent.toString());
				JSONObject json = new JSONObject(strContent.toString());
				return Json.object_from_json(json, obj);				
			} catch (Exception e) {
				e.printStackTrace();			
			}
	    }

	    return null;
	}
	
	public static <T> boolean saveData(Context context, String filename, T o, Class<T> obj)
	{
		String document = null;
	
		try {
			document = Json.json_from_object(o, obj).toString();
			
			if (document != null)
			{
				Log.i("TAG",document);
		        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
		        fos.write(document.getBytes());
		        fos.close();
		        return true;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
