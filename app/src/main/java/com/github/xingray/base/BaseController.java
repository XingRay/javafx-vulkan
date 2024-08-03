package com.github.xingray.base;

import com.xingray.java.util.TaskExecutor;
import com.xingray.javafx.base.Controller;

import java.net.URL;
import java.util.function.Function;

public class BaseController extends Controller {
    public BaseController() {
        setUrlMapper(new Function<String, URL>() {
            @Override
            public URL apply(String s) {
                return getResource(s);
            }
        });
    }

    public static URL getResource(String relativePath) {
        return BaseController.class.getResource(relativePath);
    }

    public void runOnUiThread(Runnable task) {
        TaskExecutor.ui(task);
    }

    public void runOnIoThread(Runnable task) {
        TaskExecutor.io(task);
    }
}
