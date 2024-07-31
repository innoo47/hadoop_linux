import org.apache.hadoop.fs.Path; // 파일 경로 생성 
import org.apache.hadoop.io.Text; // 문자열 형식 키 
import org.apache.hadoop.io.IntWritable; // 정수형 값 
import org.apache.hadoop.mapreduce.Job; // 맵리듀스 잡 
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat; // 입력데이터 
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat; // 분석결과 

public class FindMaxTemp { 
	public static void main(String[] args) throws Exception // 메인함수 
	{ 
		if (args.length != 2) // 데이터 경로 요청 
		{
		       	System.err.println("Input: FindMaxTemp <input path> <output path>"); 
			System.exit(-1); 
		}

		Job job = new Job(); 
		job.setJarByClass(FindMaxTemp.class); 
		job.setJobName("Find Max temp"); 
		
		FileInputFormat.addInputPath(job, new Path(args[0])); // 입력 데이터 경로 
		FileOutputFormat.setOutputPath(job, new Path(args[1])); // 출력 데이터 경로 
		
		job.setMapperClass(FindMaxTempMapper.class); // 매퍼 
		job.setReducerClass(FindMaxTempReducer.class); // 리듀서 
		job.setOutputKeyClass(Text.class); // 문자열 형태 
		job.setOutputValueClass(IntWritable.class); // 정수 형태 
		
		System.exit(job.waitForCompletion(true) ? 0 : 1 ); 
	} 
}
