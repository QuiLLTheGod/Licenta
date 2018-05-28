package quill.gmail.com.licenta.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.activities.ItemDetailsActivity;
import quill.gmail.com.licenta.activities.UsersActivity;
import quill.gmail.com.licenta.model.Item;

public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.ViewHolder>{


    private Context mContext;
    private ArrayList<Item> mItems;
    public PasswordsAdapter(Context context, ArrayList<Item> items){
        mContext = context;
        mItems = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView item_image;
        TextView password_name, passoword_description;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            password_name = itemView.findViewById(R.id.password_name);
            passoword_description = itemView.findViewById(R.id.password_description);
            linearLayout = itemView.findViewById(R.id.linear_layout_IL);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.items_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Item item = mItems.get(position);

        ImageView image = holder.item_image;
        TextView name = holder.password_name;
        TextView description = holder.passoword_description;

        image.setImageResource(item.getImageID());
        name.setText(item.getUsedFor());
        description.setText(item.getDescription());
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ItemDetailsActivity.class);
            intent.putExtra("ID", mItems.get(position).getId());
            intent.putExtra("Password", mItems.get(position).getDecryptedPassword());
            intent.putExtra("Username", mItems.get(position).getUsername());
            intent.putExtra("Details", mItems.get(position).getDescription());
            intent.putExtra("UsedFor", mItems.get(position).getUsedFor());
            ((Activity)mContext).startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
