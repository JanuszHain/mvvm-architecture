package pl.applover.architecture.mvvm.util.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import pl.applover.architecture.mvvm.App
import java.util.concurrent.TimeUnit


/**
 * Created by Janusz Hain on 2018-01-08.
 */

fun Any.equalsAnyOf(vararg args: Any?): Boolean {
    args.forEach {
        if (this == it)
            return true
    }
    return false
}

fun showToast(rId: Int, isLong: Boolean = true, context: Context = App.instance) {
    Toast.makeText(context, rId, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun showToast(text: String, isLong: Boolean = true, context: Context = App.instance) {
    Toast.makeText(context, text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

/**
 * Important for apps with changing language!!!
 * If you change language in the app (not using language defined by system) in the activity, then you have to use activity context,
 * otherwise this will not return correct language strings!
 */
fun getString(resId: Int, context: Context = App.instance) = context.getString(resId)!!

/**
 * Important for apps with changing language!!!
 * If you change language in the app (not using language defined by system) in the activity, then you have to use activity context,
 * otherwise this will not return correct language strings!
 *
 * Put %s in string resource for string placeholder etc
 */
fun getString(resId: Int, vararg formatArgs: Any, context: Context = App.instance) = context.getString(resId, *formatArgs)!!

fun getColor(resId: Int, context: Context = App.instance) = ContextCompat.getColor(context, resId)

fun getInteger(resId: Int, context: Context = App.instance) = context.resources.getInteger(resId)

fun getDrawable(resId: Int, context: Context = App.instance) = ContextCompat.getDrawable(context, resId)

fun TimeUnit.delayed(delay: Long, closure: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(closure, this.toMillis(delay))
}

infix fun Int.with(x: Int) = this.or(x)

fun <T : Activity> Activity.goToActivity(className: Class<T>, saveActivityOnBackstack: Boolean = true, bundle: Bundle? = null) {
    val intent = Intent(this, className).apply {
        bundle?.let {
            putExtras(it)
        }
    }

    if (!saveActivityOnBackstack) {
        intent.flags = intent.flags with Intent.FLAG_ACTIVITY_NO_HISTORY
    }

    startActivity(intent)
}

fun <T : Activity> Activity.goToActivityAndClearActivityBackstack(className: Class<T>, saveActivityOnBackstack: Boolean = true, bundle: Bundle? = null) {
    val intent = Intent(this, className).apply {
        bundle?.let {
            putExtras(it)
        }
    }

    intent.flags = intent.flags with Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

    if (!saveActivityOnBackstack) {
        intent.flags = intent.flags with Intent.FLAG_ACTIVITY_NO_HISTORY
    }

    startActivity(intent)
}

fun AppCompatActivity.showFragment(fragment: Fragment, into: Int, push: Boolean = true, animIn: Int? = android.R.anim.fade_in, animOut: Int? = android.R.anim.fade_out, tag: String? = null) {
    if (push) {
        supportFragmentManager.beginTransaction()
                .addToBackStack(tag)
                .setCustomAnimations(
                        animIn ?: 0,
                        animOut ?: 0)
                .replace(into, fragment)
                .commit()
    } else {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                        animIn ?: 0,
                        animOut ?: 0)
                .replace(into, fragment)
                .commit()
    }
}

fun Fragment.showNestedFragment(fragment: Fragment, into: Int, push: Boolean = true, animIn: Int? = android.R.anim.fade_in, animOut: Int? = android.R.anim.fade_out, tag: String? = null) {
    if (push) {
        childFragmentManager.beginTransaction()
                .addToBackStack(tag)
                .setCustomAnimations(
                        animIn ?: 0,
                        animOut ?: 0)
                .replace(into, fragment)
                .commitAllowingStateLoss()
    } else {
        childFragmentManager.beginTransaction()
                .setCustomAnimations(
                        animIn ?: 0,
                        animOut ?: 0)
                .replace(into, fragment)
                .commitAllowingStateLoss()
    }
}

fun AppCompatActivity.addWorkerFragmentIfNotExists(fragment: Fragment, tag: String) {
    if (supportFragmentManager.findFragmentByTag(tag) == null) {
        supportFragmentManager.beginTransaction()
                .add(fragment, tag)
                .commitNowAllowingStateLoss()
    }
}

fun AppCompatActivity.removeWorkerFragment(tag: String) {
    supportFragmentManager.findFragmentByTag(tag)?.let {
        supportFragmentManager.beginTransaction()
                .remove(it)
                .commitNowAllowingStateLoss()
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> FragmentManager.onStackTop(closure: T.() -> Unit, onFailure: (() -> Unit)? = null) {
    (peekBackStack() as? T)?.closure() ?: onFailure?.invoke()
}

fun FragmentManager.peekBackStack() =
        if (backStackEntryCount > 0) {
            getBackStackEntryAt(backStackEntryCount - 1).name?.let {
                findFragmentByTag(it)
            }
        } else null


fun FragmentManager.clearBackstack() {
    if (backStackEntryCount > 0) {
        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}

fun FragmentManager.popChildFragment(): Boolean {
    for (frag in this.fragments) {
        if (frag.isVisible) {
            val childFm = frag.childFragmentManager
            if (childFm.backStackEntryCount > 0) {
                childFm.popBackStack()
                return true
            }
        }
    }

    return false
}

fun FragmentManager.popFragment(): Boolean {

    if (this.backStackEntryCount > 0) {
        this.popBackStackImmediate()
        return true
    }

    return false
}

fun DialogFragment.showSingle(fragmentManager: FragmentManager?, tag: String) {
    fragmentManager?.findFragmentByTag(tag)?.let {
        return
    }
    show(fragmentManager, tag)
}

fun isEmailValid(target: CharSequence): Boolean {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

fun Activity.reload() {
    finish()
    startActivity(intent)
}