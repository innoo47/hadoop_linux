import java.net.URI;
import org.apache.hadoop.fs.Path; // 파일 경로 생성
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;

public class DisplayFileTwiceFS {
	public static void main(String[] args) throws Exception {
		String uri = args[0];
		Configuration conf = new Configuration();

		FileSystem filesystem = FileSystem.get(URI.create(uri), conf);
		FSDataInputStream in = null;

		try {
			in = filesystem.open(new Path(uri));
			IOUtils.copyBytes(in, System.out, 4096, false);
			in.seek(0); // 파일의 맨 앞으로 다시 이동
			IOUtils.copyBytes(in, System.out, 4096, false);
		} finally {
			IOUtils.closeStream(in);
		}
	}
}
