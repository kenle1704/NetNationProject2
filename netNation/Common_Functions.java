package netNation;
import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import java.sql.*;
public class Common_Functions {
	private static String logPath="log/Common_Functions.log";
	private static String className="Common_Functions";
	public Common_Functions(String logPath,String className){
		this.logPath=logPath;
		this.className=className;
	}
	public static List<HashMap<String,String>> mapTable(HashMap<String,String> mapHeaders,HashMap<String,HashMap<String,String>> mapValues,HashMap<String,HashMap<String,String>> mapUsages,List<HashMap<String,String>> input ){
		List<HashMap<String,String>> output = new ArrayList<>();
		for( HashMap<String,String> row : input ) {
			HashMap<String,String> mapped = new HashMap<String,String>();
			for( String key : row.keySet() ) {
				String value = row.get(key);
				if( mapValues.get(key) != null ) {
					HashMap<String,String> mapValue = mapValues.get(key);
					if( mapValue.get(value) != null ) value = mapValue.get(value);
				}
				if( mapUsages.get(key) != null ) {
					HashMap<String,String> unitReduction = mapUsages.get(key);
					for(String _key : unitReduction.keySet() ){
						String[] idValues = _key.split("-");
						String id = idValues[0];
						String val = idValues[1];
						if( val == row.get(id) ) {
							value = "" + ( Integer.parseInt(value) / Integer.parseInt(unitReduction.get(_key)));
						}
					}
				}
				if( mapHeaders.get(key) != null ) {
					mapped.put(mapHeaders.get(key),value);
				}
			}
			if( mapped.size() > 0 ) output.add(mapped);
		}
		return output;

	}
    	public static List<String> read_csv_file(String filepath){
                List<String> csv = new ArrayList<>();
                Scanner sc;
                try {
                        sc = new Scanner(new File(filepath));
                        while (sc.hasNext())  //returns a boolean value
                        {
                                csv.add(sc.next());
                        }
                        sc.close();  //closes the scanner
                } catch (FileNotFoundException e ) {
                        e.printStackTrace();
                }
                return csv;
        }
        // assume that json file only have one object
        // we can use generic type depend on the input data
        public static HashMap<String,String> read_json_file(String filepath){
                HashMap<String,String> map = new HashMap<String,String>();
                try{
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
                        Gson gson = new Gson();
                        Object json = gson.fromJson(bufferedReader, Object.class);
                        map = new Gson().fromJson(
                                json.toString(), new TypeToken<HashMap<String, String>>() {}.getType()
                        );
                } catch (FileNotFoundException e ) {
                        e.printStackTrace();
                }
                return map;
        }
	public static List<HashMap<String,String>> filterContent(List<String> input,HashMap<String,String[]> conds){
                List<HashMap<String,String>> output = new ArrayList<>();
                List<String> loggers=new ArrayList<>();
                String firstLine = (  input.size() > 0 ) ? input.get(0) : ""; // get header list
                String [] header = firstLine.split(",");
                for(int i =1;i< input.size();i++)  //returns a boolean value
                {
                        String line = input.get(i);
                        String [] values = line.split(",");
                        HashMap<String,String> mapColumns = new HashMap<String,String>();
                        Boolean isSkip = false;
                        for( int j =0; j < header.length;j++){
				if( j >= values.length ) break;
				String[] cond = conds.get(header[j].toLowerCase());
				String value = values[j];
				if( cond != null && cond.length > 0 ) {
                                               //TODO
                                         String operator = cond[0];
                                         String val = cond[1];
                                         String reason = cond[2];
                                         switch (operator ) {
                                                case "<":
                                                      if( Integer.parseInt(values[j]) < Integer.parseInt(val) ) isSkip = true;
                                                      break;
                                                case ">":
                                                      if( Integer.parseInt(values[j]) > Integer.parseInt(val) ) isSkip = true;
                                                      break;
                                                case "==":
                                                      if( values[j].equals(val) ) isSkip = true;
                                                      break;
                                                case "contains":
                                                      String[] skipList = val.split(",");
                                                      List<String> _skipList = Arrays.asList(skipList);
                                                      if( _skipList.contains(values[j]) ) isSkip = true;
                                                      break;
						case "maxlength":
			   			      value = value.substring(0,Math.min(value.length(),Integer.parseInt(val)));
						      break;
                                                default:
                                                      break;
                                         }
                                         if(isSkip) {
                                               line = reason + line;
					}
				}
				mapColumns.put(header[j].toLowerCase(),value);

                        }
                        if ( isSkip ) loggers.add(line+"\n");
                        else if( mapColumns.size() > 0 ) output.add(mapColumns);
			else System.out.println(line);
                }
                if( loggers.size() > 0 ) logger(loggers.toString());
                return output;
        }
	public static void logger(String log){
		Logger logger = Logger.getLogger(className);
		FileHandler fh;
		try {
			fh = new FileHandler(logPath,true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.info(log);

		} catch(IOException e) {
			e.printStackTrace();
		}
	
	}
}
