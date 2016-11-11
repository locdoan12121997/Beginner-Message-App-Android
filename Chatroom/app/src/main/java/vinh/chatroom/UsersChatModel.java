package vinh.chatroom;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;


public class UsersChatModel implements Serializable{

    /*recipient info*/
    private String name;
    private String provider; //if you don't include this app crash
    private String email;
    private String createtime;
    private String connection;
    @Exclude
    private int    avatarId;
    private String mRecipientUid;

    /*Current user (or sender) info*/
    private String mCurrentUserName;
    private String mCurrentUserUid;
    private String mCurrentUserEmail;
    private String mCurrentUserCreatedAt;

    public UsersChatModel(){
        //required empty username
    }
    UsersChatModel(String name,String provider, String email, String createtime, String connection){
        this.name=name;
        this.provider=provider;
        this.email=email;
        this.createtime=createtime;
        this.connection=connection;
    }

    /*Recipient info*/
    public String getFirstName() {
        return name;
    }

    public String getUserEmail() {
        //Log.e("user email  ", userEmail);
        return email;
    }
    public void setEmail(String email){this.email=email;
    }

    public String getProvider() {
        return provider;
    }

    public String getCreatedAt() {
        return createtime;
    }
    public void setCreateAt(String createtime){this.createtime=createtime;}
    public String getConnection(){
        return connection;
    }

    public int    getAvatarId(){
        return avatarId;
    }

    public String getRecipientUid(){
        return mRecipientUid;
    }

    public void setRecipientUid(String givenUserUid){
        mRecipientUid =givenUserUid;
    }

    public void setRecipientName(String name ){this.name=name;}
    /*Current user (or sender) info*/
    public void setCurrentUserName(String currentUserName){
        mCurrentUserName=currentUserName;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        mCurrentUserEmail = currentUserEmail;
    }

    public void setCurrentUserCreatedAt(String currentUserCreatedAt) {
        this.mCurrentUserCreatedAt = currentUserCreatedAt;
    }

    public void setCurrentUserUid(String currentUserUid){
        mCurrentUserUid=currentUserUid;
    }

    public String getCurrentUserName(){
        return mCurrentUserName;
    }

    public String getCurrentUserEmail() {
        //Log.e("current user email  ", mCurrentUserEmail);
        return mCurrentUserEmail;
    }

    public String getCurrentUserCreatedAt() {
        return mCurrentUserCreatedAt;
    }

    public String getCurrentUserUid(){
        return mCurrentUserUid;
    }


    /*create chat endpoint for firebase*/
    public String getChatRef(){
        return createUniqueChatRef();
    }



    private String createUniqueChatRef(){
        String uniqueChatRef="";
        if(createdAtCurrentUser()>createdAtRecipient()){
            uniqueChatRef=cleanEmailAddress(getCurrentUserEmail())+"-"+cleanEmailAddress(getUserEmail());
        }else {

            uniqueChatRef=cleanEmailAddress(getUserEmail())+"-"+cleanEmailAddress(getCurrentUserEmail());
        }
        return uniqueChatRef;
    }

    private long createdAtCurrentUser(){
        return Long.parseLong(getCurrentUserCreatedAt());
    }

    private long createdAtRecipient(){
        return Long.parseLong(getCreatedAt());
    }

    private String cleanEmailAddress(String email){

        //replace dot with comma since firebase does not allow dot
        return email.replace(".","-");

    }



}