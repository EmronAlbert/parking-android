package webservice;

/**
 * Created by m09ny on 09/15/17.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class VacanciesTask  extends AsyncTask<String, String, String> implements CredentialInterface{

    private VacanciesDelegate vacanciesDelegate;
    private String username;
   private String password;

    @Override
    protected String doInBackground(String... params) {
        try {
            return callVacanciesService();
        } catch (IOException | JSONException e) {
            Log.e("ERROR", "Failed to get the vacancies.", e);
            return null;
        }
    }

    private String callVacanciesService() throws IOException, JSONException {

        Uri uri = Uri.parse(BASE_URL).buildUpon().appendPath("vacancies").build();

        HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
        connection.setRequestMethod("GET");

        connection.setUseCaches(false);
        connection.setRequestProperty("User-Agent", "MyAgent");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);

        String baseAuthStr = username + ":" + password;
        connection.addRequestProperty("Authorization", "Basic " + Base64.encodeToString(baseAuthStr.getBytes("UTF-8"), Base64.DEFAULT));

        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);

        connection.connect();

        StringBuilder sb = new StringBuilder();
        int httpResult = connection.getResponseCode();
        if (httpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            System.out.println("" + sb.toString());
        } else {
            System.out.println(connection.getResponseMessage());
        }

        //connection.disconnect();

        return sb.toString();
    }


    public VacanciesTask(String username, String password) {

        this.username = username;
        this.password = password;

        Uri uri = Uri.parse(BASE_URL).buildUpon().appendPath("vacancies").build();
        this.execute(uri.toString());
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        String response = String.valueOf(o);

        if (vacanciesDelegate != null){
            vacanciesDelegate.onVacanciesDone(response);
        }

    }

    public VacanciesDelegate getDelegate() {
        return vacanciesDelegate;
    }

    public void setVacanciesDelegate(VacanciesDelegate vacanciesDelegate) {
        this.vacanciesDelegate = vacanciesDelegate;
    }
}
