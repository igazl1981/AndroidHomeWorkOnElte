package com.fitbuilder.productbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.fitbuilder.productbrowser.config.Globals;
import com.fitbuilder.productbrowser.fragments.CategoriesListFragment;
import com.fitbuilder.productbrowser.fragments.ProductDetailFragment;
import com.fitbuilder.productbrowser.fragments.ProductsListFragment;
import com.fitbuilder.productbrowser.lib.UserFunctions;
import com.fitbuilder.productbrowser.models.Category;
import com.fitbuilder.productbrowser.models.Product;


public class MainActivity extends ActionBarActivity implements
        ProductsListFragment.ProductsListCallbacks,
        CategoriesListFragment.CategoryListCallbacks,
        FragmentManager.OnBackStackChangedListener, View.OnClickListener {

    private final static int REQUEST_CODE_LOGIN = 1;

    private boolean mobileLayout = false;

    private Button actionsButton,
            categoriesButton;

    private CategoriesListFragment categoriesListFragment;

    private ProductsListFragment productListFragment;

    private Category selectedCategory;

    private Menu actionMenu;

    private boolean showLogout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("FBLog", "MAIN ACTIVITY ONCREATE");

        FragmentManager manager = getSupportFragmentManager();

        /*if (findViewById(R.id.mobileFrameLayout) != null)*/ {
            mobileLayout = true;
            if (savedInstanceState == null) {
                UserFunctions userFunctions = new UserFunctions();
                userFunctions.logoutUser(getApplicationContext());
                productListFragment = new ProductsListFragment();
                manager.beginTransaction().add(R.id.mobileFrameLayout, productListFragment).commit();
                getSupportFragmentManager().addOnBackStackChangedListener(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                boolean result = data.getBooleanExtra("USER_LOGGED_IN", false);
                this.showLogout = result;
                invalidateOptionsMenu();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.textMsgLoggedIn), Toast.LENGTH_SHORT).show();
            }
            else {
                this.showLogout = false;
                invalidateOptionsMenu();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("FBLog", "MAIN ACTIVITY OnSTART");
        UserFunctions userFunctions = new UserFunctions();
        if (userFunctions.isLoggedIn(getApplicationContext())) {
            Log.d("FBLog", "MAIN ACTIVITY OnSTART - LoggedIn");
            this.showLogout = true;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        boolean canBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);

        if (actionMenu != null) {
            actionMenu.setGroupVisible(R.id.main_group_sort, !canBack);
        }

        if (!canBack) {
            if (selectedCategory != null)
                setTitle(selectedCategory.getName());
            else
                setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*this.actionMenu = menu;
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);*/

        MenuItem mLogin = menu.findItem(R.id.action_login);
        MenuItem mLogout = menu.findItem(R.id.action_logout);
        MenuItem mUser = menu.findItem(R.id.action_user);

        if (mLogin != null) {
            mLogin.setVisible(!showLogout);
        }
        if (mLogout != null) {
//            mLogout.setVisible(showLogout);
            mUser.setVisible(showLogout);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_login:
                Log.d("FBLog", "START LOGIN");
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
                break;
            case R.id.action_logout:
                Log.d("FBLog", "LOGOUT CLICKED");
                new UserFunctions().logoutUser(getApplicationContext());
                this.showLogout = false;
                invalidateOptionsMenu();
                break;
            case R.id.action_refresh:
                productListFragment.refreshList();
                break;
            case R.id.menu_sort_by_name_asc:
                productListFragment.changeSort(Globals.ProductSort.NAME_ASC);
            break;
            case R.id.menu_sort_by_name_desc:
                productListFragment.changeSort(Globals.ProductSort.NAME_DESC);
            break;
            case R.id.menu_sort_by_price_asc:
                productListFragment.changeSort(Globals.ProductSort.PRICE_ASC);
            break;
            case R.id.menu_sort_by_price_desc:
                productListFragment.changeSort(Globals.ProductSort.PRICE_DESC);
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStackImmediate();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.actionMenu = menu;
        //MenuInflater menuInflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void productSelect(Product product) {
        if (mobileLayout) {
            ProductDetailFragment detailFragment = new ProductDetailFragment();

            Bundle args = new Bundle();
            args.putSerializable("PRODUCT", product);

            replaceFragment(detailFragment, args);

            setTitle(product.getName());
        }
    }

    @Override
    public void categorySelected(Category category) {
        try {
            selectedCategory = category;
            productListFragment.refreshList(selectedCategory);
            getSupportFragmentManager().popBackStackImmediate();
        }
        catch (Exception e) {
            Log.d("FBLog", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCategories:
                if (categoriesListFragment == null) {
                    categoriesListFragment = new CategoriesListFragment();
                }
                replaceFragment(categoriesListFragment, null);
                break;
        }
    }

    private void replaceFragment(Fragment fragment, Bundle args) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        fragment.setArguments(args);

        ft.replace(R.id.mobileFrameLayout, fragment, "Fragment" + fragment.getId());
        ft.addToBackStack("Fragment" + fragment.getId());
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
    }
}
