package mglowinski.library.api;

/**
 * Created by macglo on 21.09.17.
 */

public class ApiUtils {

    public static final String BASE_URL = "http://10.132.233.60:8080/api/";

    public static SOService getSOService() {
        return RetrofitClient.getClient(BASE_URL).create(SOService.class);
    }
}