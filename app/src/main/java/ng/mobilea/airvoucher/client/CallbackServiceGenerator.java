package ng.mobilea.airvoucher.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Hp on 13/12/2017.
 */

public class CallbackServiceGenerator {


    //private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
    //                .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static Retrofit retrofit = null;
    // No need to instantiate this class.
    private CallbackServiceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        return createService(serviceClass, baseUrl, null);
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, final HashMap<String, String> headers) {

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
//        if(!okHttpClient.interceptors().contains(logging))
//            okHttpClient.interceptors().add(logging);

        if(headers != null && !headers.isEmpty() ){
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Headers.Builder headerBuilder = new Headers.Builder();
                    for (Map.Entry<String, String> pair : headers.entrySet()){
                        headerBuilder.add(pair.getKey(), pair.getValue());
                    }
                    Request request = original.newBuilder()
                            .headers(headerBuilder.build())
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            };

            if(!okHttpClient.interceptors().contains(interceptor))
                okHttpClient.interceptors().add(interceptor);
        }

        if(retrofit == null)
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        return retrofit.create(serviceClass);
    }
}
