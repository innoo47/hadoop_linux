import org.apache.hadoop.io.Text; // 문자열 형식 키 
import org.apache.hadoop.mapreduce.Mapper; // 매퍼 
import java.io.IOException; // 예외처리 
import org.apache.hadoop.io.IntWritable; // 정수형 값 
import org.apache.hadoop.io.LongWritable; 

public class FindMaxTempMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
       	private static final int MISSING = 9999; // 실측 데이터 
	
	@Override public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
	{ 
		String data = value.toString(); String year = data.substring(15, 19); // 연도 추출 
		int airTemp;
		// 기온 추출: parseInt() 메소드는 + 문자 처리가 안되므로 제외 시킴 
		if (data.charAt(87)=='+') airTemp=Integer.parseInt(data.substring(88,92)); 
		else airTemp = Integer.parseInt(data.substring(87, 92)); 
		
		String quality = data.substring(92, 93); 
		if (airTemp != MISSING && quality.matches("[01459]")) // 정상이면 
		{ 
			context.write(new Text(year), new IntWritable(airTemp)); // 출력 
		} 
	} 
}
