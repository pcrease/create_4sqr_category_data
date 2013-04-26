/*
 *This class requires the JSONsimple library as a dependency in order to 
 *parse the 4square category file 
 */

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Main_FourSquare {

	/**
	 * @param args
	 * @throws IOException
	 */

	private static int imageSize32 = 32;
	private static int imageSize44 = 44;
	private static int imageSize64 = 64;
	private static int imageSize88 = 88;

	private static final String HEADER_STRING = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static final String XML_OPEN_TAG_STRING = "<item>";
	private static final String XML_CLOSE_TAG_STRING = "</item>";
	private static final String XML_OPEN_TAG_RESOURCE_STRING = "<resources>";
	private static final String XML_CLOSE_TAG_RESOURCE_STRING = "</resources>";
	private static final String XML_OPEN_TAG_ARRAY_STRING = "<array name=\"";
	private static final String XML_CLOSE_TAG_ARRAY_STRING = "</array>";

	private static String pathToImageFolder = "C:/Temp/4sqicons/";
	private static final String pathToListFolder = "C:/Temp/Lists/";

	private static ArrayList<String> idList = new ArrayList<String>();
	private static ArrayList<String> nameList = new ArrayList<String>();
	private static ArrayList<String> pluralNameList = new ArrayList<String>();
	private static ArrayList<String> hierachicalPlaceList = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException {

		URL url = new URL("https://api.foursquare.com/v2/venues/categories?" +
				"oauth_token=MWHPOPBR311OAXVJY4SO4B3OBR1L34EWGFMOZVXSBPCK24ZA&v=20130425");

		// open the stream and put it into BufferedReader
		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));

		String inputLine;
		StringBuilder builder = new StringBuilder();

		while ((inputLine = in.readLine()) != null)
			builder.append(inputLine);

		JSONObject rootObject = (JSONObject) JSONValue
				.parse(builder.toString());
		JSONObject dataObject = (JSONObject) rootObject.get("response");

		JSONArray categories = (JSONArray) dataObject.get("categories");
		parseCategoriesJSONdata(categories, 1, false, true);

		// System.out.println(rootObject.toJSONString());

		in.close();

	}

	private static void parseCategoriesJSONdata(JSONArray categories,
			int hierachy, boolean outputImageFiles, boolean outputAndroidLists)
			throws IOException {

		

		for (int i = 0; i < categories.size(); i++) {
			JSONObject category = (JSONObject) categories.get(i);

			System.out.println(category.get("name") + " " + hierachy);
			System.out.println(category.get("id"));
			System.out.println(category.get("pluralName"));
			
			String name = (String) category.get("name");
			name=name.replace("&", "and");
			name=name.replace("'", "");
			
			String pluralName = (String) category.get("pluralName");
			pluralName=pluralName.replace("&", "and");
			pluralName=pluralName.replace("'", "");
			
			idList.add((String) category.get("id"));
			nameList.add(name);
			pluralNameList.add(pluralName);
			hierachicalPlaceList.add(""+hierachy);
			
			JSONObject IconCategory = (JSONObject) category.get("icon");

			if (outputImageFiles == true)
				getIconFiles(IconCategory, (String) category.get("id"));

			if (category.get("categories") != null) {
				parseCategoriesJSONdata((JSONArray) category.get("categories"),
						hierachy + 1, outputImageFiles, outputAndroidLists);
			}
		}
		
		if (outputAndroidLists == true){
		writeOutItemLists(idList.toArray(new String[idList.size()]),"sqrCategoryIds");
		writeOutItemLists(nameList.toArray(new String[nameList.size()]),"sqrCategoryNames");
		writeOutItemLists(pluralNameList.toArray(new String[pluralNameList.size()]),"sqrCategoryPluralNames");
		writeOutItemLists(hierachicalPlaceList.toArray(new String[hierachicalPlaceList.size()]),"sqrHierarichalPlace");
		}

	}

	private static void writeOutItemLists(String[] listItems, String resourceName)
			throws IOException {

		PrintWriter out = new PrintWriter(new FileWriter(pathToListFolder
				+ resourceName + ".xml"));
		// output to the file a line
		out.println(HEADER_STRING);
		out.println(XML_OPEN_TAG_RESOURCE_STRING);
		out.println(XML_OPEN_TAG_ARRAY_STRING + resourceName + "\">");

		for (int i = 0; i < listItems.length; i++) {
			out.println(XML_OPEN_TAG_STRING + listItems[i]
					+ XML_CLOSE_TAG_STRING);
		}
		out.println(XML_CLOSE_TAG_ARRAY_STRING);
		out.println(XML_CLOSE_TAG_RESOURCE_STRING);
		out.close();
	}

	private static void getIconFiles(JSONObject IconCategory, String id)
			throws IOException {

		String prefix = (String) IconCategory.get("prefix");
		prefix = prefix.replace("_v2", "");

		URL url = null;
		try {
			url = new URL(prefix + imageSize32 + IconCategory.get("suffix"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(url.toString());

		BufferedImage icon32 = null;
		try {
			icon32 = ImageIO.read(url);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File outputfile32 = new File(pathToImageFolder + id + "_" + imageSize32
				+ ".png");
		try {
			if (icon32 != null)
				ImageIO.write(icon32, "png", outputfile32);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			url = new URL(prefix + imageSize44 + IconCategory.get("suffix"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage icon44 = null;
		try {
			icon44 = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File outputfile44 = new File(pathToImageFolder + id + "_" + imageSize44
				+ ".png");
		try {
			if (icon44 != null)
				ImageIO.write(icon44, "png", outputfile44);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			url = new URL(prefix + imageSize64 + IconCategory.get("suffix"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedImage icon64 = null;
		try {
			icon64 = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (icon64 != null) {
			File outputfile64 = new File(pathToImageFolder + id + "_"
					+ imageSize64 + ".png");
			ImageIO.write(icon64, "png", outputfile64);
		}

		try {
			url = new URL(prefix + imageSize88 + IconCategory.get("suffix"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage icon88 = null;
		try {
			icon88 = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (icon88 != null) {
			File outputfile88 = new File(pathToImageFolder + id + "_"
					+ imageSize88 + ".png");
			ImageIO.write(icon88, "png", outputfile88);
		}

	}

}
