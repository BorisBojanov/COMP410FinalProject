package com.budgetapp.user;
/*
Represents the person using the budgeting app.

userId	                                    int	Attribute
name	                                    String	Attribute
email	                                    String	Attribute
passwordHash	                            String	Attribute
login(email: String, password: String)	    boolean	Method
logout()	                                void	Method
connectEmailAccount(account: EmailAccount)	void	Method
viewDashboard()	                            void	Method
*/
public class user {
    private int userId;
    private String name;
    private String email;
    private String passwordHash;

    public user(int userId, String name, String email, String passwordHash) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    

}
