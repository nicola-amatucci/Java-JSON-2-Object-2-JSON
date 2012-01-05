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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

public class Json
{
	private static final String DATA_FORMAT_0 = "dd-MM-yyyy";
	private static final String DATA_FORMAT_1 = "dd-MM-yyyy HH:mm:ss";
	private static final String DATA_FORMAT_2 = "yyyy-MM-dd";
	private static final String DATA_FORMAT_3 = "yyyy-MM-dd HH:mm:ss";

	private static String DATA_FORMAT = DATA_FORMAT_2;
	public static void set_date_format(String s)
	{
		Json.DATA_FORMAT = s;
	}
	
	public static <T> T object_from_json(JSONObject json, Class<T> objClass) throws Exception {
		T t = null;
		Object o = null;
		
		try {
			//create new object instance
			t = objClass.newInstance();

			//object fields
			Field[] fields = objClass.getFields();

			for (Field field : fields)
			{
				//field name
				o = json.get(field.getName());

				if (o.equals(null))
					continue;

				//field value
				try {
					String typeName = field.getType().getSimpleName();
					
					if (typeName.equals("String"))
					{ 
						o = json.getString(field.getName()); //String
					}
					else if (typeName.equals("boolean"))
					{
						o = Integer.valueOf(json.getInt(field.getName())); //boolean
					}					
					else if (typeName.equals("int"))
					{
						o = Integer.valueOf(json.getInt(field.getName())); //int
					}
					else if (typeName.equals("long"))
					{
						o = Long.valueOf(json.getLong(field.getName())); //long						
					}
					else if (typeName.equals("double"))
					{
						o = Double.valueOf(json.getDouble(field.getName())); //double
					}
					else if (typeName.equals("Date"))
					{
						o = new SimpleDateFormat(Json.DATA_FORMAT).parse(o.toString()); //data
					}
					else if (field.getType().isArray())
					{
						JSONArray arrayJSON = new JSONArray(o.toString());
						T[] arrayOfT = (T[]) null;
					
						try
						{
							//create object array
							Class c = Class.forName(field.getType().getName()).getComponentType();
							arrayOfT = (T[]) Array.newInstance(c, arrayJSON.length());
							
							//parse objects						
							for (int i = 0; i < json.length(); i++)
								arrayOfT[i] = (T) object_from_json(arrayJSON.getJSONObject(i), c);
						}
						catch (Exception e) {
							throw e;
						}
						
						o = arrayOfT;
					}
					else {
						o = object_from_json(new JSONObject(o.toString()), field.getType()); //object
					}
					
				} catch (Exception e) {
					throw e;
				}

				t.getClass().getField(field.getName()).set(t, o);
			}

		} catch (Exception e) {
			throw e;
		}

		return t;
	}

	public static <T> JSONObject json_from_object(Object o, Class<T> objClass) throws Exception
	{
		JSONObject json = new JSONObject();
		Field[] fields = objClass.getFields();
		try {
			for (Field field : fields)
			{
				//valore del campo
				try {
					String typeName = field.getType().getSimpleName();					
					
					//in base al tipo richiamo l'opportuna funzione
					if (typeName.equals("String")
						|| typeName.equals("boolean")
						|| typeName.equals("int")
						|| typeName.equals("long")
						|| typeName.equals("double")
						)
					{ 
						json.put(field.getName(), objClass.getField(field.getName()).get(o));
					}
					else if (typeName.equals("Date"))
					{						
						json.put(field.getName(), new SimpleDateFormat(Json.DATA_FORMAT).format((java.util.Date)objClass.getField(field.getName()).get(o)) );						
					}
					else if (field.getType().isArray())
					{
						try
						{
							JSONArray array = new JSONArray();
							
							Class c = Class.forName(field.getType().getName()).getComponentType();							
							Object[] objArray = (Object[])objClass.getField(field.getName()).get(o);
							for (int i = 0; i < objArray.length; i++)
							{
								array.put(json_from_object(objArray[i], c));
							}
							json.put(field.getName(), array);
						}
						catch (Exception e) {
							throw e;
						}
					}
					else {
						JSONObject jsonObj = json_from_object(objClass.getField(field.getName()).get(o), field.getType() );
						json.put(field.getName(), jsonObj);
					}
					
				} catch (Exception e) {
					throw e;
				}
				
				
			}
		} catch (Exception e) {
			throw e;
		}
		
		return json;
	}
}
