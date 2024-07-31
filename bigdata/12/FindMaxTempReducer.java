import java.io.IOException; // 예외처리 
import org.apache.hadoop.io.Text; // 문자열 형식 키 
import org.apache.hadoop.io.IntWritable; // 정수형 값 
import org.apache.hadoop.mapreduce.Reducer; // 리듀서 

public class FindMaxTempReducer extends Reducer<Text, IntWritable, Text, IntWritable> { 
	
	@Override public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
       
	int maxTemp = Integer.MIN_VALUE; // 최대값 초기화 
	
	for (IntWritable value : values) {
	       	maxTemp = Math.max(maxTemp, value.get()); // 최대값 산출 
	} 
	
	context.write(key, new IntWritable(maxTemp)); // 결과 출력 
	} 
}
