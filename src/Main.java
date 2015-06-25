import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.search.GoogleSearch;
import com.google.search.entity.websearch.Result;

public class Main {
    public static void main(String[] args) throws Exception {
	File propertiesFile = new File("settings.txt");
	if (!propertiesFile.exists())
	    propertiesFile.createNewFile();
	Properties properties = new Properties();
	properties.load(new FileInputStream(propertiesFile));
	String browser = "";
	if ((browser = properties.getProperty("browser")) == null)
	    reselectBrowser(propertiesFile);

	// Get song name from clipboard
	String songName = (String) Toolkit.getDefaultToolkit()
		.getSystemClipboard().getData(DataFlavor.stringFlavor);
	System.out.println("Searching for lyrics of: " + songName);

	// Search google
	GoogleSearch googleSearch = new GoogleSearch();
	String searchingFor = songName + " - lyrics";

	List<Result> results = googleSearch.getResults(searchingFor);

	if (results.size() != 0) {
	    String urltext = results.get(0).getUrl();
	    System.out.println(urltext);

	    // Open first result in firefox
	    try {
		URL firstResultURL = new URL(urltext);
		String cmd = browser + " " + firstResultURL.toString();
		Runtime.getRuntime().exec(cmd);
		System.out.println(">>" + cmd);
	    } catch (Exception err) {
		reselectBrowser(propertiesFile);
	    }
	}
	// in.close();
    }

    static void reselectBrowser(File propertiesFile) throws Exception,
	    IOException {
	System.out.println("Select your browser");
	JFileChooser chooser = new JFileChooser();
	FileFilter exeFilter = new FileNameExtensionFilter("Applications",
		new String[] { "exe" });
	chooser.setFileFilter(exeFilter);
	if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    Properties p = new Properties();
	    p.put("browser", chooser.getSelectedFile().getAbsolutePath());
	    p.store(new FileOutputStream(propertiesFile), null);
	}
	System.out.println("Press enter to search again...");
	Scanner console = new Scanner(System.in);
	console.nextLine();
	main(null);
	console.close();
    }
}