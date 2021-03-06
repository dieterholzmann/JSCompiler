package de.dieterholzmann.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.javascript.jscomp.JSSourceFile;

public class HTTPJSCompiler {

	public HTTPJSCompiler(HashMap<String, String> argsList) {
		init(argsList);
	}

	private void init(HashMap<String, String> argsList) {
		ArrayList<String> files = findJSFiles(argsList);

		ArrayList<JSSourceFile> externalJavascriptFiles = new ArrayList<JSSourceFile>();
		ArrayList<JSSourceFile> primaryJavascriptFiles = new ArrayList<JSSourceFile>();

		String protocol = getProtocol(argsList.get("http"));

		for (String file : files) {
			BufferedReader br = null;
			String ln = null;
			String code = "";
			try {
				br = new BufferedReader(new InputStreamReader(
						new URL(file).openStream(), "UTF-8"));

				while ((ln = br.readLine()) != null) {
					code += ln + "\n";
				}
				br.close();

				// set protocol bei undefined protocol urls
				if (file.startsWith("//"))
					file = protocol + ":" + file;

				// check the base url for external files
				if (!file.startsWith(baseURL))
					externalJavascriptFiles.add(JSSourceFile.fromCode(file,
							code));
				else
					primaryJavascriptFiles.add(JSSourceFile
							.fromCode(file, code));

				// if (file.indexOf("mini") > 0 || file.indexOf("min") > 0) {
				// externalJavascriptFiles.add(JSSourceFile.fromCode(file,
				// code));
				// System.out.println("External: " + file);
				// } else {
				// System.out.println("Primary: " + file);
				// }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		new MyCompiler(externalJavascriptFiles, primaryJavascriptFiles,
				argsList);
	}

	private ArrayList<String> findJSFiles(HashMap<String, String> argsList) {
		ArrayList<String> files = new ArrayList<String>();
		URL url = null;
		try {
			url = new URL(argsList.get("http"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		BufferedReader in = null;
		String ln;
		String html = "";

		try {
			in = new BufferedReader(new InputStreamReader(url.openStream(),
					"UTF-8"));

			while ((ln = in.readLine()) != null) {
				html += ln;
				if (ln.indexOf("base") > 0)
					setBaseURL(ln);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Pattern p = Pattern.compile("src=\"(.*?)\"");
		Matcher m = p.matcher(html);

		String filePath = null;
		while (m.find()) {
			filePath = m.group(1);
			if (filePath.indexOf(".js") > 0) {

				if (!filePath.startsWith(baseURL)
						&& (!filePath.startsWith("http")
								&& !filePath.startsWith("www") && !filePath
									.startsWith("//"))) {

					files.add(baseURL + filePath);
				} else {
					files.add(filePath);
				}
			}
		}

		return files;
	}

	/**
	 * Set base url
	 * 
	 * @param str
	 *            - html quellcode
	 */
	private void setBaseURL(String str) {
		Pattern p = Pattern.compile("<base href=\"(.*?)\"");
		Matcher m = p.matcher(str);

		while (m.find()) {
			if (!m.group(1).equals(""))
				baseURL = m.group(1);
		}

		if (!baseURL.equals("") && baseURL.lastIndexOf("/") <= 0)
			baseURL += "/";
	}

	/**
	 * Get the protocol from url
	 * 
	 * @param str
	 *            - page url
	 * @return protocol as String (http/https)
	 */
	private String getProtocol(String str) {
		if (str != null && !str.equals("")) {
			if (str.startsWith("http"))
				return str.substring(0, str.indexOf("://"));
		}
		return "http";
	}

	private String baseURL = null;
}
