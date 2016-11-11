package vinh.chatroom;

/**
 * Created by chuatebongdem on 10/10/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.id.message;

/**
 * Created by chuatebongdem on 9/21/2016.
 */

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter (Context context, ArrayList<User> user){
        super(context,0,user);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ){
        User user = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_users,parent,false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.listItemName);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.listItemAvatar);

        name.setText(user.getName());
        avatar.setImageResource(R.drawable.first);

        return convertView;
    }
}
