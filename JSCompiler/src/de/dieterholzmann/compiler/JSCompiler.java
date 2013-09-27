package de.dieterholzmann.compiler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;

/**
 * 
 * @author Dieter Holzmann Diese Klasse durchsucht einen im "--path" angegebenen
 *         Ordner nach JavaScript Dateien und komprimiert diese mit dem
 *         "google closure compiler" zu einer Datei.
 */
public class JSCompiler {

	/**
	 * @param args
	 *            - path, out_path, r
	 */
	public static void main(String[] args) {
		if (args.length <= 0)
			System.err
					.println("Usage: java -jar jscompiler [--r] --path /path/to/js/folder [[--out_path] /path/to/out/folder]");

		for (int i = 0, l = args.length; i < l; i++) {
			if (args[i].startsWith("--")) {
				if (!(args[i + 1].startsWith("--")))
					argsList.put(args[i].substring(2, args[i].length()),
							args[i + 1]);
			}
		}

		try {
			if (argsList.get("path") == null)
				throw new IllegalArgumentException(
						"Not a valid argument path, please select a folder");

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}

		if (argsList.get("out_path") == null)
			argsList.put("out_path", "");

		// @TODO recursive arg
		if (argsList.get("--r") != null)
			recursive = true;

		new JSCompiler();

	}

	public JSCompiler() {
		init();
	}

	private void init() {
		FileFilter filefilter = new FileFilter() {
			public boolean accept(File file) {
				// if the file extension is "js" return true, else false
				if (file.getName().endsWith(".js") || file.isDirectory()) {
					return true;
				}
				return false;
			}
		};

		findJSFiles(argsList.get("path"), filefilter);
	}

	private void findJSFiles(String path, FileFilter filter) {
		System.out.println();
		File c = new File(path);
		File[] files = c.listFiles(filter);

		ArrayList<JSSourceFile> primaryJavascriptFiles = new ArrayList<JSSourceFile>();

		ArrayList<JSSourceFile> externalJavascriptFiles = new ArrayList<JSSourceFile>();

		for (File f : files) {
			if (f.isDirectory()) {
				if (recursive) {
					findJSFiles(f.getPath(), filter);
				}
			} else {
				String name = f.getName();

				if (name.indexOf("mini") > 0 || name.indexOf("min") > 0) {
				} else {
					primaryJavascriptFiles.add(JSSourceFile.fromFile(f));
				}
			}
		}
		compileFiles(externalJavascriptFiles, primaryJavascriptFiles);
	}

	private void compileFiles(ArrayList<JSSourceFile> externalJavascriptFiles,
			ArrayList<JSSourceFile> primaryJavascriptFiles) {

		if (externalJavascriptFiles.size() > 0
				|| primaryJavascriptFiles.size() > 0) {
			Compiler compiler = new Compiler();
			CompilerOptions options = new CompilerOptions();
			compiler.compile(externalJavascriptFiles, primaryJavascriptFiles,
					options);

			for (JSError message : compiler.getWarnings()) {
				System.err.println("Warning message: " + message.toString());
			}

			for (JSError message : compiler.getErrors()) {
				System.err.println("Error message: " + message.toString());
			}

			FileWriter outputFile;
			try {
				outputFile = new FileWriter(argsList.get("out_path")
						+ "out.mini.js");
				outputFile.write(compiler.toSource());
				outputFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Boolean recursive = false;

	private static HashMap<String, String> argsList = new HashMap<String, String>();
}
