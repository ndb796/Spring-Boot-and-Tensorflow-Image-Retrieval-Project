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
     * Ư���� URL�� �����Ͽ� POST ������� ��û(Request)�� ���� �� �ֵ��� �غ��մϴ�.
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
     * ��û(Request) ��Ŷ�� �ٵ�(Body) �κп� �Ķ���� �����͸� ����ϴ�.
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
     * ��û(Request) ��Ŷ�� �ٵ�(Body) �κп� ���� �����͸� ����ϴ�.
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
     * ���ε带 ��ģ �ڿ� �����κ��� ������ �޴� �޼ҵ��Դϴ�.
     */
    public String finish() throws IOException {
        String response = "";

        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        // ������ ���� �ڵ尡 ������ ��� ������ ������ ��ȯ�մϴ�.
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
            throw new IOException("���� ���� �߻�: " + status);
        }

        return response;
    }
}