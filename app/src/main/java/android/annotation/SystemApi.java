package android.annotation;

public @interface SystemApi {
    class Client{

        public static final int PRIVILEGED_APPS = 0;
    }

    int client();
}
