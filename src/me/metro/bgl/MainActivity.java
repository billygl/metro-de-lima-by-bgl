package me.metro.bgl;

import me.metro.bgl.adapters.TabsAdapter;
import me.metro.bgl.model.dao.DataBaseHelper;
import me.metro.bgl.model.dao.EstacionDao;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private boolean editable = false;

	public boolean isEditable() {
		return EstacionDao.isEditable(this);
	}

	public void toggleEditar() {
		editable = EstacionDao.isEditable(this);
		EstacionDao.setEditable(this, !editable);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		Bundle args = new Bundle();
		args.putBoolean(DataBaseHelper.FAVORITAS, true);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("todas")
						.setIndicator(
								createTabView(this, getText(R.string.todas)
										.toString())),
				EstacionesActivity.CursorLoaderListFragment.class, null);

		mTabsAdapter.addTab(
				mTabHost.newTabSpec("favoritas").setIndicator(
						createTabView(this, getText(R.string.frecuentes)
								.toString())),
				EstacionesActivity.CursorLoaderListFragment.class, args);
		mTabHost.setCurrentTab(1);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_opciones, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {		
		MenuItem item = menu.getItem(0);
		if (EstacionDao.isEditable(this))
			item.setTitle(R.string.str_no_editar);
		else
			item.setTitle(R.string.str_editar);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.iteOpcionInstrucciones) {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.instrucciones_dialog);
			dialog.setTitle(getText(R.string.mnu_instrucciones).toString());
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		} else if (item.getItemId() == R.id.iteOpcionAcerca) {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.acerca_dialog);
			dialog.setTitle(getText(R.string.mnu_acerca).toString());
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		} else if (item.getItemId() == R.id.iteOpcionEditar) {
			toggleEditar();

		}
		return true;
	}
}