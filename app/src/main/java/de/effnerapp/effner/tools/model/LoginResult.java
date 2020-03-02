package de.effnerapp.effner.tools.model;

public class LoginResult {
    private boolean login;
    private boolean showLoginScreen;

    public LoginResult(boolean login, boolean showLoginScreen) {
        this.login = login;
        this.showLoginScreen = showLoginScreen;
    }

    public boolean isLogin() {
        return login;
    }

    public boolean showLoginScreen() {
        return showLoginScreen;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void setShowLoginScreen(boolean showLoginScreen) {
        this.showLoginScreen = showLoginScreen;
    }
}
