package com.technotrade.pts2.pts2testapp.helper;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.technotrade.pts2.pts2testapp.enumeration.KeyboardType;
import com.technotrade.pts2.pts2testapp.gui.fragment.IKeyboard;
import com.technotrade.pts2.pts2testapp.gui.fragment.KeyboardAmountFragment;
import com.technotrade.pts2.pts2testapp.gui.fragment.KeyboardOrderConfirmationFragment;
import com.technotrade.pts2.pts2testapp.gui.fragment.KeyboardOrderInFormationFragment;
import com.technotrade.pts2.pts2testapp.gui.fragment.KeyboardOrderedFragment;
import com.technotrade.pts2.pts2testapp.gui.fragment.KeyboardVolumeFragment;

import java.util.Stack;

public class KeyboardHelper {
    private final KeyboardOrderInFormationFragment mKeyboardOrderInFormationFragment = new KeyboardOrderInFormationFragment();
    private final KeyboardVolumeFragment mKeyboardVolumeFragment = new KeyboardVolumeFragment();
    private final KeyboardAmountFragment mKeyboardAmountFragment = new KeyboardAmountFragment();
    private final KeyboardOrderConfirmationFragment mKeyboardOrderConfirmationFragment = new KeyboardOrderConfirmationFragment();
    private final KeyboardOrderedFragment mKeyboardOrderedFragment = new KeyboardOrderedFragment();

    private static final String TAG = "Keyboard";

    private final Stack<KeyboardType> mFragmentStack = new Stack<>();
    private int mCurrentContainerViewId = -1;
    private FragmentManager mCurrentFragmentManager = null;
    private KeyboardType mCurrentVisibleType = null;

    public void showKeyboardFragment(@IdRes int containerViewId, FragmentManager fm, KeyboardType type) {
        mCurrentContainerViewId = containerViewId;
        mCurrentFragmentManager = fm;

        replaceFragment(fm, getFragmentByType(type));
        mCurrentVisibleType = type;
        mFragmentStack.push(type);
    }

    public void replaceWithKeyboardFragment(@IdRes int containerViewId, FragmentManager fm, KeyboardType type) {
        clearStack();
        mCurrentContainerViewId = containerViewId;
        mCurrentFragmentManager = fm;

        replaceFragment(fm, getFragmentByType(type));
        mCurrentVisibleType = type;
        mFragmentStack.push(type);
    }

    public boolean popKeyboardFragment() {
        if (mFragmentStack.isEmpty() || mCurrentFragmentManager == null) {
            return false;
        }

        clearFragmentByType(mFragmentStack.peek());
        mFragmentStack.pop();

        if (mFragmentStack.isEmpty()) {
            hideAndRemoveKeyboard(mCurrentFragmentManager);
            return true;
        }

        KeyboardType previous = mFragmentStack.peek();
        replaceFragment(mCurrentFragmentManager, getFragmentByType(previous));
        mCurrentVisibleType = previous;
        return true;
    }

    public void hideAndRemoveKeyboard(FragmentManager fm) {
        if (mCurrentVisibleType != null) {
            clearFragmentByType(mCurrentVisibleType);
        }
        removeFragment(fm);
        mFragmentStack.clear();
        mCurrentVisibleType = null;
    }

    public void clearStack() {
        clearAllFragments();

        if (mCurrentFragmentManager != null) {
            hideAndRemoveKeyboard(mCurrentFragmentManager);
        }

        mFragmentStack.clear();
        mCurrentVisibleType = null;
    }

    public boolean hasFragmentsInStack() {
        return !mFragmentStack.isEmpty();
    }

    public int getStackSize() {
        return mFragmentStack.size();
    }

    private void replaceFragment(FragmentManager fm, Fragment fragment) {
        if (fragment == null) return;
        removeFragment(fm);
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.add(mCurrentContainerViewId, fragment, TAG);
        ft.commit();
    }

    private void removeFragment(FragmentManager fm) {
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(prev);
            ft.commitNow();
        }
    }

    private Fragment getFragmentByType(KeyboardType type) {
        switch (type) {
            case ORDER_IN_FORMATION: return mKeyboardOrderInFormationFragment;
            case VOLUME: return mKeyboardVolumeFragment;
            case AMOUNT: return mKeyboardAmountFragment;
            case ORDER_CONFIRMATION: return mKeyboardOrderConfirmationFragment;
            case ORDERED: return mKeyboardOrderedFragment;
            default: return null;
        }
    }

    private void clearFragmentByType(KeyboardType type) {
        Fragment f = getFragmentByType(type);
        if (f instanceof IKeyboard) {
            ((IKeyboard) f).clear();
        }
    }

    public void clearAllFragments() {
        if (mCurrentFragmentManager == null) return;

        for (KeyboardType type : KeyboardType.values()) {
            Fragment fragment = getFragmentByType(type);
            if (fragment != null && fragment.isAdded()) {
                if (fragment instanceof IKeyboard) {
                    ((IKeyboard) fragment).clear();
                }
            }
        }
    }
}