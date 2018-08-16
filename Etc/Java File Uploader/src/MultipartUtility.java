import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class MultipartUtility {
	
    private HttpURLConnection conn;
    private DataOutputStream request;
    
    private final String boundary =  "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";

    /*
     * 특정한 URL로 접속하여 POST 방식으로 요청(Request)을 보낼 수 있도록 준비합니다.
     */
    public MultipartUtility(String host, int port, String path) throws IOException {

        URL url = new URL("http", host, port, path);
        conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);

        request =  new DataOutputStream(conn.getOutputStream());
    }

    /*
     * 요청(Request) 패킷의 바디(Body) 부분에 파라미터 데이터를 담습니다.
     */
    public void addFormField(String name, String value) throws IOException  {
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""+ this.crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);
        request.writeBytes(value+ this.crlf);
        request.flush();
    }

    /*
     * 요청(Request) 패킷의 바디(Body) 부분에 파일 데이터를 담습니다.
     */
    public void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                fieldName + "\";filename=\"" +
                fileName + "\"" + this.crlf);
        request.writeBytes(this.crlf);

        byte[] bytes = Files.readAllBytes(uploadFile.toPath());
        request.write(bytes);
    }

    /*
     * 업로드를 마친 뒤에 서버로부터 응답을 받는 메소드입니다.
     */
    public String finish() throws IOException {
        String response = "";

        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        // 서버의 상태 코드가 정상인 경우 서버의 응답을 반환합니다.
        int status = conn.getResponseCode();
        
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new BufferedInputStream(conn.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            
            responseStreamReader.close();
            response = stringBuilder.toString();
            conn.disconnect();
        } else {
            throw new IOException("서버 오류 발생: " + status);
        }

        return response;
    }
}