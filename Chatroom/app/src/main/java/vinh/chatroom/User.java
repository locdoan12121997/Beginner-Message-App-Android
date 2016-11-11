

/**
 * Created by chuatebongdem on 9/28/2016.
 */

package vinh.chatroom;

/**
 * Created by chuatebongdem on 9/21/2016.
 */

public class User {
    private String name, email,userID;
    private int avatar;

    public User(int avatar ,String name, String email){
        this.avatar=avatar;
        this.name=name;
        this.email=email;
        this.userID=null;
    }

    public User(int avatar ,String name, String email, String userID){
        this.avatar=avatar;
        this.name=name;
        this.email=email;
        this.userID=userID;
    }

    public String getName(){
        return name;
    }

    public String getUserId(){
        return userID;
    }

    public int getAvatar() {
        return avatar;
    }

    public  String getID() {return userID;}

    public User getObject() {return this;}



}
