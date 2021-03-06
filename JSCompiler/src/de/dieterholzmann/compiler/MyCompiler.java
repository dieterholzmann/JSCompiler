package de.dieterholzmann.compiler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.WarningLevel;

public class MyCompiler {
	
	public MyCompiler(ArrayList<JSSourceFile> externalJavascriptFiles,
			ArrayList<JSSourceFile> primaryJavascriptFiles, HashMap<String, String> argsList) {
		init(externalJavascriptFiles, primaryJavascriptFiles, argsList);
	}

	private void init(ArrayList<JSSourceFile> externalJavascriptFiles,
			ArrayList<JSSourceFile> primaryJavascriptFiles, HashMap<String, String> argsList) {
		
		Compiler compiler = new Compiler();
		CompilerOptions options = new CompilerOptions();
		compiler.compile(externalJavascriptFiles, primaryJavascriptFiles,
				options);
		WarningLevel.DEFAULT.setOptionsForWarningLevel(options);
//		CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
//		WarningLevel.QUIET.setOptionsForWarningLevel(options);
		
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
