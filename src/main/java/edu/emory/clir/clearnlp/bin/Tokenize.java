/**
 * Copyright 2014, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.bin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Tokenize
{
	@Option(name="-l", usage="language (default: english)", required=false, metaVar="<language>")
	private String s_language = TLanguage.ENGLISH.toString();
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String s_inputPath;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<regex>")
	private String s_inputExt = "*";
	@Option(name="-oe", usage="output file extension (default: tok)", required=false, metaVar="<string>")
	private String s_outputExt = "tok";
	@Option(name="-line", usage="if set, treat each line as one sentence", required=false, metaVar="<boolean>")
	private boolean b_line = false;
	
	public Tokenize() {}
	
	public Tokenize(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		try
		{
			AbstractTokenizer tokenizer = NLPUtils.getTokenizer(TLanguage.getType(s_language));
			
			for (String inputFile : FileUtils.getFileList(s_inputPath, s_inputExt, false))
			{
				System.out.println(inputFile);
				if (b_line) tokenizeLines(tokenizer, inputFile, inputFile+"."+s_outputExt);
				else		tokenize(tokenizer, inputFile, inputFile+"."+s_outputExt);
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public void tokenize(AbstractTokenizer tokenizer, String inputFile, String outputFile) throws IOException
	{
		InputStream in  = IOUtils.createFileInputStream(inputFile);
		PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
		
		for (List<String> tokens : tokenizer.segmentize(in))
			out.println(Joiner.join(tokens, StringConst.SPACE));
		
		in.close();
		out.close();
	}
	
	public void tokenizeLines(AbstractTokenizer tokenizer, String inputFile, String outputFile) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(inputFile);
		PrintStream out = IOUtils.createBufferedPrintStream(outputFile);
		String line;
		
		while ((line = reader.readLine()) != null)
			out.println(Joiner.join(tokenizer.tokenize(line), StringConst.SPACE));
		
		reader.close();
		out.close();
	}
	
	static public void main(String[] args)
	{
		new Tokenize(args);
	}
}
