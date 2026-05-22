package com.budgetapp.auth;

import com.budgetapp.model.User;

/**
 * Holds the currently logged-in user for the duration of the session.
 * Controllers read/write through here instead of passing User objects around.
 *
 * Usage:
 *   After successful login:   auth.setCurrentUser(user);
 *   From any controller:      auth.getCurrentUser();
 *   On logout button:         auth.logout();
 */
public class auth {

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        if (currentUser != null) {
            currentUser.logout();
        }
        currentUser = null;
    }
}
