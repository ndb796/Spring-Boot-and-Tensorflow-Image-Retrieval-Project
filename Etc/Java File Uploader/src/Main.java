import java.io.File;

public class Main {

	public static void main(String[] args) {
		try {
			MultipartUtility multipartUtility = new MultipartUtility("localhost", 5000, "/");
			multipartUtility.addFilePart("query_img", new File("images/Santa.jpg"));
			String result = multipartUtility.finish();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
