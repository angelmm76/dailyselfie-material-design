package dailyselfie.recyclerview;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SelfieRecyViewAdapter extends RecyclerView.Adapter<SelfieRecyViewAdapter.SelfieViewHolder> {
	private ArrayList<SelfieRecord> mSelfies = new ArrayList<SelfieRecord>();
	private OnItemClickListener listener;

	private static final String TAG = "SelfieRecyView Adapter";

	public interface OnItemClickListener {
		void onItemClick(SelfieRecord selfie);
	}

	// Provide a suitable constructor (depends on the kind of dataset)
	public SelfieRecyViewAdapter(ArrayList<SelfieRecord> items, OnItemClickListener listener) {
		this.mSelfies = items;
		this.listener = listener;
	}

	// Provide a reference to the views for each data item.  Complex data items may need more than
	// one view per item, and you provide access to all the views for a data item in a view holder

	public static class SelfieViewHolder extends RecyclerView.ViewHolder {
		CardView cv;
		ImageView thumbnail;
		TextView date;
		Button button;

		// Constructor
		SelfieViewHolder(View itemView) {
			super(itemView);
			cv = (CardView)itemView.findViewById(R.id.card_view);
			date = (TextView)itemView.findViewById(R.id.selfie_date);
			thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
			button = (Button) itemView.findViewById(R.id.button1);
		}

		// On item click listener
		public void bind(final SelfieRecord item, final OnItemClickListener listener) {
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					listener.onItemClick(item);
				}
			});
		}
	}

	//	List<SelfieRecord> selfies;

	// Provide a suitable constructor (depends on the kind of dataset)
//	SelfieRecyViewAdapter(ArrayList<SelfieRecord> selfies){
//		this.mSelfies = selfies;
//	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mSelfies.size();
	}

	// Return the item in position
	public SelfieRecord getItem(int position) {
		return mSelfies.get(position);
	}

	// Create new views (invoked by the layout manager)
	@Override
	public SelfieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		Log.i(TAG, "New item view created (onCreateViewHolder)");
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view, viewGroup, false);
		SelfieViewHolder selfivihol = new SelfieViewHolder(v);
		return selfivihol;
	}

	// Replace the contents of a view (invoked by the layout manager). Similar to getView
	// Set values of cardview fields
	@Override
	public void onBindViewHolder(SelfieViewHolder holder, int i) {
		Log.i(TAG, "Get view (onBindViewHolder), position: " + i);
		holder.date.setText(mSelfies.get(i).getDate());
		holder.thumbnail.setImageBitmap(mSelfies.get(i).getThumbnail());
		// Set delete button
		final int pos = i;
		holder.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Delete selfie, position: " + pos);
				String timeStamp = mSelfies.get(pos).getDate();
//				Snackbar.make(, "Selfie deleted", Toast.LENGTH_LONG).show();
				File file = new File(mSelfies.get(pos).getFilePath());
				file.delete();
				mSelfies.remove(pos);
				notifyDataSetChanged();
				// Snackbar
				Snackbar.make(v, "Selfie " + timeStamp + " deleted", Snackbar.LENGTH_LONG)
						.setAction("UNDO", new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								Log.i(TAG, "Snackbar action clicked!");
							}
						})
						.show();
			}
		});
		holder.bind(mSelfies.get(i), listener);
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	public void add(SelfieRecord selfieItem) {
		mSelfies.add(selfieItem);
		notifyDataSetChanged();
	}

//	private ArrayList<SelfieRecord> list = new ArrayList<SelfieRecord>();
//	private static LayoutInflater inflater = null;
//	private Context mContext;
//
//	public SelfieRecyViewAdapter(Context context) {
//		mContext = context;
//		inflater = LayoutInflater.from(mContext);
//	}
//
//	public int getCount() {
//		return list.size();
//	}
//
//	public Object getItem(int position) {
//		return list.get(position);
//	}
//
//	public long getItemId(int position) {
//		return position;
//	}
//
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		View newView = convertView;
//		ViewHolder holder;
//		final int pos = position;
//
//		SelfieRecord curr = list.get(position);
//
//		if (null == convertView) {
//
//			holder = new ViewHolder();
//			newView = inflater.inflate(R.layout.selfie_view, null);
//			holder.thumbnail = (ImageView) newView.findViewById(R.id.thumbnail);
//			holder.date = (TextView) newView.findViewById(R.id.selfie_date);
//			newView.setTag(holder);
//
//			Button deleteButton = (Button) newView.findViewById(R.id.button1);
//
//			deleteButton.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Toast.makeText(mContext, "Selfie deleted", Toast.LENGTH_LONG).show();
//					File file = new File(list.get(pos).getFilePath());
//					file.delete();
//					list.remove(pos);
//			        notifyDataSetChanged();
//
//				}
//			});
//
//		} else {
//			Log.i(TAG, "GetView. Recycled convert view, position: " + position);
//			holder = (ViewHolder) newView.getTag();
//		}
//
//		holder.thumbnail.setImageBitmap(curr.getThumbnail());
//		holder.date.setText(curr.getDate());
//
//		return newView;
//	}
//
//	static class ViewHolder {
//		ImageView thumbnail;
//		TextView date;
//	}
//
//	public void add(SelfieRecord listItem) {
//		list.add(listItem);
//		notifyDataSetChanged();
//	}
//
//	public ArrayList<SelfieRecord> getList(){
//		return list;
//	}
//
//	public void removeAllViews(){
//		list.clear();
//		this.notifyDataSetChanged();
//
//	}
}
