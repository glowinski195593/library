package mglowinski.library.api;

/**
 * Created by macglo on 21.09.17.
 */

public class ApiUtils {

    public static final String BASE_URL = "http://www.mocky.io/v2/";

    public static SOService getSOService() {
        return RetrofitClient.getClient(BASE_URL).create(SOService.class);
    }
}