package io.github.ktpm.bluemoonmanagement.util;

import javafx.scene.Parent;

public class FxView<T> {
    private final Parent view;
    private final T controller;

    public FxView(Parent view, T controller) {
        this.view = view;
        this.controller = controller;
    }

    public Parent getView() {
        return view;
    }

    public T getController() {
        return controller;
    }
}

