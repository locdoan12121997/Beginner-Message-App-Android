package vinh.chatroom;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainSecondFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    private DatabaseReference mFirebaseChatRef;

    /* Reference to users in firebase */
    private DatabaseReference mFireChatUsersRef;
    private DatabaseReference myConnectionsStatusRef;

    /* Listener for Firebase session changes */
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    /* recyclerView for mchat users */
    private RecyclerView mUsersFireChatRecyclerView;

    /* progress bar */
    private View mProgressBarForUsers;

    /* fire chat adapter */
    private UsersChatAdapter mUsersChatAdapter;

    /* current user uid */
    private String mCurrentUserUid;
    private String mCurrentUserName;

    /* current user email */
    private String mCurrentUserEmail;
    private FirebaseUser mAuthData;

    /* Listen to users change in firebase-remember to detach it */
    private ChildEventListener mListenerUsers;

    /* Listen for user presence */
    private ValueEventListener mConnectedListener;

    /* List holding user key */
    private List<String>  mUsersKeyList;


    public static MainSecondFragment newInstance(int sectionNumber) {
        MainSecondFragment fragment = new MainSecondFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainSecondFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_second, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mFirebaseChatRef=database.getReference();
        mFireChatUsersRef=database.getReference("users");
        mUsersFireChatRecyclerView=(RecyclerView)rootView.findViewById(R.id.usersFireChatRecyclerView);
        List<UsersChatModel> emptyListChat=new ArrayList<UsersChatModel>();
        mUsersChatAdapter =new UsersChatAdapter(getActivity(),emptyListChat);

        // Set adapter to recyclerView
        mUsersFireChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUsersFireChatRecyclerView.setHasFixedSize(true);
        mUsersFireChatRecyclerView.setAdapter(mUsersChatAdapter);

        // Initialize keys list
        mUsersKeyList=new ArrayList<String>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                setAuthenticatedUser(user);
            }
        });
        return rootView;


    }


    private void setAuthenticatedUser(FirebaseUser authData) {
        mAuthData=authData;
        if (authData != null) {

            /* User auth has not expire yet */

            // Get unique current user ID
            mCurrentUserUid=authData.getUid();

            // Get current user email
            mCurrentUserEmail= (String) authData.getEmail();
            mCurrentUserName= (String) authData.getDisplayName();

            // Query all mChat user except current user
            queryFireChatUsers();


        } else {
            // Token expires or user log out
            // So show logIn screen to reinitiate the token
            navigateToLogin();
        }
    }
    private void queryFireChatUsers() {

        //Show progress bar

        mListenerUsers = mFireChatUsersRef.limitToFirst(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Hide progress bar

                if (dataSnapshot.exists()) {

                    String userUid = dataSnapshot.getKey();

                    if (!userUid.equals(mCurrentUserUid)) {

                        //Get recipient user name
                        UsersChatModel user = dataSnapshot.getValue(UsersChatModel.class);
                        //Add recipient uid
                        user.setRecipientName((String) dataSnapshot.child("name").getValue());
                        user.setCreateAt((String)dataSnapshot.child("createtime").getValue());
                        user.setEmail((String)dataSnapshot.child("email").getValue());
                        user.setRecipientUid(userUid);
                        //Add current user (or sender) info
                        user.setCurrentUserEmail(mCurrentUserEmail); //email
                        user.setCurrentUserUid(mCurrentUserUid);//uid
                        mUsersKeyList.add(userUid);
                        mUsersChatAdapter.refill(user);


                    } else {
                        String userName = (String) dataSnapshot.child("name").getValue(); //Get current user first name
                        String createdAt = (String)dataSnapshot.child("createtime").getValue(); //Get current user date creation
                        mUsersChatAdapter.setNameAndCreatedAt(userName, createdAt); //Add it the adapter
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()) {
                    String userUid = dataSnapshot.getKey();
                    if (!userUid.equals(mCurrentUserUid)) {
                        UsersChatModel user = dataSnapshot.getValue(UsersChatModel.class);

                        //Add recipient uid
                        user.setRecipientUid(userUid);

                        //Add current user (or sender) info
                        user.setCurrentUserEmail(mCurrentUserEmail); //email
                        user.setCurrentUserUid(mCurrentUserUid);//uid
                        int index = mUsersKeyList.indexOf(userUid);
                        mUsersChatAdapter.changeUser(index, user);
                    }

                }


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
        myConnectionsStatusRef= mFireChatUsersRef.child(mCurrentUserUid).child("connection");

        // Indication of connection status
        mConnectedListener = mFirebaseChatRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    myConnectionsStatusRef.setValue("online");
                    // When this device disconnects, remove it
                    myConnectionsStatusRef.onDisconnect().setValue("offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    private void navigateToLogin() {

        // Go to LogIn screen
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        startActivity(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        auth.removeAuthStateListener(mAuthStateListener);

        mUsersKeyList.clear();

        // Stop all listeners
        // Make sure to check if they have been initialized
        if(mListenerUsers!=null) {
            mFireChatUsersRef.removeEventListener(mListenerUsers);
        }
        if(mConnectedListener!=null) {
            mFirebaseChatRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        }
    }


    private void logout() {

        if (this.mAuthData != null) {

            /* Logout of mChat */

            // Store current user status as offline
            myConnectionsStatusRef.setValue("offline");

            // Finish token
            auth.signOut();

            /* Update authenticated user and show login screen */
            setAuthenticatedUser(null);
        }
    }


}
