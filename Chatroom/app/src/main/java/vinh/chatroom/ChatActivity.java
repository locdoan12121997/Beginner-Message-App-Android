package vinh.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends Activity {

    private RecyclerView mChatRecyclerView;
    private TextView mUserMessageChatText;
    private MessageChatAdapter mMessageChatAdapter;

    /* Sender and Recipient status*/
    private static final int SENDER_STATUS=0;
    private static final int RECIPIENT_STATUS=1;

    /* Recipient uid */
    private String mRecipientUid;

    /* Sender uid */
    private String mSenderUid;

    /* unique Firebase ref for this chat */
    private DatabaseReference mFirebaseMessagesChat;

    /* Listen to change in chat in firabase*/
    private ChildEventListener mMessageChatListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get information from the previous activity
        Intent getUsersData=getIntent();
        UsersChatModel usersDataModel= (UsersChatModel) getUsersData.getSerializableExtra("usersData");

        // Set recipient uid
        mRecipientUid=usersDataModel.getRecipientUid();

        // Set sender uid;
        mSenderUid=usersDataModel.getCurrentUserUid();

        // Reference to recyclerView and text view
        mChatRecyclerView=(RecyclerView)findViewById(R.id.chat_recycler_view);
        mUserMessageChatText=(TextView)findViewById(R.id.chat_user_message);

        // Set recyclerView and adapter
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setHasFixedSize(true);

        // Initialize adapter
        List<MessageChatModel> emptyMessageChat=new ArrayList<MessageChatModel>();
        mMessageChatAdapter=new MessageChatAdapter(emptyMessageChat);

        // Set adapter to recyclerView
        mChatRecyclerView.setAdapter(mMessageChatAdapter);

        // Initialize firebase for this chat
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mFirebaseMessagesChat= database.getReference().child("chat").child(usersDataModel.getChatRef());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMessageChatListener=mFirebaseMessagesChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                if(dataSnapshot.exists()){

                    MessageChatModel newMessage=dataSnapshot.getValue(MessageChatModel.class);
                    if(newMessage.getSender().equals(mSenderUid)){
                        newMessage.setRecipientOrSenderStatus(SENDER_STATUS);
                    }else{
                        newMessage.setRecipientOrSenderStatus(RECIPIENT_STATUS);
                    }
                    mMessageChatAdapter.refillAdapter(newMessage);
                    mChatRecyclerView.scrollToPosition(mMessageChatAdapter.getItemCount()-1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Remove listener
        if(mMessageChatListener !=null) {
            // Remove listener
            mFirebaseMessagesChat.removeEventListener(mMessageChatListener);
        }
        // Clean chat message
        mMessageChatAdapter.cleanUp();

    }


    public void sendMessageToFireChat(View sendButton){
        String senderMessage=mUserMessageChatText.getText().toString();
        senderMessage=senderMessage.trim();

        if(!senderMessage.isEmpty()){

            // Send message to firebase
            Map<String, String> newMessage = new HashMap<String, String>();
            newMessage.put("sender", mSenderUid); // Sender uid
            newMessage.put("recipient",mRecipientUid); // Recipient uid
            newMessage.put("message",senderMessage); // Message

            mFirebaseMessagesChat.push().setValue(newMessage);

            // Clear text
            mUserMessageChatText.setText("");

        }
    }


}