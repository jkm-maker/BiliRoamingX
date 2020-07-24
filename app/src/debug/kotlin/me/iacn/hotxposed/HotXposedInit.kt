package me.iacn.hotxposed

import android.content.Context
import dalvik.system.PathClassLoader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import me.iacn.biliroaming.BuildConfig
import me.iacn.biliroaming.Constant
import me.iacn.biliroaming.utils.*
import java.io.File

/**
 * Created by iAcn on 2019/3/25
 * Email i@iacn.me
 */
class HotXposedInit : IXposedHookLoadPackage {

    companion object {
        private const val REAL_XPOSED_INIT = "me.iacn.biliroaming.XposedInit"
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        disableModulesUpdatedNotification(lpparam)
        if (!Constant.BILIBILI_PACKAGENAME.contains(lpparam.packageName) && lpparam.packageName != BuildConfig.APPLICATION_ID) return

        val moduleApkFile = getModuleApkFile()
        if (moduleApkFile?.exists() != true) return

        val classLoader = PathClassLoader(moduleApkFile.absolutePath, lpparam::class.java.classLoader)
        classLoader.loadClass(REAL_XPOSED_INIT)?.new()?.run{
            val param = StartupParam::class.java.new() as StartupParam
            param.modulePath = moduleApkFile.absolutePath
            param.startsSystemServer = false
            callMethod("initZygote", param)
            callMethod("handleLoadPackage", lpparam)
        }
    }

    private fun disableModulesUpdatedNotification(lpparam: LoadPackageParam) {
        if ("de.robv.android.xposed.installer" == lpparam.packageName) {
            "de.robv.android.xposed.installer.util.NotificationUtil".hookMethod(lpparam.classLoader,
                    "showModulesUpdatedNotification", XC_MethodReplacement.DO_NOTHING)
        }
    }

    private fun getModuleApkFile(): File? {
        val activityThread = "android.app.ActivityThread".findClassOrNull(null)?.callStaticMethod("currentActivityThread")
        val context = activityThread?.callMethodAs<Context>("getSystemContext")
        val applicationInfo = context?.packageManager?.getApplicationInfo(BuildConfig.APPLICATION_ID, 0)
                ?: return null
        return File(applicationInfo.sourceDir)
    }
}
