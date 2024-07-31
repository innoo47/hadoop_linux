import java.io.IOException; // 예외처리 
import java.util.StringTokenizer; 
import org.apache.hadoop.io.Text; // 문자열 형식 키 
import org.apache.hadoop.fs.Path; // 파일 경로 생성 
import org.apache.hadoop.io.IntWritable; // 정수형 값 
import org.apache.hadoop.mapreduce.Job; // 맵리듀스 잡 
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.mapreduce.Mapper; // 매퍼 
import org.apache.hadoop.mapreduce.Reducer; // 리듀서 
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat; // 입력데이터 
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat; // 분석결과

public class StringCounter {
	public static class TokenMapper extends Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text text = new Text();
		public void map(Object key, Text value, Context context) throws IOException, 
		       InterruptedException {
			       StringTokenizer itr = new StringTokenizer(value.toString());
			       while(itr.hasMoreTokens()) {
				       text.set(itr.nextToken());
				       context.write(text, one);
			       }
		}
	}

	public static class CountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for(IntWritable value : values) {
				sum += value.get();
			}

			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration cf = new Configuration();
		Job job = Job.getInstance(cf, "string count");
		job.setJarByClass(StringCounter.class);
		job.setMapperClass(TokenMapper.class);
		job.setCombinerClass(CountReducer.class);
		job.setReducerClass(CountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
