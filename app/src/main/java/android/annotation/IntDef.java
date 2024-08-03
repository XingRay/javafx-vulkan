package android.annotation;

public @interface IntDef {
    boolean flag();

    String[] prefix();

    int[] value();
}
