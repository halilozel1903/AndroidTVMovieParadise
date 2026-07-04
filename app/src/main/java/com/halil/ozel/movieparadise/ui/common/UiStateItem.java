package com.halil.ozel.movieparadise.ui.common;

public final class UiStateItem {

    public static final class Loading {
        private static final Loading INSTANCE = new Loading();

        private Loading() {}

        public static Loading get() {
            return INSTANCE;
        }
    }

    public static final class Error {
        private final String message;
        private final Runnable retryAction;

        public Error(String message, Runnable retryAction) {
            this.message = message;
            this.retryAction = retryAction;
        }

        public String getMessage() {
            return message;
        }

        public Runnable getRetryAction() {
            return retryAction;
        }
    }

    public static final class Empty {
        private final String message;

        public Empty(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class Retry {
        private final String message;
        private final Runnable retryAction;

        public Retry(String message, Runnable retryAction) {
            this.message = message;
            this.retryAction = retryAction;
        }

        public String getMessage() {
            return message;
        }

        public Runnable getRetryAction() {
            return retryAction;
        }
    }

    private UiStateItem() {}
}
