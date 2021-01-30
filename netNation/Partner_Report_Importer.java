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
public class Partner_Report_Importer {
	// define log information for logger
	private static String logPath="log/Partner_Report_Importer.log";
    private static String className="Partner_Report_Importer";
	private static final String ignorePartnerIDs="26392"; // comma delimited for ignore list 
	private static final DB_Connector connector = new DB_Connector();
	private static final Query_Handler qhandler = new Query_Handler();
	private static final Common_Functions common = new Common_Functions(logPath,className);
	private static final String csvFilePath ="resource/Sample_Report.csv";
	private static final String jsonFilePath ="resource/typemap.json";
	public static void main(String[] args) {
	    //assume that first args is input file name 
	    //second args is input map 
		HashMap<String,String[]> conds = new HashMap<String,String[]>();
		HashMap<String,HashMap<String,String>> mapValues = new HashMap<String,HashMap<String,String>>();
		HashMap<String,HashMap<String,String>> mapUsages = new HashMap<String,HashMap<String,String>>();
		HashMap<String,String> unitReduction = new HashMap<String,String>();
		String isVarcharValues = "A_product,A_partnerpurchasedplanid,A_plan,B_partnerpurchasedplanid,B_domain";
		List<String> input = common.read_csv_file(csvFilePath);
                HashMap<String,String> json = common.read_json_file(jsonFilePath);
		HashMap<String,String> mapHeadersCharable = new HashMap<>();
		HashMap<String,String> mapHeadersDomains = new HashMap<>();
		// condition to filter 
		conds.put("partnumber",new String[]{"==","","NO PartNumber ::: " });
		conds.put("itemcount",new String[]{"<","1","Item Count Negative ::: " });//non positive 
		conds.put("partnerid",new String[]{"contains",ignorePartnerIDs,"Item contains PartnerID ::: "});
		conds.put("accountguid",new String[]{"maxlength","32",""});
		// assign value for mapping from json file
		mapValues.put("partnerid",json); 
		//mapping table header for Charable table
		mapHeadersCharable.put("partnumber","A_product");
		mapHeadersCharable.put("partnerid","A_partnerid");
		mapHeadersCharable.put("accountguid","A_partnerpurchasedplanid");
		mapHeadersCharable.put("plan","A_plan");
		mapHeadersCharable.put("itemcount","A_usage"); // v 
		//mapping table header for Domains table 
		mapHeadersDomains.put("accountguid","B_partnerpurchasedplanid");
		mapHeadersDomains.put("domains","B_domain");
		//map unitreduction 
		unitReduction.put("partnerid-EA000001GB0O","1000");
		unitReduction.put("partnerid-PMQ00005GB0R","5000");
		unitReduction.put("partnerid-SSX006NR","1000");
		unitReduction.put("partnerid-SPQ00001MB0R","2000");
		mapUsages.put("itemcount",unitReduction);
		List<HashMap<String,String>> filteredDataChargeable = common.filterContent(input,conds);
		List<HashMap<String,String>> filteredDataDomains = common.filterContent(input,new HashMap<String,String[]>());
		List<HashMap<String,String>> mappedChargeable = common.mapTable(mapHeadersCharable,mapValues,mapUsages,filteredDataChargeable);
		List<HashMap<String,String>> mappedDomains = common.mapTable(mapHeadersDomains,mapValues,mapUsages,filteredDataDomains);
		// insert to A_chargeable
		Connection conn = connector.getConnection();
		// get total itemCount;
        int itemCountSum=0;
        for(int i=0;i<filteredDataChargeable.size();i++){
                HashMap<String,String> row = filteredDataChargeable.get(i);
                int ic = Integer.parseInt(row.get("itemcount"));
                itemCountSum++;
        }
        System.out.println("Total Item Count : " + itemCountSum);
		//create chargeable table query
		String queries="";
		for(int i=0; i<mappedChargeable.size();i++){
			HashMap<String,String> row = mappedChargeable.get(i);
			String query = qhandler.insertQuery("A_chargeable",row,isVarcharValues);
			System.out.println(query);
			queries += query;
		}
		HashMap<String,Integer> executed = new HashMap<String,Integer>();
		//create domains table query 
		for(int i=0; i<mappedDomains.size();i++){
                        HashMap<String,String> row = mappedDomains.get(i);
			String domain = row.get("B_domain");
			if( executed.get(domain) == null) {
				executed.put(domain,1);
				String query = qhandler.insertQuery("B_domains",row,isVarcharValues);
                        	System.out.println(query);
				queries += query;
			}
        }
		connector.executeQuery(queries);
		connector.closeConnection();
	}
}
