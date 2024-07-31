import java.util.Set;
import java.util.List;
import java.net.URI;
import java.util.HashSet;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException; // 예외처리
import java.io.BufferedReader;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text; // 문자열 형식 키
import org.apache.hadoop.fs.Path; // 파일 경로 생성
import org.apache.hadoop.io.IntWritable; // 정수형 값
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.mapreduce.Job; // 맵리듀스 잡
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Mapper; // 매퍼
import org.apache.hadoop.mapreduce.Reducer; // 리듀서
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat; // 입력데이터
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat; // 분석결과

public class WordCount2 {

	public static class TokenMapper extends Mapper<Object, Text, Text, IntWritable>{ 
		static enum CountersEnum { INPUT_WORDS }

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		private boolean caseSensitive;
		private Set<String> patternsToSkip = new HashSet<String>();
		private Configuration cf;
		private BufferedReader fis;
		
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
	
			cf = context.getConfiguration();
			caseSensitive = cf.getBoolean("StringCounter.case.sensitive", true);

			if (cf.getBoolean("StringCounter.skip.patterns", false)) {

				URI[] patternsURIs = Job.getInstance(cf).getCacheFiles();

				for (URI patternsURI : patternsURIs) {
					Path patternsPath = new Path(patternsURI.getPath());
					String patternsFileName = patternsPath.getName().toString();
					parseSkipFile(patternsFileName);
				}
			}
		}

	
		private void parseSkipFile(String fileName) { 
			try { 
				fis = new BufferedReader(new FileReader(fileName)); 
				String pattern = null; 
				
				while ((pattern = fis.readLine()) != null) { 
					patternsToSkip.add(pattern); 
				}
		       	} catch (IOException ioe) { 
				System.err.println("Caught exception while parsing the cached file '" + StringUtils.stringifyException(ioe)); 
			} 
		}

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			String line = (caseSensitive) ? value.toString() : value.toString().toLowerCase();

			for (String pattern : patternsToSkip) {

				line = line.replaceAll(pattern, "");
			}

			StringTokenizer itr = new StringTokenizer(line);

			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(word, one);
				Counter counter = context.getCounter(CountersEnum.class.getName(), CountersEnum.INPUT_WORDS.toString());

				counter.increment(1);
			}
		}
	}

	public static class CountReducer extends Reducer<Text,IntWritable,Text,IntWritable> { 
		private IntWritable result = new IntWritable(); 
		public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException { 
			int sum = 0; 
			for (IntWritable value : values) { 
				sum += value.get(); 
			} 
			result.set(sum); 
			context.write(key, result); 
		} 
	}

	public static void main(String[] args) throws Exception {

		Configuration cf = new Configuration();
		GenericOptionsParser optionParser = new GenericOptionsParser(cf, args);
		String[] remainingArgs = optionParser.getRemainingArgs();
		if ((remainingArgs.length != 2) && (remainingArgs.length != 4)) {

			System.err.println("Usage: StringCounter <in> <out> [-skip skipPatternFile]");
			System.exit(2);
		}

		Job job = Job.getInstance(cf, "word count");
		job.setJarByClass(WordCount2.class);
		job.setMapperClass(TokenMapper.class);
		job.setCombinerClass(CountReducer.class);
		job.setReducerClass(CountReducer.class);
		job.setOutputKeyClass(Text.class); // 문자열 형태
		job.setOutputValueClass(IntWritable.class); // 정수 형태
		List<String> otherArgs = new ArrayList<String>();

		for (int i=0; i < remainingArgs.length; ++i) {

			if ("-skip".equals(remainingArgs[i])) {
				job.addCacheFile(new Path(remainingArgs[++i]).toUri());
				job.getConfiguration().setBoolean("StringCounter.skip.patterns", true);
			} else {
				otherArgs.add(remainingArgs[i]);
			}
		}

		FileInputFormat.addInputPath(job, new Path(otherArgs.get(0)));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs.get(1)));
		System.exit(job.waitForCompletion(true) ? 0 : 1); // 잡의 처리 결과 리턴
	}
}
