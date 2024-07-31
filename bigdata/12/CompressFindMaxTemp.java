import org.apache.hadoop.io.Text; // 문자열 형식 키
import org.apache.hadoop.fs.Path; // 파일 경로 생성
import org.apache.hadoop.io.IntWritable; // 정수형 값
import org.apache.hadoop.mapreduce.Job; // 맵리듀스 잡
import org.apache.hadoop.io.compress.GzipCodec; // 압축 코덱
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat; // 입력데이터
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat; // 분석결과

public class CompressFindMaxTemp {

	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			System.err.println("Usage: CompressFindMaxTemp <input path> " + "<output path>");
			System.exit(-1);
		}

		Job job = new Job();
		job.setJarByClass(FindMaxTemp.class);
		FileInputFormat.addInputPath(job, new Path(args[0])); // 입력 데이터 경로
		FileOutputFormat.setOutputPath(job, new Path(args[1])); // 출력 데이터 경로
		job.setOutputKeyClass(Text.class); // 문자열 형태
		job.setOutputValueClass(IntWritable.class); // 정수 형태

		FileOutputFormat.setCompressOutput(job, true);
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

		job.setMapperClass(FindMaxTempMapper.class); // 매퍼 실행
		job.setCombinerClass(FindMaxTempReducer.class); // 컴바이너 실행
		job.setReducerClass(FindMaxTempReducer.class); // 리듀서 실행
		System.exit(job.waitForCompletion(true) ? 0 : 1); // 잡의 처리 결과 리턴
	}
}
