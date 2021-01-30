package netNation;
import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Query_Handler {
	// query builder
	// TableData is hashmap array that contains mapping between column header and value
	// isVarcharFilter is String that contain all columns that is varchar which will require single quote outside, date column will also require double quote in insert 
	public static String insertQuery(String tableName,HashMap<String,String> tableData,String isVarcharFilter){
                // insert data
                LinkedList<String> columnNames = new LinkedList<String>();
                LinkedList<String> columnValues = new LinkedList<String>();
                for (String columnName : tableData.keySet()) {
                        String columnValue = tableData.get(columnName);
                        if( columnValue != "" ) {
                                columnNames.add(columnName);
				if( isVarcharFilter.indexOf(columnName) > -1 ) columnValue = "'"+columnValue+"'";
                                columnValues.add(columnValue);

                        }
                }
                return "INSERT INTO " + tableName + "( " + String.join(", ", columnNames) + " ) VALUES (" + String.join(", ", columnValues) + " );";
        }
	// query for update
	// query for delete
	// query for select
}
