package com.technotrade.pts2.pts2testapp.gui.activity;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.StringProvider;
import com.technotrade.pts2.pts2testapp.databinding.ActivityMainBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.MainViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.MainViewModelFactory;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;

import java.util.Objects;

public class MainActivity extends BaseActivity<MainViewModel> implements StringProvider {
    private ActivityMainBinding mBinding;
    private MainViewModel mViewModel;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private Button bConnect;
    private Button bSettings;
    private Button bExtra;
    private AnimationDrawable mProgressDrawable;

    public MainActivity() {
        mProgressDrawable = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen.installSplashScreen(this);

        mViewModel = new ViewModelProvider(this, getDefaultViewModelProviderFactory())
            .get(getViewModelClass());

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        mNavController = navHostFragment.getNavController();
        mAppBarConfiguration = new AppBarConfiguration.Builder(mNavController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        mBinding.toolbar.inflateMenu(R.menu.menu_main);
        mBinding.toolbar.setOnMenuItemClickListener(item -> {
            mNavController.navigate(R.id.action_global_to_extraFragment);
            return true;
        });
    }

    @NonNull
    @Override
    public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        if (getClass() == MainActivity.class) {
            return new MainViewModelFactory(this); // your custom factory
        }
        return super.getDefaultViewModelProviderFactory();
    }

    @Override
    protected Class<MainViewModel> getViewModelClass() {
        return MainViewModel.class;
    }

    @Override
    protected boolean execModelViewCommand(EventCommand<?> eventCommand) {
        if (eventCommand == null) return false;

        String command = eventCommand.getCommand();
        Object data = eventCommand.getData();

        switch (ViewModelCommand.valueOf(command)) {
            case showError:
                DialogHelper.showDialogWarning(
                    this,
                    getSupportFragmentManager().getPrimaryNavigationFragment(),
                    getResources().getString(R.string.error),
                    (String) data
                );
                return true;

            case showConnectedButton:
                showConnectedButton();
                return true;

            case showDisconnectedButton:
                showDisconnectedButton();
                return true;

            case showConnectingProgress:
                if (data == null) {
                    showConnectingProgress();
                } else {
                    showConnectingProgress((Boolean) data);
                }
                return true;

            case setSettingsButtonEnabled:
                bSettings.setEnabled((boolean) data);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mViewModel != null) {
            mViewModel.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mViewModel != null) {
            mViewModel.onStop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem iConnectItem = menu.findItem(R.id.iConnect);
        bConnect = Objects.requireNonNull(iConnectItem.getActionView()).findViewById(R.id.button);
        assert bConnect != null;
        Drawable connectButtonDrawable = ResourcesCompat.getDrawable(this.getResources(), R.drawable.selector_button_blue, null);
        bConnect.setBackground(connectButtonDrawable);
        bConnect.setText(R.string.connect);
        bConnect.setTextSize(9);
        bConnect.setTextColor(Color.WHITE);
        showDisconnectedButton();
        mViewModel.getConnectButtonEnabled().observe(this, bConnect::setEnabled);
        bConnect.setOnClickListener(v -> mViewModel.onConnectClicked());

        MenuItem iSettingsItem = menu.findItem(R.id.iSettings);
        bSettings = Objects.requireNonNull(iSettingsItem.getActionView()).findViewById(R.id.button);
        assert bSettings != null;
        Drawable settingsButtonDrawable = ResourcesCompat.getDrawable(this.getResources(), R.drawable.selector_button_blue, null);
        bSettings.setBackground(settingsButtonDrawable);
        Drawable settingsIconDrawable = ResourcesCompat.getDrawable(this.getResources(), R.drawable.settings, null);
        bSettings.setCompoundDrawablesWithIntrinsicBounds(null, null, settingsIconDrawable, null);
        bSettings.setText(R.string.connection_settings);
        bSettings.setTextSize(9);
        bSettings.setTextColor(Color.WHITE);
        mViewModel.getSettingsButtonEnabled().observe(this, bSettings::setEnabled);
        bSettings.setOnClickListener(view -> mNavController.navigate(R.id.action_global_to_settingsFragment));

        MenuItem iExtraItem = menu.findItem(R.id.iExtra);
        bExtra = Objects.requireNonNull(iExtraItem.getActionView()).findViewById(R.id.button);
        assert bExtra != null;
        bExtra.setText(R.string.extra_fragment_label);
        bExtra.setTextSize(9);
        bExtra.setTextColor(Color.WHITE);
        bExtra.setOnClickListener(view -> mNavController.navigate(R.id.action_global_to_extraFragment));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getOnBackPressedDispatcher().onBackPressed();
                return true;
            default:
                if (mNavController != null) {
                    return NavigationUI.onNavDestinationSelected(item, mNavController)
                        || super.onOptionsItemSelected(item);
                } else {
                    return super.onOptionsItemSelected(item);
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
            || super.onSupportNavigateUp();
    }

    @Override
    public String getResourceString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigateTo(R.id.pumpsFragment);
    }

    public void navigateTo(int actionId) {
        mNavController.navigate(actionId);
    }

    public void navigateTo(int actionId, Bundle args) {
        mNavController.navigate(actionId, args);
    }

    public void navigateTo(int actionId, Bundle args, NavOptions navOptions) {
        mNavController.navigate(actionId, args, navOptions);
    }

    public void navigateTo(int actionId, Bundle args, NavOptions navOptions, FragmentNavigator.Extras extras) {
        mNavController.navigate(actionId, args, navOptions, extras);
    }

    public void showConnectedButton() {
        showConnectingProgress(false);
        assert bConnect != null;
        runOnUiThread(() -> {
            bConnect.setText(R.string.disconnect);
            bConnect.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ledgreenblack, 0);
        });
    }

    public void showDisconnectedButton() {
        showConnectingProgress(false);
        assert bConnect != null;
        runOnUiThread(() -> {
            bConnect.setText(R.string.connect);
            bConnect.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ledredblack, 0);
        });
    }

    public void showConnectingProgress() {
        showConnectingProgress(true);
    }

    public void showConnectingProgress(boolean enable) {
        if (!enable) {
            runOnUiThread(() -> {
                if (mProgressDrawable != null && mProgressDrawable.isRunning()) {
                    mProgressDrawable.setVisible(false, false);
                    mProgressDrawable.stop();
                    mProgressDrawable = null;
                }
            });
            return;
        }

        if (mProgressDrawable == null) {
            mProgressDrawable = (AnimationDrawable) AppCompatResources.getDrawable(this, R.drawable.progress_animated);
        }

        assert bConnect != null;
        runOnUiThread(() -> {
            bConnect.setCompoundDrawablesWithIntrinsicBounds(null, null, mProgressDrawable, null);
            mProgressDrawable.setVisible(true, true);
            mProgressDrawable.start();
        });
    }
}