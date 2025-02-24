@file:Suppress("DEPRECATION")

package me.iacn.biliroaming

import android.app.Activity
import android.app.AndroidAppHelper
import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.text.style.ClickableSpan
import android.text.style.LineBackgroundSpan
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import dalvik.system.BaseDexClassLoader
import me.iacn.biliroaming.utils.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import kotlin.math.max
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


infix fun Configs.Class.from(cl: ClassLoader) = if (hasName()) name.findClassOrNull(cl) else null
val Configs.Method.orNull get() = if (hasName()) name else null
val Configs.Field.orNull get() = if (hasName()) name else null

val hookInfo get() = BiliBiliPackage.instance.hookInfo

class BiliBiliPackage constructor(private val mClassLoader: ClassLoader, mContext: Context) {
    init {
        instance = this
    }

    lateinit var globalDexHelper: DexHelper

    @OptIn(ExperimentalTime::class)
    private val mHookInfo: Configs.HookInfo = run {
        val (result, time) = measureTimedValue { readHookInfo(mContext) }
        Log.d("load hookinfo $time")
        Log.d(result.copy { clearMapIds() }.print())
        result
    }
    val hookInfo get() = mHookInfo
    val bangumiApiResponseClass by Weak { mHookInfo.bangumiApiResponse from mClassLoader }
    val rxGeneralResponseClass by Weak { "com.bilibili.okretro.call.rxjava.RxGeneralResponse" from mClassLoader }
    val fastJsonClass by Weak { mHookInfo.fastJson.class_ from mClassLoader }
    val bangumiUniformSeasonClass by Weak { mHookInfo.bangumiSeason from mClassLoader }
    val sectionClass by Weak { mHookInfo.section.class_ from mClassLoader }
    val retrofitResponseClass by Weak { mHookInfo.retrofitResponse from mClassLoader }
    val themeHelperClass by Weak { mHookInfo.themeHelper.class_ from mClassLoader }
    val themeIdHelperClass by Weak { mHookInfo.themeIdHelper.class_ from mClassLoader }
    val columnHelperClass by Weak { mHookInfo.columnHelper.class_ from mClassLoader }
    val settingRouterClass by Weak { mHookInfo.settings.settingRouter from mClassLoader }
    val themeListClickClass by Weak { mHookInfo.themeListClick from mClassLoader }
    val shareWrapperClass by Weak { mHookInfo.shareWrapper.class_ from mClassLoader }
    val themeNameClass by Weak { mHookInfo.themeName.class_ from mClassLoader }
    val themeProcessorClass by Weak { mHookInfo.themeProcessor.class_ from mClassLoader }
    val drawerClass by Weak { mHookInfo.drawer.class_ from mClassLoader }
    val generalResponseClass by Weak { mHookInfo.generalResponse from mClassLoader }
    val seasonParamsMapClass by Weak { "com.bilibili.bangumi.data.page.detail.BangumiDetailApiService\$UniformSeasonParamsMap" from mClassLoader }
    val seasonParamsClass by Weak { mHookInfo.bangumiParams.class_ from mClassLoader }
    val brandSplashClass by Weak { "tv.danmaku.bili.ui.splash.brand.ui.BaseBrandSplashFragment" from mClassLoader }
    val urlConnectionClass by Weak { "com.bilibili.lib.okhttp.huc.OkHttpURLConnection" from mClassLoader }
    val downloadThreadListenerClass by Weak { mHookInfo.downloadThread.listener from mClassLoader }
    val downloadThreadViewHostClass by Weak { mHookInfo.downloadThread.viewHost from mClassLoader }
    val reportDownloadThreadClass by Weak { mHookInfo.downloadThread.reportDownload.class_ from mClassLoader }
    val libBiliClass by Weak { mHookInfo.signQuery.class_ from mClassLoader }
    val splashActivityClass by Weak {
        "tv.danmaku.bili.ui.splash.SplashActivity" from mClassLoader
            ?: "tv.danmaku.bili.MainActivityV2" from mClassLoader
    }
    val mainActivityClass by Weak { "tv.danmaku.bili.MainActivityV2" from mClassLoader }
    val homeUserCenterClass by Weak { if (mHookInfo.settings.homeUserCenterCount == 1) mHookInfo.settings.homeUserCenterList.first().class_ from mClassLoader else null }
    val musicNotificationHelperClass by Weak { mHookInfo.musicNotification.helper from mClassLoader }
    val liveNotificationHelperClass by Weak { mHookInfo.musicNotification.liveHelper from mClassLoader }
    val notificationBuilderClass by Weak { mHookInfo.musicNotification.builder.class_ from mClassLoader }
    val absMusicServiceClass by Weak { mHookInfo.musicNotification.absMusicService from mClassLoader }
    val menuGroupItemClass by Weak { mHookInfo.settings.menuGroupItem from mClassLoader }
    val drawerLayoutClass by Weak { mHookInfo.drawer.layout from mClassLoader }
    val drawerLayoutParamsClass by Weak { mHookInfo.drawer.layoutParams from mClassLoader }
    val splashInfoClass by Weak {
        "tv.danmaku.bili.ui.splash.brand.BrandShowInfo" from mClassLoader
            ?: "tv.danmaku.bili.ui.splash.brand.model.BrandShowInfo" from mClassLoader
    }
    val commentCopyClass by Weak { mHookInfo.commentLongClick from mClassLoader }
    val commentCopyNewClass by Weak { mHookInfo.commentLongClickNew from mClassLoader }
    val kotlinJsonClass by Weak { "kotlinx.serialization.json.Json" from mClassLoader }
    val gsonConverterClass by Weak { mHookInfo.gsonHelper.gsonConverter from mClassLoader }
    val playerCoreServiceV2Class by Weak { mHookInfo.playerCoreService.class_ from mClassLoader }
    val teenagersModeDialogActivityClass by Weak {
        "com.bilibili.teenagersmode.ui.TeenagersModeDialogActivity" from mClassLoader
    }
    val pegasusFeedClass by Weak { mHookInfo.pegasusFeed.class_ from mClassLoader }
    val popularClass by Weak { mHookInfo.popular.class_ from mClassLoader }
    val subtitleSpanClass by Weak { mHookInfo.subtitleSpan from mClassLoader }
    val chronosSwitchClass by Weak { mHookInfo.chronosSwitch from mClassLoader }
    val biliSpaceClass by Weak { "com.bilibili.app.authorspace.api.BiliSpace" from mClassLoader }
    val biliVideoDetailClass by Weak {
        "tv.danmaku.bili.ui.video.api.BiliVideoDetail" from mClassLoader
            ?: "tv.danmaku.bili.videopage.data.view.model.BiliVideoDetail" from mClassLoader
    }
    val commentSpanTextViewClass by Weak { mHookInfo.commentSpan from mClassLoader }
    val commentSpanEllipsisTextViewClass by Weak { "com.bilibili.app.comm.comment2.widget.CommentSpanEllipsisTextView" from mClassLoader }
    val liveRoomActivityClass by Weak { "com.bilibili.bililive.room.ui.roomv3.LiveRoomActivityV3" from mClassLoader }
    val liveKvConfigHelperClass by Weak { "com.bilibili.bililive.tec.kvcore.LiveKvConfigHelper" from mClassLoader }
    val storyVideoActivityClass by Weak { "com.bilibili.video.story.StoryVideoActivity" from mClassLoader }
    val okioWrapperClass by Weak { mHookInfo.okio.class_ from mClassLoader }
    val ellipsizingTextViewClass by Weak { "com.bilibili.bplus.followingcard.widget.EllipsizingTextView" from mClassLoader }
    val setLineToAllCountMethod get() = mHookInfo.setLineToAllCount.orNull
    val shareClickResultClass by Weak { "com.bilibili.lib.sharewrapper.online.api.ShareClickResult" from mClassLoader }
    val backgroundPlayerClass by Weak { mHookInfo.musicNotification.backgroundPlayer from mClassLoader }
    val playerServiceClass by Weak { mHookInfo.musicNotification.playerService from mClassLoader }
    val mediaSessionCallbackClass by Weak { mHookInfo.musicNotification.mediaSessionCallback from mClassLoader }
    val playerOnSeekCompleteClass by Weak { mHookInfo.playerCoreService.seekCompleteListener from mClassLoader }
    val musicBackgroundPlayerClass by Weak { mHookInfo.musicNotification.musicBackgroundPlayer from mClassLoader }
    val kanbanCallbackClass by Weak { mHookInfo.kanBan.class_ from mClassLoader }
    val toastHelperClass by Weak { mHookInfo.toastHelper.class_ from mClassLoader }
    val upgradeUtilsClass by Weak { mHookInfo.appUpgrade.upgradeUtils from mClassLoader }
    val writeChannelMethod get() = mHookInfo.appUpgrade.writeChannel.orNull
    val writeInfoMethod get() = mHookInfo.appUpgrade.writeInfo.orNull
    val cleanApkDirMethod get() = mHookInfo.appUpgrade.cleanApkDir.orNull
    val helpFragmentClass by Weak { "com.bilibili.app.preferences.fragment.HelpFragment" from mClassLoader }
    val supplierClass by Weak { mHookInfo.appUpgrade.supplier from mClassLoader }
    val checkMethod get() = mHookInfo.appUpgrade.check.orNull
    val upgradeInfoClass by Weak { mHookInfo.appUpgrade.upgradeInfo from mClassLoader }
    val versionExceptionClass by Weak { mHookInfo.appUpgrade.versionException from mClassLoader }
    val userFragmentClass by Weak { "tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment" from mClassLoader }
    val themeUtilsClass by Weak { mHookInfo.darkSwitch.themeUtils from mClassLoader }
    val switchDarkModeMethod get() = mHookInfo.darkSwitch.switchDarkMode.orNull
    val isDarkFollowSystemMethod get() = mHookInfo.darkSwitch.isDarkFollowSystem.orNull
    val toolbarServiceClass by Weak { mHookInfo.toolbarService.class_ from mClassLoader }
    val miniPlayMethod get() = mHookInfo.toolbarService.miniPlay.orNull
    val superMenuClass by Weak { mHookInfo.superMenu.class_ from mClassLoader }
    val menuItemClickListenerClass by Weak { mHookInfo.superMenu.menuItemClickListener from mClassLoader }
    val menuItemImplClass by Weak { mHookInfo.superMenu.menuItemImpl from mClassLoader }
    val showMethod get() = mHookInfo.superMenu.show.orNull
    val clickListenerField get() = mHookInfo.superMenu.clickListener.orNull
    val blkvClass by Weak { mHookInfo.blkv.class_ from mClassLoader }
    val biliAccountsClass by Weak { mHookInfo.biliAccounts.class_ from mClassLoader }
    val networkExceptionClass by Weak { "com.bilibili.lib.moss.api.NetworkException" from mClassLoader }
    val brotliInputStreamClass by Weak { mHookInfo.brotliInputStream from mClassLoader }
    val commentInvalidFragmentClass by Weak {
        "com.bilibili.bangumi.ui.page.detail.OGVCommentFragment".from(mClassLoader)
            ?: "com.bilibili.bangumi.ui.page.detail.BangumiCommentInvalidFragmentV2"
                .from(mClassLoader)
    }
    val playerQualityServiceClass by Weak { "com.bilibili.playerbizcommon.features.quality.PlayerQualityService" from mClassLoader }
    val mossResponseHandlerClass by Weak { "com.bilibili.lib.moss.api.MossResponseHandler" from mClassLoader }
    val projectionPlayUrlClass by Weak { "com.bilibili.lib.projection.internal.api.model.ProjectionPlayUrl" from mClassLoader }
    val okioClass by Weak { mHookInfo.okio2.class_ from mClassLoader }
    val bufferedSourceClass by Weak { mHookInfo.okio2.bufferedSource from mClassLoader }
    val realCallClass by Weak { mHookInfo.okHttp.realCall.class_ from mClassLoader }
    val responseClass by Weak { mHookInfo.okHttp.response.class_ from mClassLoader }
    val responseBodyClass by Weak { mHookInfo.okHttp.responseBody.class_ from mClassLoader }
    val mediaTypeClass by Weak { mHookInfo.okHttp.mediaType.class_ from mClassLoader }
    val callbackClass by Weak { mHookInfo.okHttp.callback from mClassLoader }
    val biliCallClass by Weak { mHookInfo.biliCall.class_ from mClassLoader }
    val parserClass by Weak { mHookInfo.biliCall.parser from mClassLoader }
    val livePagerRecyclerViewClass by Weak { mHookInfo.livePagerRecyclerView from mClassLoader }
    val cronCanvasClass by Weak { "com.bilibili.cron.Canvas" from mClassLoader }
    val subtitleConfigGetClass by Weak { "tv.danmaku.biliplayerv2.service.interact.biz.chronos.chronosrpc.methods.receive.GetDanmakuConfig\$SubtitleConfig" from mClassLoader }
    val subtitleConfigChangeClass by Weak { "tv.danmaku.biliplayerv2.service.interact.biz.chronos.chronosrpc.methods.send.DanmakuConfigChange\$SubtitleConfig" from mClassLoader }
    val liveRoomPlayerViewClass by Weak { "com.bilibili.bililive.room.ui.roomv3.player.container.LiveRoomPlayerContainerView" from mClassLoader }
    val biliConfigClass by Weak { mHookInfo.biliConfig.class_ from mClassLoader }
    val updateInfoSupplierClass by Weak { mHookInfo.updateInfoSupplier.class_ from mClassLoader }
    val latestVersionExceptionClass by Weak { "tv.danmaku.bili.update.internal.exception.LatestVersionException" from mClassLoader }
    val playerPreloadHolderClass by Weak { mHookInfo.playerPreloadHolder.class_ from mClassLoader }
    val playerSettingHelperClass by Weak { mHookInfo.playerSettingHelper.class_ from mClassLoader }
    val liveRtcEnableClass by Weak { mHookInfo.liveRtcHelper.liveRtcEnableClass from mClassLoader }
    val playURLMossClass by Weak { "com.bapis.bilibili.app.playurl.v1.PlayURLMoss" from mClassLoader }
    val playViewReqClass by Weak { "com.bapis.bilibili.app.playurl.v1.PlayViewReq" from mClassLoader }
    val playerMossClass by Weak { "com.bapis.bilibili.app.playerunite.v1.PlayerMoss" from mClassLoader }
    val playViewUniteReqClass by Weak { "com.bapis.bilibili.app.playerunite.v1.PlayViewUniteReq" from mClassLoader }
    val viewMossClass by Weak { "com.bapis.bilibili.app.view.v1.ViewMoss" from mClassLoader }
    val viewReqClass by Weak { "com.bapis.bilibili.app.view.v1.ViewReq" from mClassLoader }
    val bkArcPartClass by Weak { "com.bapis.bilibili.app.listener.v1.BKArcPart" from mClassLoader }
    val builtInThemesClass by Weak { mHookInfo.builtInThemes.class_ from mClassLoader }
    val themeColorsConstructor by Weak {
        mHookInfo.themeColors.from(mClassLoader)?.declaredConstructors?.firstOrNull { it.isPrivate }
            ?.apply { isAccessible = true }
    }
    val biliGlobalPreferenceClass by Weak { mHookInfo.biliGlobalPreference.class_ from mClassLoader }
    val dmMossClass by Weak { "com.bapis.bilibili.community.service.dm.v1.DMMoss" from mClassLoader }
    val treePointItemClass by Weak { "com.bilibili.app.comm.list.common.data.ThreePointItem" from mClassLoader }
    val dislikeReasonClass by Weak { "com.bilibili.app.comm.list.common.data.DislikeReason" from mClassLoader }
    val cardClickProcessorClass by Weak { mHookInfo.cardClickProcessor.class_ from mClassLoader }
    val publishToFollowingConfigClass by Weak { mHookInfo.publishToFollowingConfig from mClassLoader }
    val imageFragmentClass by Weak { "com.bilibili.lib.imageviewer.fragment.ImageFragment" from mClassLoader }
    val unknownFieldSetLiteInstance by Weak {
        "com.google.protobuf.UnknownFieldSetLite".from(mClassLoader)
            ?.callStaticMethod("getDefaultInstance")
    }

    val ids: Map<String, Int> by lazy {
        mHookInfo.mapIds.idsMap
    }

    fun getCustomizeAccessKey(area: String): String? {
        var key = sPrefs.getString("${area}_accessKey", null)
        if (key.isNullOrBlank()) key = accessKey
        return key
    }

    val biliAccounts by lazy {
        biliAccountsClass?.callStaticMethodOrNull(
            mHookInfo.biliAccounts.get.orNull,
            AndroidAppHelper.currentApplication()
        )
    }

    val accessKey by lazy {
        biliAccounts?.callMethodOrNullAs<String>(mHookInfo.biliAccounts.getAccessKey.orNull)
    }

    val appKey by lazy {
        mHookInfo.biliConfig.getAppKey.orNull?.let {
            biliConfigClass?.callStaticMethodOrNullAs<String>(it)
        } ?: appKeyMap[packageName] ?: "1d8b6e7d45233436"
    }

    fun fastJsonParse() = mHookInfo.fastJson.parse.orNull

    fun colorArray() = mHookInfo.themeHelper.colorArray.orNull

    fun colorId() = mHookInfo.themeIdHelper.colorId.orNull

    fun columnColorArray() = mHookInfo.columnHelper.colorArray.orNull

    fun signQueryName() = mHookInfo.signQuery.method.orNull

    fun skinList() = mHookInfo.skinList.orNull

    fun themeReset() = mHookInfo.themeProcessor.methodsList.map { it.orNull }

    fun homeCenters() = mHookInfo.settings.homeUserCenterList.map {
        it.class_ from mClassLoader to it.addSetting.orNull
    }

    fun requestField() = mHookInfo.okHttp.response.request.orNull

    fun likeMethod() = mHookInfo.section.like.orNull

    fun themeName() = mHookInfo.themeName.field.orNull

    fun shareWrapper() = mHookInfo.shareWrapper.method.orNull

    fun downloadingThread() = mHookInfo.downloadThread.field.orNull

    fun reportDownloadThread() = mHookInfo.downloadThread.reportDownload.method.orNull

    fun setNotification() = mHookInfo.musicNotification.builder.method.orNull

    fun openDrawer() = mHookInfo.drawer.open.orNull

    fun closeDrawer() = mHookInfo.drawer.close.orNull

    fun isDrawerOpen() = mHookInfo.drawer.isOpen.orNull

    fun paramsToMap() = mHookInfo.bangumiParams.paramsToMap.orNull

    fun gson() = mHookInfo.gsonHelper.gson.orNull

    fun defaultSpeed() = mHookInfo.playerCoreService.getDefaultSpeed.orNull

    fun setDefaultSpeed() = mHookInfo.playerCoreService.setDefaultSpeed.orNull

    fun updateSpeed() = mHookInfo.playerCoreService.updateSpeed.orNull

    fun urlField() = mHookInfo.okHttp.request.url.orNull

    fun methodFiled() = mHookInfo.okHttp.request.method.orNull

    fun gsonToJson() = mHookInfo.gsonHelper.toJson.orNull

    fun gsonFromJson() = mHookInfo.gsonHelper.fromJson.orNull

    fun pegasusFeed() = mHookInfo.pegasusFeed.method.orNull

    fun descCopy() = mHookInfo.descCopy.methodsList.map { it.orNull }

    fun descCopyView() = mHookInfo.descCopy.classesList.map { it from mClassLoader }

    fun responseDataField() = runCatchingOrNull {
        rxGeneralResponseClass?.getDeclaredField("data")?.name
    } ?: "_data"

    fun okio() = mHookInfo.okio.field.orNull

    fun okioLength() = mHookInfo.okio.length.orNull

    fun okioInputStream() = mHookInfo.okioBuffer.inputStream.orNull

    fun okioReadFrom() = mHookInfo.okioBuffer.readFrom.orNull

    fun seekTo() = mHookInfo.playerCoreService.seekTo.orNull

    fun onSeekComplete() = mHookInfo.playerCoreService.onSeekComplete.orNull

    fun setState() = mHookInfo.musicNotification.setState.orNull

    fun kanbanCallback() = mHookInfo.kanBan.method.orNull

    fun showToast() = mHookInfo.toastHelper.show.orNull

    fun cancelShowToast() = mHookInfo.toastHelper.cancel.orNull

    fun canTryWatchVipQuality() = mHookInfo.canTryWatchVipQuality.orNull

    fun setInvalidTips() = commentInvalidFragmentClass?.declaredMethods?.find { m ->
        m.parameterTypes.let { it.size == 2 && it[0] == commentInvalidFragmentClass && it[1].name == "kotlin.Pair" }
    }?.name

    fun musicWrapperPlayer() = mHookInfo.musicNotification.musicWrapperPlayer.orNull

    fun musicPlayer() = mHookInfo.musicNotification.musicPlayer.orNull

    fun musicPlayerService() = mHookInfo.musicNotification.musicPlayerService.orNull

    fun codeField() = mHookInfo.okHttp.response.code.orNull

    fun bodyField() = mHookInfo.okHttp.response.body.orNull

    fun protocolField() = mHookInfo.okHttp.response.protocol.orNull

    fun headersField() = mHookInfo.okHttp.response.headers.orNull

    fun getHeader() = mHookInfo.okHttp.headers.get.orNull

    fun execute() = mHookInfo.okHttp.realCall.execute.orNull

    fun enqueue() = mHookInfo.okHttp.realCall.enqueue.orNull

    fun source() = mHookInfo.okio2.source.orNull

    fun sourceBuffer() = mHookInfo.okio2.sourceBuffer.orNull

    fun create() = mHookInfo.okHttp.responseBody.create.orNull

    fun string() = mHookInfo.okHttp.responseBody.string.orNull

    fun bodySource() = mHookInfo.okHttp.responseBody.source.orNull

    fun get() = mHookInfo.okHttp.mediaType.get.orNull

    fun setParser() = mHookInfo.biliCall.setParser.orNull

    fun biliCallRequestField() = mHookInfo.biliCall.request.orNull

    fun onOperateClick() = mHookInfo.onOperateClick.orNull

    fun getContentString() = mHookInfo.getContentString.orNull

    fun check() = mHookInfo.updateInfoSupplier.check.orNull

    fun dynamicDescHolderListeners() =
        mHookInfo.dynDescHolderListenerList.map { it.from(mClassLoader) }

    fun playerFullStoryWidgets() =
        mHookInfo.playerFullStoryWidgetList.map { it.class_.from(mClassLoader) to it.method.orNull }

    fun bangumiUniformSeasonActivityEntrance() = mHookInfo.bangumiSeasonActivityEntrance.orNull

    fun getPreload() = mHookInfo.playerPreloadHolder.get.orNull

    fun getDefaultQn() = mHookInfo.playerSettingHelper.getDefaultQn.orNull

    fun liveRtcEnable() = mHookInfo.liveRtcHelper.liveRtcEnableMethod.orNull

    fun allThemesField() = mHookInfo.builtInThemes.all.orNull

    fun getBLKVPrefs() = mHookInfo.biliGlobalPreference.get.orNull

    fun onFeedClicked() = mHookInfo.cardClickProcessor.onFeedClicked.orNull

    private fun readHookInfo(context: Context): Configs.HookInfo {
        try {
            val hookInfoFile = File(context.cacheDir, Constant.HOOK_INFO_FILE_NAME)
            Log.d("Reading hook info: $hookInfoFile")
            val t = measureTimeMillis {
                if (hookInfoFile.isFile && hookInfoFile.canRead()) {
                    val lastUpdateTime = context.packageManager.getPackageInfo(
                        AndroidAppHelper.currentPackageName(),
                        0
                    ).lastUpdateTime
                    val lastModuleUpdateTime = try {
                        context.packageManager.getPackageInfo(BuildConfig.APPLICATION_ID, 0)
                    } catch (e: Throwable) {
                        null
                    }?.lastUpdateTime ?: 0
                    val info = FileInputStream(hookInfoFile).use {
                        runCatchingOrNull { Configs.HookInfo.parseFrom(it) }
                            ?: Configs.HookInfo.newBuilder().build()
                    }
                    if (info.lastUpdateTime >= lastUpdateTime && info.lastUpdateTime >= lastModuleUpdateTime
                        && getVersionCode(context.packageName) == info.clientVersionCode
                        && BuildConfig.VERSION_CODE == info.moduleVersionCode
                        && BuildConfig.VERSION_NAME == info.moduleVersionName
                        && info.biliAccounts.getAccessKey.orNull != null
                    ) {
                        if (BuildConfig.DEBUG) {
                            try {
                                System.loadLibrary("biliroaming")
                                context.classLoader.findDexClassLoader(::findRealClassloader)?.let {
                                    globalDexHelper = DexHelper(it)
                                }
                            } catch (_: Throwable) {
                            }
                        }
                        return info
                    }
                }
            }
            Log.d("Read hook info completed: take $t ms")
        } catch (e: Throwable) {
            Log.w(e)
        }
        return initHookInfo(context).also {
            try {
                val hookInfoFile = File(context.cacheDir, Constant.HOOK_INFO_FILE_NAME)
                if (hookInfoFile.exists()) hookInfoFile.delete()
                FileOutputStream(hookInfoFile).use { o -> it.writeTo(o) }
            } catch (e: Exception) {
                Log.e(e)
            }
        }
    }

    companion object {
        @JvmStatic
        fun findRealClassloader(classloader: BaseDexClassLoader): BaseDexClassLoader {
            val serviceField = classloader.javaClass.findFirstFieldByExactTypeOrNull(
                "com.bilibili.lib.tribe.core.internal.loader.DefaultClassLoaderService" from classloader
            )
            val delegateField = classloader.javaClass.findFirstFieldByExactTypeOrNull(
                "com.bilibili.lib.tribe.core.internal.loader.TribeLoaderDelegate" from classloader
            )
            return if (serviceField != null) {
                serviceField.type.declaredFields.filter { f ->
                    f.type == ClassLoader::class.java
                }.map { f ->
                    classloader.getObjectFieldOrNull(serviceField.name)
                        ?.getObjectFieldOrNull(f.name)
                }.firstOrNull { o ->
                    o?.javaClass?.name?.startsWith("com.bilibili") == false
                } as? BaseDexClassLoader ?: classloader
            } else if (delegateField != null) {
                val loaderField =
                    delegateField.type.findFirstFieldByExactTypeOrNull(ClassLoader::class.java)
                val out = classloader.getObjectFieldOrNull(delegateField.name)
                    ?.getObjectFieldOrNull(loaderField?.name)
                if (BaseDexClassLoader::class.java.isInstance(out)) out as BaseDexClassLoader else classloader
            } else classloader
        }

        @JvmStatic
        fun initHookInfo(context: Context) = hookInfo {
            val classloader = context.classLoader
            val classesList = context.classLoader.allClassesList(::findRealClassloader).asSequence()

            try {
                System.loadLibrary("biliroaming")
            } catch (e: Throwable) {
                Log.e(e)
                Log.toast("不支持该架构或框架，部分功能可能失效")
                return@hookInfo
            }

            val dexHelper =
                DexHelper(classloader.findDexClassLoader(::findRealClassloader) ?: return@hookInfo)
            if (BuildConfig.DEBUG) instance.globalDexHelper = dexHelper
            lastUpdateTime = max(
                context.packageManager.getPackageInfo(
                    AndroidAppHelper.currentPackageName(),
                    0
                ).lastUpdateTime,
                runCatchingOrNull {
                    context.packageManager.getPackageInfo(
                        BuildConfig.APPLICATION_ID,
                        0
                    )
                }?.lastUpdateTime ?: 0
            )
            clientVersionCode = getVersionCode(context.packageName)
            moduleVersionCode = BuildConfig.VERSION_CODE
            moduleVersionName = BuildConfig.VERSION_NAME
            mapIds = mapIds {
                val reg = Regex("^tv\\.danmaku\\.bili\\.[^.]*$")
                val mask = Modifier.STATIC or Modifier.PUBLIC or Modifier.FINAL
                classesList.filter {
                    it.startsWith("tv.danmaku.bili")
                }.filter {
                    it.matches(reg)
                }.flatMap { c ->
                    c.findClass(classloader).declaredFields.filter {
                        it.modifiers == mask
                                && it.type == Int::class.javaPrimitiveType
                    }
                }.forEach { ids[it.name] = it.get(null) as Int }
            }
            appUpgrade = appUpgrade {
                val writeChannelMethod = dexHelper.findMethodUsingString(
                    "Channel info has already exist.",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@appUpgrade
                val checkMethodIndex = dexHelper.findMethodUsingString(
                    "Do sync http request.",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@appUpgrade
                val checkMethod = dexHelper.decodeMethodIndex(checkMethodIndex) as Method
                val upgradeInfoClass = checkMethod.returnType
                val upgradeUtilsClass = writeChannelMethod.declaringClass
                val writeInfoMethod = upgradeUtilsClass.methods.find { it.name == "writeInfo" }
                    ?: dexHelper.findMethodInvoking(
                        checkMethodIndex,
                        -1,
                        -1,
                        null,
                        -1,
                        longArrayOf(
                            dexHelper.encodeClassIndex(Context::class.java),
                            dexHelper.encodeClassIndex(upgradeInfoClass)
                        ),
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    } ?: return@appUpgrade
                val cleanApkDirMethod = upgradeUtilsClass.methods.find { it.name == "cleanApkDir" }
                    ?: dexHelper.findMethodInvoking(
                        checkMethodIndex,
                        -1,
                        -1,
                        null,
                        -1,
                        longArrayOf(
                            dexHelper.encodeClassIndex(Context::class.java),
                            dexHelper.encodeClassIndex(Boolean::class.javaPrimitiveType!!)
                        ),
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    } ?: return@appUpgrade
                val versionExceptionClass =
                    "tv.danmaku.bili.update.internal.exception.LatestVersionException"
                        .from(classloader) ?: dexHelper.findMethodInvoking(
                        checkMethodIndex,
                        -1,
                        -1,
                        "VL",
                        -1,
                        longArrayOf(dexHelper.encodeClassIndex(String::class.java)),
                        null,
                        null,
                        false
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)?.takeIf { m ->
                            m is Constructor<*> && Exception::class.java.isAssignableFrom(m.declaringClass)
                                    && m.declaringClass.name != Exception::class.java.name
                        }?.declaringClass
                    } ?: return@appUpgrade
                upgradeUtils = class_ { name = upgradeUtilsClass.name }
                writeChannel = method { name = writeChannelMethod.name }
                writeInfo = method { name = writeInfoMethod.name }
                cleanApkDir = method { name = cleanApkDirMethod.name }
                supplier = class_ { name = checkMethod.declaringClass.name }
                check = method { name = checkMethod.name }
                upgradeInfo = class_ { name = upgradeInfoClass.name }
                versionException = class_ { name = versionExceptionClass.name }
            }
            darkSwitch = darkSwitch {
                val userFragmentClass = "tv.danmaku.bili.ui.main2.mine.HomeUserCenterFragment"
                    .from(classloader) ?: dexHelper.findMethodUsingString(
                    "key_global_link_entrance_shown",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@darkSwitch
                val userFragmentIndex = dexHelper.encodeClassIndex(userFragmentClass)
                val switchDarkModeIndex = dexHelper.findMethodUsingString(
                    "default",
                    false,
                    -1,
                    1,
                    "VZ",
                    userFragmentIndex,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@darkSwitch
                val switchDarkModeMethod =
                    dexHelper.decodeMethodIndex(switchDarkModeIndex) ?: return@darkSwitch
                var isDarkFollowSystemMethod: Method? = null
                var themeUtilsClass = "com.bilibili.lib.ui.util.MultipleThemeUtils"
                    .from(classloader)?.also {
                        isDarkFollowSystemMethod = it.runCatchingOrNull {
                            getDeclaredMethod("isNightFollowSystem", Context::class.java)
                        }
                    }
                isDarkFollowSystemMethod ?: run {
                    val contextIndex = dexHelper.encodeClassIndex(Context::class.java)
                    isDarkFollowSystemMethod = dexHelper.findMethodInvoking(
                        switchDarkModeIndex,
                        -1,
                        1,
                        "ZL",
                        -1,
                        longArrayOf(contextIndex),
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    } as? Method
                    themeUtilsClass = isDarkFollowSystemMethod?.declaringClass
                }
                userFragment = class_ { name = userFragmentClass.name }
                switchDarkMode = method { name = switchDarkModeMethod.name }
                themeUtilsClass?.let {
                    themeUtils = class_ { name = it.name }
                }
                isDarkFollowSystemMethod?.let {
                    isDarkFollowSystem = method { name = it.name }
                }
            }
            toolbarService = toolbarService {
                val bangumiActivityClass =
                    "com.bilibili.bangumi.ui.page.detail.BangumiDetailActivityV3".from(classloader)
                        ?: return@toolbarService
                val onStopMethod = bangumiActivityClass.getDeclaredMethod("onStop")
                val onStopMethodIndex = dexHelper.encodeMethodIndex(onStopMethod)
                val contextClassIndex = dexHelper.encodeClassIndex(Context::class.java)
                val miniPlayMethod = dexHelper.findMethodInvoking(
                    onStopMethodIndex,
                    -1,
                    1,
                    "VL",
                    -1,
                    longArrayOf(contextClassIndex),
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@toolbarService
                val toolbarServiceClass = miniPlayMethod.declaringClass
                class_ = class_ { name = toolbarServiceClass.name }
                miniPlay = method { name = miniPlayMethod.name }
            }
            setLineToAllCount = method {
                val ellipsizingTextViewClass =
                    "com.bilibili.bplus.followingcard.widget.EllipsizingTextView"
                        .from(classloader) ?: return@method
                name = ellipsizingTextViewClass.declaredMethods
                    .find { it.name == "setLineToAllCount" }?.name
                    ?: run {
                        val setOnClickListenerIndex = dexHelper.encodeMethodIndex(
                            View::class.java.getMethod(
                                "setOnClickListener",
                                View.OnClickListener::class.java
                            )
                        )
                        val ellipsizingTextViewIndex =
                            dexHelper.encodeClassIndex(ellipsizingTextViewClass)
                        val viewGroupIndex = dexHelper.encodeClassIndex(ViewGroup::class.java)
                        val listIndex = dexHelper.encodeClassIndex(List::class.java)
                        dexHelper.findMethodInvoked(
                            setOnClickListenerIndex,
                            -1,
                            -1,
                            "LLL",
                            -1,
                            longArrayOf(viewGroupIndex, listIndex),
                            null,
                            null,
                            false
                        ).asSequence().firstNotNullOfOrNull {
                            dexHelper.findMethodInvoking(
                                it,
                                -1,
                                -1,
                                "VI",
                                ellipsizingTextViewIndex,
                                null,
                                null,
                                null,
                                true
                            ).asSequence().firstNotNullOfOrNull { idx ->
                                dexHelper.decodeMethodIndex(idx) as? Method
                            }
                        }
                    }?.name ?: return@method
            }
            superMenu = superMenu {
                val mWebActivityClass = "tv.danmaku.bili.ui.webview.MWebActivity".from(classloader)
                    ?: return@superMenu
                val mWebActivityIndex = dexHelper.encodeClassIndex(mWebActivityClass)
                val showMethod = dexHelper.findMethodUsingString(
                    "open_browser",
                    false,
                    -1,
                    -1,
                    null,
                    mWebActivityIndex,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.run {
                    dexHelper.findMethodInvoking(
                        this,
                        -1,
                        0,
                        "V",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }
                } ?: return@superMenu
                val superMenuClass = showMethod.declaringClass
                val clickListenerField = superMenuClass.declaredFields.find { f ->
                    f.type.isInterface && f.type.let { c ->
                        c.declaredMethods.size == 1 && c.declaredMethods[0].let { m ->
                            m.returnType == Boolean::class.javaPrimitiveType && m.parameterTypes.let {
                                it.size == 1 && it[0].isInterface
                            }
                        }
                    }
                } ?: return@superMenu
                val menuItemImplClass = dexHelper.findMethodUsingString(
                    "MenuItemImpl{mId='",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@superMenu
                class_ = class_ { name = showMethod.declaringClass.name }
                menuItemClickListener = class_ { name = clickListenerField.type.name }
                menuItemImpl = class_ { name = menuItemImplClass.name }
                show = method { name = showMethod.name }
                clickListener = field { name = clickListenerField.name }
            }
            playbackSpeed = playbackSpeed {
                val adapterClasses = dexHelper.findMethodUsingString(
                    "player.player.choose-speed.0.player",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    false
                ).map { dexHelper.decodeMethodIndex(it)?.declaringClass }.filterNotNull()
                    .mapNotNull {
                        it.declaredFields.firstNotNullOfOrNull { f ->
                            if (View.OnClickListener::class.java.isAssignableFrom(f.type)) {
                                f.type
                            } else null
                        }
                    }
                val adapterFields = adapterClasses.mapNotNull {
                    it.findFirstFieldByExactTypeOrNull(FloatArray::class.java)
                }
                adapterClasses.zip(adapterFields).map { (c, f) ->
                    speedAdapter {
                        class_ = class_ { name = c.name }
                        speedArray = field { name = f.name }
                    }
                }.let { speedAdapter.addAll(it) }
                storySuperMenu = storySuperMenu {
                    val clazz = "com.bilibili.video.story.action.StorySuperMenu".from(classloader)
                        ?: dexHelper.findMethodUsingString(
                            "story_setting_normal_share",
                            false,
                            -1,
                            -1,
                            null,
                            -1,
                            null,
                            null,
                            null,
                            true
                        ).firstOrNull()?.let { dexHelper.decodeMethodIndex(it)?.declaringClass }
                        ?: return@storySuperMenu
                    val field = clazz.findFirstFieldByExactTypeOrNull(FloatArray::class.java)
                        ?: return@storySuperMenu
                    class_ = class_ { name = clazz.name }
                    speedArray = field { name = field.name }
                }
                menuFuncSegment = menuFuncSegment {
                    val clazz = "tv.danmaku.bili.ui.video.videodetail.function.MenuFuncSegment"
                        .from(classloader) ?: dexHelper.findMethodUsingString(
                        "pref_key_share_listen_show_new",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let { dexHelper.decodeMethodIndex(it)?.declaringClass }
                    ?: return@menuFuncSegment
                    val field = clazz.findFirstFieldByExactTypeOrNull(FloatArray::class.java)
                        ?: return@menuFuncSegment
                    class_ = class_ { name = clazz.name }
                    speedArray = field { name = field.name }
                }
                newShareService = newShareService {
                    val clazz =
                        "com.bilibili.bangumi.logic.page.detail.service.refactor.NewShareService"
                            .from(classloader) ?: dexHelper.findMethodUsingString(
                            "pgc.pgc-video-detail.half-player-more-option.0.click",
                            false,
                            -1,
                            -1,
                            null,
                            -1,
                            null,
                            null,
                            null,
                            true
                        ).firstOrNull()?.let { dexHelper.decodeMethodIndex(it)?.declaringClass }
                        ?: return@newShareService
                    val field = clazz.findFirstFieldByExactTypeOrNull(FloatArray::class.java)
                        ?: return@newShareService
                    class_ = class_ { name = clazz.name }
                    speedArray = field { name = field.name }
                }
                val settingClasses = dexHelper.findMethodUsingString(
                    "player.player.full-more.speed.player",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    false
                ).map { dexHelper.decodeMethodIndex(it)?.declaringClass }.filterNotNull()
                val settingFields = settingClasses.map {
                    Pair(
                        it.findFirstFieldByExactTypeOrNull(FloatArray::class.java),
                        it.findFirstFieldByExactTypeOrNull(IntArray::class.java)
                    )
                }
                settingClasses.zip(settingFields).mapNotNull {
                    val clazz = it.first
                    val (fSpeed, fId) = it.second
                    if (fSpeed == null || fId == null) {
                        null
                    } else {
                        playSpeedSetting {
                            class_ = class_ { name = clazz.name }
                            speedArray = field { name = fSpeed.name }
                            speedTextIdArray = field { name = fId.name }
                        }
                    }
                }.let { playSpeedSetting.addAll(it) }
                podcastSpeedSeekBar = podcastSpeedSeekBar {
                    val clazz = "com.bilibili.music.podcast.view.PodcastSpeedSeekBar"
                        .from(classloader) ?: return@podcastSpeedSeekBar
                    val speedArrayField =
                        clazz.findFirstFieldByExactTypeOrNull(FloatArray::class.java)
                            ?: return@podcastSpeedSeekBar
                    val speedNameListField = clazz.findFirstFieldByExactTypeOrNull(List::class.java)
                        ?: return@podcastSpeedSeekBar
                    class_ = class_ { name = clazz.name }
                    speedArray = field { name = speedArrayField.name }
                    speedNameList = field { name = speedNameListField.name }
                }
                musicPlayerPanel = absMusicPlayerPanelSegment {
                    val clazz = "com.bilibili.music.podcast.segment.AbsMusicPlayerPanelSegment"
                        .from(classloader) ?: dexHelper.findMethodUsingString(
                        "turn_left",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let { dexHelper.decodeMethodIndex(it)?.declaringClass }
                    ?: return@absMusicPlayerPanelSegment
                    val field = clazz.findFirstFieldByExactTypeOrNull(FloatArray::class.java)
                        ?: return@absMusicPlayerPanelSegment
                    class_ = class_ { name = clazz.name }
                    speedArray = field { name = field.name }
                }
                val widgetClasses = dexHelper.findMethodUsingString(
                    "player.player.speed.0.player",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    false
                ).map { dexHelper.decodeMethodIndex(it)?.declaringClass }.filterNotNull()
                val setTextMethod =
                    TextView::class.java.getDeclaredMethod("setText", CharSequence::class.java)
                val setTextMethodIndex = dexHelper.encodeMethodIndex(setTextMethod)
                val updateMethods = widgetClasses.mapNotNull { c ->
                    dexHelper.findMethodInvoked(
                        setTextMethodIndex,
                        -1,
                        -1,
                        null,
                        dexHelper.encodeClassIndex(c),
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let { dexHelper.decodeMethodIndex(it) }
                }
                widgetClasses.zip(updateMethods).map { (c, m) ->
                    playerSpeedWidget {
                        class_ = class_ { name = c.name }
                        update = method { name = m.name }
                    }
                }.let { playerSpeedWidget.addAll(it) }
                playerSettingService = playerSettingService {
                    val clazz = dexHelper.findMethodUsingString(
                        "could not remove all key for scope: ",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)?.declaringClass
                    } ?: return@playerSettingService
                    val getFloatMethod = clazz.declaredMethods.find { m ->
                        val floatType = Float::class.javaPrimitiveType
                        m.returnType == floatType && m.parameterTypes.let {
                            it.size == 2 && it[0] == String::class.java && it[1] == floatType
                        }
                    } ?: return@playerSettingService
                    class_ = class_ { name = clazz.name }
                    getFloat = method { name = getFloatMethod.name }
                }
                dexHelper.findMethodUsingString(
                    "player.player.gesture.speedup.player",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    false
                ).asSequence().mapNotNull {
                    dexHelper.findMethodInvoking(
                        it,
                        -1,
                        1,
                        "VF",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let { idx ->
                        dexHelper.decodeMethodIndex(idx)
                    }
                }.map {
                    tripleSpeedService {
                        class_ = class_ { name = it.declaringClass.name }
                        updateSpeed = method { name = it.name }
                    }
                }.let { tripleSpeedService.addAll(it.toList()) }
            }
            orientationProcessor = orientationProcessor {
                val startGravitySensorMethod = dexHelper.findMethodUsingString(
                    "Start gravity sensor",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@orientationProcessor
                val clazz = startGravitySensorMethod.declaringClass
                val activityField = clazz.declaredFields.find { f ->
                    Activity::class.java.isAssignableFrom(f.type)
                } ?: return@orientationProcessor
                val switchOrientationMethod = clazz.declaredMethods.find { m ->
                    m.parameterTypes.let { it.size == 1 && it[0] == Int::class.javaPrimitiveType }
                } ?: return@orientationProcessor
                val correctOrientationMethod = clazz.declaredMethods.find { m ->
                    m.parameterTypes.let { it.size == 1 && it[0] == clazz } && !m.isSynthetic
                } ?: return@orientationProcessor
                class_ = class_ { name = clazz.name }
                activity = field { name = activityField.name }
                startGravitySensor = method { name = startGravitySensorMethod.name }
                switchOrientation = method { name = switchOrientationMethod.name }
                correctOrientation = method { name = correctOrientationMethod.name }
            }
            screenLayoutHelper = screenLayoutHelper {
                val clazz =
                    "com.bilibili.bangumi.ui.page.detail.playerV2.screenstate.OGVDetailScreenStateLayoutHelper"
                        .from(classloader) ?: dexHelper.findMethodUsingString(
                        "OGV-OGVDetailScreenStateLayoutHelper",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass ?: return@screenLayoutHelper
                val field = clazz.declaredFields.find {
                    Activity::class.java.isAssignableFrom(it.type)
                } ?: return@screenLayoutHelper
                val types = clazz.declaredFields.map { it.type }
                val method = clazz.declaredMethods.find { m ->
                    m.isPublic && m.parameterTypes.let {
                        it.size == 1 && !it[0].isPrimitive && it[0] in types
                    }
                } ?: return@screenLayoutHelper
                class_ = class_ { name = clazz.name }
                activity = field { name = field.name }
                onStateChange = method { name = method.name }
            }
            biliAccountInfo = biliAccountInfo {
                val clazz = "com.bilibili.lib.accountinfo.BiliAccountInfo".from(classloader)
                val get = clazz?.runCatchingOrNull { getDeclaredMethod("get") }
                val vip = clazz?.runCatchingOrNull { getDeclaredMethod("isEffectiveVip") }
                if (clazz != null && get != null && vip != null) {
                    class_ = class_ { name = clazz.name }
                    this.get = method { name = get.name }
                    isEffectiveVip = method { name = vip.name }
                    return@biliAccountInfo
                }
                val downloadActivityIdx = "tv.danmaku.bili.ui.offline.DownloadingActivity"
                    .from(classloader)?.let { dexHelper.encodeClassIndex(it) }
                    ?: return@biliAccountInfo
                val vipMethod = dexHelper.findMethodUsingString(
                    "meantime",
                    false,
                    -1,
                    -1,
                    null,
                    downloadActivityIdx,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.run {
                    dexHelper.findMethodInvoking(
                        this,
                        -1,
                        -1,
                        "Z",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }
                } ?: return@biliAccountInfo
                val infoClass = vipMethod.declaringClass
                val getMethod = infoClass.declaredMethods.find {
                    it.isStatic && !it.isSynthetic && it.returnType == infoClass && it.parameterTypes.isEmpty()
                } ?: return@biliAccountInfo
                class_ = class_ { name = infoClass.name }
                this.get = method { name = getMethod.name }
                isEffectiveVip = method { name = vipMethod.name }
            }
            dexHelper.findMethodUsingString(
                "last_4k_hint_show_timestamp",
                false,
                -1,
                -1,
                null,
                -1,
                null,
                null,
                null,
                false
            ).asSequence().mapNotNull { dexHelper.decodeMethodIndex(it)?.declaringClass }
                .mapNotNull { c ->
                    val m = c.declaredMethods.find { m ->
                        m.parameterTypes.let { it.size == 5 && it[1] == Boolean::class.javaPrimitiveType }
                    } ?: return@mapNotNull null
                    qualityViewHolder {
                        class_ = class_ { name = c.name }
                        bindOnline = method { name = m.name }
                    }
                }.toList().let { qualityViewHolder.addAll(it) }
            blkv = bLKV {
                if (!BuildConfig.DEBUG) return@bLKV
                val byNameMethod = dexHelper.findMethodUsingString(
                    ".blkv",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@bLKV
                val clazz = byNameMethod.declaringClass
                val byFileMethod = clazz.declaredMethods.find { m ->
                    !m.isSynthetic && SharedPreferences::class.java.isAssignableFrom(m.returnType)
                            && m.parameterTypes.let { it.size == 4 && it[1] == File::class.java }
                } ?: return@bLKV
                class_ = class_ { name = clazz.name }
                getByName = method { name = byNameMethod.name }
                getByFile = method { name = byFileMethod.name }
            }
            favFolderDialog = favFolderDialog {
                val clazz = dexHelper.findMethodUsingString(
                    "key:season_id",
                    false,
                    -1,
                    0,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)?.declaringClass
                } ?: return@favFolderDialog
                val field = clazz.declaredFields.firstOrNull {
                    CheckBox::class.java.isAssignableFrom(it.type)
                } ?: return@favFolderDialog
                class_ = class_ { name = clazz.name }
                checkBox = field { name = field.name }
            }
            playerController = iPlayerController {
                val m = dexHelper.findMethodUsingString("use unite player: ")
                    .firstOrNull()?.let { dexHelper.decodeMethodIndex(it) }
                if (m != null) {
                    class_ = class_ { name = m.declaringClass.name }
                    getPlayer = method { name = m.name }
                    return@iPlayerController
                }
                val m2 = dexHelper.findMethodUsingString("create and prepared player for shared")
                    .firstOrNull()?.run {
                        val clazz = dexHelper.decodeMethodIndex(this)
                            ?.declaringClass ?: return@iPlayerController
                        val classIdx = dexHelper.encodeClassIndex(clazz)
                        dexHelper.findMethodInvoking(
                            this,
                            parameterShorty = "V",
                            declaringClass = classIdx
                        ).firstOrNull()?.run {
                            dexHelper.findMethodInvoking(
                                this,
                                parameterShorty = "LLZL"
                            ).firstOrNull()?.let {
                                dexHelper.decodeMethodIndex(it)
                            }
                        }
                    } ?: return@iPlayerController
                class_ = class_ { name = m2.declaringClass.name }
                getPlayer = method { name = m2.name }
            }

            bangumiApiResponse = class_ {
                name = "com.bilibili.bangumi.data.common.api.BangumiApiResponse"
            }
            retrofitResponse = class_ {
                name = "retrofit2.Response".from(classloader)?.name
                    ?: dexHelper.findMethodUsingString(
                        "rawResponse must be successful response",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass?.name ?: return@class_
            }
            val responseBodyClass = "okhttp3.ResponseBody".from(classloader)
                ?: dexHelper.findMethodUsingString(
                    "Cannot buffer entire body for content length: ",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass
            okHttp = okHttp {
                val responseClass = "okhttp3.Response".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        "Response{protocol=",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass ?: return@okHttp
                val requestClass = "okhttp3.Request".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        "Request{method=",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass ?: return@okHttp
                val urlClass = "okhttp3.HttpUrl".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        ":@",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass ?: return@okHttp
                responseBodyClass ?: return@okHttp
                val getMethod = dexHelper.findMethodUsingString(
                    "No subtype found for:",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@okHttp
                val realCallClass = dexHelper.findMethodUsingString(
                    "web socket",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@okHttp
                val realCallMethods = dexHelper.findMethodUsingString(
                    "Already Executed",
                    false,
                    -1,
                    -1,
                    null,
                    dexHelper.encodeClassIndex(realCallClass),
                    null,
                    null,
                    null,
                    false
                ).map { dexHelper.decodeMethodIndex(it) as Method }
                    .takeIf { it.size == 2 }
                    ?: return@okHttp
                val executeMethod = realCallMethods.find { it.parameterTypes.isEmpty() }
                    ?: return@okHttp
                val enqueueMethod = realCallMethods.find { it.parameterTypes.size == 1 }
                    ?: return@okHttp
                val headersClass = "okhttp3.Headers".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        "Expected alternating header names and values",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass ?: return@okHttp
                val headerGetMethod = headersClass.declaredMethods.find { m ->
                    m.isPublic && m.returnType == String::class.java && m.parameterTypes.let {
                        it.size == 1 && it[0] == String::class.java
                    }
                } ?: return@okHttp
                request = request {
                    class_ = class_ { name = requestClass.name }
                    url = field {
                        name = requestClass.findFirstFieldByExactTypeOrNull(urlClass)?.name
                            ?: return@field
                    }
                    method = field {
                        name = requestClass.findFirstFieldByExactTypeOrNull(String::class.java)
                            ?.name ?: return@field
                    }
                }
                response = response {
                    class_ = class_ { name = responseClass.name }
                    request = field {
                        name = responseClass.findFirstFieldByExactTypeOrNull(requestClass)?.name
                            ?: return@field
                    }
                    code = field {
                        val intType = Int::class.javaPrimitiveType!!
                        name = responseClass.findFirstFieldByExactTypeOrNull(intType)?.name
                            ?: return@field
                    }
                    body = field {
                        name = responseClass.findFirstFieldByExactTypeOrNull(responseBodyClass)
                            ?.name ?: return@field
                    }
                    protocol = field {
                        name = responseClass.declaredFields.find { it.type.isEnum }?.name
                            ?: return@field
                    }
                    headers = field {
                        name = responseClass.findFirstFieldByExactTypeOrNull(headersClass)
                            ?.name ?: return@field
                    }
                }
                responseBody = responseBody {
                    class_ = class_ { name = responseBodyClass.name }
                    create = method {
                        name = responseBodyClass.methods.find {
                            it.isStatic && it.parameterTypes.size == 2 && it.parameterTypes[1] == String::class.java
                        }?.name ?: return@method
                    }
                    string = method {
                        name = responseBodyClass.methods.find {
                            it.parameterTypes.isEmpty() && it.returnType == String::class.java
                        }?.name ?: return@method
                    }
                    source = method {
                        name = responseBodyClass.declaredMethods.find {
                            Modifier.isAbstract(it.modifiers) && it.returnType.isInterface && it.parameterTypes.isEmpty()
                        }?.name ?: return@method
                    }
                }
                mediaType = mediaType {
                    class_ = class_ { name = getMethod.declaringClass.name }
                    get = method { name = getMethod.name }
                }
                realCall = realCall {
                    class_ = class_ { name = realCallClass.name }
                    execute = method { name = executeMethod.name }
                    enqueue = method { name = enqueueMethod.name }
                }
                headers = headers {
                    class_ = class_ { name = headersClass.name }
                    get = method { name = headerGetMethod.name }
                }
                callback = class_ { name = enqueueMethod.parameterTypes.first().name }
            }
            okio2 = okio2 {
                val okioClass = "okio.Okio".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        "getsockname failed",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        false
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it).takeIf { m ->
                            m != null && m.isFinal
                        }
                    }?.declaringClass ?: return@okio2
                class_ = class_ { name = okioClass.name }
                val sourceMethod = okioClass.methods.find {
                    it.parameterTypes.size == 1 && it.parameterTypes[0] == InputStream::class.java
                } ?: return@okio2
                val sourceBufferMethod = okioClass.methods.find {
                    it.parameterTypes.size == 1 && it.parameterTypes[0] == sourceMethod.returnType
                } ?: return@okio2
                bufferedSource = class_ { name = sourceBufferMethod.returnType.name }
                source = method { name = sourceMethod.name }
                sourceBuffer = method { name = sourceBufferMethod.name }
            }
            fastJson = fastJson {
                val fastJsonClass = dexHelper.findMethodUsingString(
                    "toJSON error",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@fastJson
                val notObfuscated = "JSON" == fastJsonClass.simpleName
                class_ = class_ { name = fastJsonClass.name }
                parse = method { name = if (notObfuscated) "parseObject" else "a" }
            }
            biliAccounts = biliAccounts {
                val biliAccountsClass = dexHelper.findMethodUsingString(
                    "refresh token error",
                    false,
                    -1,
                    0,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@biliAccounts
                class_ = class_ { name = biliAccountsClass.name }
                val biliAccountIndex = dexHelper.encodeClassIndex(biliAccountsClass)
                val biliAuthFragmentMethodIndex = dexHelper.findMethodUsingString(
                    "initFacial enter",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@biliAccounts
                val calledMethods = dexHelper.findMethodInvoking(
                    biliAuthFragmentMethodIndex,
                    -1,
                    -1,
                    null,
                    biliAccountIndex,
                    null,
                    null,
                    null,
                    false
                ).asSequence().mapNotNull {
                    dexHelper.decodeMethodIndex(it) as? Method
                }

                get = method {
                    name = calledMethods.firstOrNull {
                        it.isStatic && it.parameterTypes.size == 1 && it.parameterTypes[0] == Context::class.java && it.returnType == biliAccountsClass
                    }?.name ?: return@method
                }

                getAccessKey = method {
                    name = calledMethods.firstOrNull {
                        it.isNotStatic && it.parameterTypes.isEmpty() && it.returnType == String::class.java
                    }?.name ?: return@method
                }
            }
            themeHelper = themeHelper {
                val colorArrayMethod = dexHelper.findMethodUsingString(
                    "theme_entries_last_key",
                    false,
                    dexHelper.encodeClassIndex(Int::class.java),
                    1,
                    null,
                    -1,
                    longArrayOf(dexHelper.encodeClassIndex(Context::class.java)),
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@themeHelper

                class_ = class_ { name = colorArrayMethod.declaringClass.name }
                colorArray = field { name = colorArrayMethod.name }
            }
            themeIdHelper = themeIdHelper {
                val mWebActivityClass = "tv.danmaku.bili.ui.webview.MWebActivity".from(classloader)
                    ?: return@themeIdHelper
                val mWebActivityIndex = dexHelper.encodeClassIndex(mWebActivityClass)
                val themeIdHelperClass = dexHelper.findMethodUsingString(
                    "native.theme",
                    false,
                    -1,
                    -1,
                    null,
                    mWebActivityIndex,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.run {
                    dexHelper.findMethodInvoking(
                        this,
                        -1,
                        0,
                        "I",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass
                } ?: return@themeIdHelper
                class_ = class_ { name = themeIdHelperClass.name }
                colorId = field {
                    name = themeIdHelperClass.declaredFields.find {
                        it.type == SparseArray::class.java
                    }?.name ?: return@field
                }
            }
            columnHelper = columnHelper {
                val columnHelperClass = classesList.filter {
                    it.startsWith("com.bilibili.column.helper")
                }.firstNotNullOfOrNull { c ->
                    c.findClass(classloader).takeIf {
                        it.declaredFields.count { f ->
                            Modifier.isStatic(it.modifiers) && f.type == SparseArray::class.java
                        } > 1
                    }
                } ?: return@columnHelper
                class_ = class_ {
                    name = columnHelperClass.name ?: return@class_
                }
                colorArray = field {
                    name = columnHelperClass.declaredFields.firstOrNull {
                        it.type == SparseArray::class.java &&
                                (it.genericType as ParameterizedType).actualTypeArguments[0].toString() == "int[]"
                    }?.name ?: return@field
                }
            }
            skinList = method {
                val biliSkinListClass =
                    "tv.danmaku.bili.ui.theme.api.BiliSkinList".findClassOrNull(classloader)
                        ?: return@method
                name =
                    "tv.danmaku.bili.ui.theme.ThemeStoreActivity".findClassOrNull(classloader)?.declaredMethods?.firstOrNull {
                        it.parameterTypes.size == 2 && it.parameterTypes[0] == biliSkinListClass &&
                                it.parameterTypes[1] == Boolean::class.javaPrimitiveType
                    }?.name ?: return@method
            }
            themeProcessor = themeProcessor {
                val biliSkinListClass =
                    "tv.danmaku.bili.ui.theme.api.BiliSkinList".findClassOrNull(classloader)
                val themeProcessorClass = classesList.filter {
                    it.startsWith("tv.danmaku.bili.ui.theme")
                }.firstNotNullOfOrNull { c ->
                    c.findClass(classloader).takeIf {
                        it.declaredFields.count { f ->
                            f.type == biliSkinListClass
                        } > 1
                    }
                } ?: return@themeProcessor
                class_ = class_ { name = themeProcessorClass.name }
                methods += themeProcessorClass.declaredMethods.filter {
                    it.parameterTypes.isEmpty() && it.modifiers == 0
                }.map { method { name = it.name } }
            }
            themeListClick = class_ {
                name =
                    "tv.danmaku.bili.ui.theme.ThemeStoreActivity".findClassOrNull(classloader)?.declaredClasses?.firstOrNull {
                        it.interfaces.contains(View.OnClickListener::class.java)
                    }?.name ?: return@class_
            }
            shareWrapper = shareWrapper {
                val shareToIndex = dexHelper.findMethodUsingString(
                    "share.helper.inner",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@shareWrapper
                val shareHelperClass =
                    dexHelper.decodeMethodIndex(shareToIndex)?.declaringClass ?: return@shareWrapper
                class_ = class_ { name = shareHelperClass.name }
                val shareHelperIndex = dexHelper.encodeClassIndex(shareHelperClass)
                val stringIndex = dexHelper.encodeClassIndex(String::class.java)
                val bundleIndex = dexHelper.encodeClassIndex(Bundle::class.java)
                val shareMethod = dexHelper.findMethodInvoking(
                    shareToIndex,
                    -1,
                    2,
                    "VLL",
                    shareHelperIndex,
                    longArrayOf(stringIndex, bundleIndex),
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@shareWrapper
                method = method { name = shareMethod.name }
            }
            themeName = themeName {
                val themeNameClass = classesList.filter {
                    it.startsWith("tv.danmaku.bili.ui.garb")
                }.map {
                    it.findClass(classloader)
                }.firstOrNull { c ->
                    c.declaredFields.count {
                        Modifier.isStatic(it.modifiers) && it.type == Map::class.java
                    } == 1
                }
                class_ = class_ {
                    name = themeNameClass?.name ?: return@class_
                }
                field = field {
                    name = themeNameClass?.declaredFields?.firstOrNull {
                        it.type == Map::class.java
                                && Modifier.isStatic(it.modifiers)
                    }?.name ?: return@field
                }
            }
            section = section {
                val sectionClass = dexHelper.findMethodUsingString(
                    "ActionViewHolder",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@section
                val likeMethod = sectionClass.superclass?.declaredMethods?.find {
                    it.parameterTypes.size == 1 && it.returnType == Void.TYPE && !it.isFinal
                } ?: return@section
                class_ = class_ { name = sectionClass.name }
                like = method { name = likeMethod.name }
            }
            signQuery = signQuery {
                val signedQueryClass =
                    "com.bilibili.nativelibrary.SignedQuery".findClassOrNull(classloader)
                        ?: return@signQuery
                val libBiliClass =
                    "com.bilibili.nativelibrary.LibBili" from classloader ?: return@signQuery
                class_ = class_ { name = libBiliClass.name }
                method = method {
                    name = libBiliClass.declaredMethods.firstOrNull {
                        it.parameterTypes.size == 1 && it.parameterTypes[0] == Map::class.java &&
                                it.returnType == signedQueryClass
                    }?.name ?: return@method
                }
            }
            settings = settings {
                val menuGroupItemClass =
                    "com.bilibili.lib.homepage.mine.MenuGroup\$Item" from classloader
                        ?: return@settings
                menuGroupItem = class_ { name = menuGroupItemClass.name }
                settingRouter = class_ {
                    name = dexHelper.findMethodUsingString(
                        "UperHotMineSolution",
                        false,
                        -1,
                        0,
                        "V",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass?.interfaces?.firstOrNull()?.let {
                        dexHelper.encodeClassIndex(it)
                    }?.let {
                        dexHelper.findField(it, null, true).asSequence().firstNotNullOfOrNull { f ->
                            dexHelper.decodeFieldIndex(f)
                        }?.declaringClass
                    }?.name ?: return@class_
                }
                val contextIndex = dexHelper.encodeClassIndex(Context::class.java)
                val listIndex = dexHelper.encodeClassIndex(List::class.java)
                dexHelper.findMethodUsingString(
                    "main.my-information.noportrait.0.show",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    false
                ).asSequence().mapNotNull { dexHelper.decodeMethodIndex(it)?.declaringClass }
                    .forEach { homeUserCenterClass ->
                        val homeUserCenterIndex = dexHelper.encodeClassIndex(homeUserCenterClass)
                        val addSettingMethod = dexHelper.findMethodUsingString(
                            "bilibili://main/scan",
                            true,
                            -1,
                            -1,
                            null,
                            homeUserCenterIndex,
                            null,
                            longArrayOf(contextIndex),
                            null,
                            false
                        ).asSequence().mapNotNull {
                            dexHelper.decodeMethodIndex(it) as? Method
                        }.firstOrNull {
                            it.parameterTypes.size == 2 &&
                                    it.parameterTypes[1] != List::class.java
                        } ?: dexHelper.findMethodUsingString(
                            "activity://main/preference",
                            true,
                            -1,
                            -1,
                            null,
                            homeUserCenterIndex,
                            null,
                            longArrayOf(contextIndex, listIndex),
                            null,
                            true
                        ).asSequence().firstNotNullOfOrNull {
                            dexHelper.decodeMethodIndex(it)
                        } ?: dexHelper.findMethodUsingString(
                            "bilibili://main/preference",
                            true,
                            -1,
                            -1,
                            null,
                            homeUserCenterIndex,
                            null,
                            longArrayOf(contextIndex, listIndex),
                            null,
                            true
                        ).asSequence().firstNotNullOfOrNull {
                            dexHelper.decodeMethodIndex(it)
                        } ?: return@settings
                        homeUserCenter += homeUserCenter {
                            class_ = class_ { name = homeUserCenterClass.name }
                            addSetting = method { name = addSettingMethod.name }
                        }
                    }
            }
            drawer = drawer {
                val navigationViewClass =
                    "android.support.design.widget.NavigationView" from classloader
                val regex = Regex("^tv\\.danmaku\\.bili\\.ui\\.main2\\.[^.]*$")
                class_ = class_ {
                    name = classesList.filter {
                        it.startsWith("tv.danmaku.bili.ui.main2")
                    }.filter { it.matches(regex) }.firstOrNull { c ->
                        c.findClass(classloader).declaredFields.any {
                            it.type == navigationViewClass
                        }
                    } ?: return@class_
                }
                val drawerLayoutClass = "androidx.drawerlayout.widget.DrawerLayout" from classloader
                    ?: "android.support.v4.widget.DrawerLayout" from classloader ?: return@drawer
                layout = class_ { name = drawerLayoutClass.name }
                layoutParams = class_ {
                    name = drawerLayoutClass.declaredClasses.firstOrNull {
                        it.superclass == ViewGroup.MarginLayoutParams::class.java
                    }?.name ?: return@class_
                }
                try {
                    open = method {
                        name = drawerLayoutClass.getMethod(
                            "openDrawer",
                            View::class.java,
                            Boolean::class.javaPrimitiveType
                        ).name ?: throw Throwable()
                    }
                    close = method {
                        name = drawerLayoutClass.getMethod(
                            "closeDrawer",
                            View::class.java,
                            Boolean::class.javaPrimitiveType
                        ).name ?: throw Throwable()
                    }
                } catch (e: Throwable) {
                    val a = drawerLayoutClass.declaredMethods.filter {
                        Modifier.isPublic(it.modifiers) &&
                                it.parameterTypes.size == 2 && it.parameterTypes[0] == View::class.java &&
                                it.parameterTypes[1] == Boolean::class.javaPrimitiveType
                    }.map { it.name }.toTypedArray()
                    if (a.size == 2) {
                        open = method { name = a[0] }
                        close = method { name = a[1] }
                    }
                }
                isOpen = method {
                    name = runCatchingOrNull {
                        drawerLayoutClass.getMethod("isDrawerOpen", View::class.java).name
                    } ?: drawerLayoutClass.declaredMethods.firstOrNull {
                        Modifier.isPublic(it.modifiers) &&
                                it.parameterTypes.size == 1 && it.parameterTypes[0] == View::class.java &&
                                it.returnType == Boolean::class.javaPrimitiveType
                    }?.name ?: return@method
                }
            }
            downloadThread = downloadThread {
                val viewHostClass = (if (platform == "android_hd")
                    "tv.danmaku.bili.ui.offline.HdOfflineDowningFragment".from(classloader)
                else
                    "tv.danmaku.bili.ui.offline.DownloadingActivity".from(classloader))
                    ?: return@downloadThread
                viewHost = class_ { name = viewHostClass.name }
                field = field {
                    name = viewHostClass.declaredFields.find {
                        it.type == Int::class.javaPrimitiveType
                    }?.name ?: return@downloadThread
                }
                val onTaskCountClickMethod = viewHostClass.declaredMethods.find { m ->
                    m.isSynthetic && m.parameterTypes.let {
                        it.size == 4 && if (platform == "android_hd") {
                            it[0] == TextView::class.java && it[1] == viewHostClass
                        } else it[0] == viewHostClass && it[1] == TextView::class.java
                    }
                } ?: return@downloadThread
                val listenerClass = dexHelper.findMethodInvoked(
                    dexHelper.encodeMethodIndex(onTaskCountClickMethod),
                    -1,
                    1,
                    "VL",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@downloadThread
                listener = class_ { name = listenerClass.name }
                val reportMethod = dexHelper.findMethodUsingString(
                    "meantime",
                    false,
                    -1,
                    -1,
                    null,
                    dexHelper.encodeClassIndex(viewHostClass),
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.run {
                    dexHelper.findMethodInvoking(
                        this,
                        -1,
                        2,
                        "VLI",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }
                } ?: return@downloadThread
                reportDownload = reportDownload {
                    class_ = class_ { name = reportMethod.declaringClass.name }
                    method = method { name = reportMethod.name }
                }
            }
            musicNotification = musicNotification {
                val bitmapIndex = dexHelper.encodeClassIndex(Bitmap::class.java)
                val notificationIndex = dexHelper.encodeClassIndex(Notification::class.java)
                val musicNotificationHelperClass = dexHelper.findMethodUsingString(
                    "buildNewJBNotification",
                    true,
                    notificationIndex,
                    1,
                    "LL",
                    -1,
                    longArrayOf(bitmapIndex),
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@musicNotification
                helper = class_ { name = musicNotificationHelperClass.name }
                liveHelper = class_ {
                    name = dexHelper.findMethodUsingString(
                        "buildLiveNotification",
                        true,
                        notificationIndex,
                        1,
                        "LL",
                        -1,
                        longArrayOf(bitmapIndex),
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass?.name ?: return@class_
                }
                val musicNotificationHelperIndex =
                    dexHelper.encodeClassIndex(musicNotificationHelperClass)
                val musicManagerIndex = dexHelper.findMethodUsingString(
                    "the build sdk >= 8.0",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@musicNotification
                builder = notificationBuilder {
                    dexHelper.findMethodInvoking(
                        musicManagerIndex,
                        -1,
                        0,
                        "L",
                        musicNotificationHelperIndex,
                        null,
                        null,
                        null,
                        false
                    ).asSequence().flatMap {
                        dexHelper.findMethodInvoking(
                            it,
                            -1,
                            1,
                            "VL",
                            musicNotificationHelperIndex,
                            null,
                            null,
                            null,
                            false
                        ).asSequence()
                    }.firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.let {
                        class_ = class_ {
                            name = (it as? Method)?.parameterTypes?.get(0)?.name ?: return@class_
                        }
                        method = method { name = it.name }
                    }
                }
                val backgroundPlayerClass = dexHelper.findMethodUsingString(
                    "backgroundPlayer status changed",
                    true,
                    -1,
                    2,
                    "VIZ",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@musicNotification
                backgroundPlayer = class_ { name = backgroundPlayerClass.name }
                val setStateMethod = dexHelper.findMethodUsingString(
                    "MediaSession setPlaybackState",
                    true,
                    -1,
                    1,
                    "VI",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@musicNotification
                setState = method { name = setStateMethod.name }
                absMusicService = class_ { name = setStateMethod.declaringClass.name }
                val playerServiceClass = dexHelper.findMethodUsingString(
                    "mPlayerServiceManager",
                    true,
                    -1,
                    0,
                    "L",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass?.superclass?.interfaces?.firstOrNull()
                playerService = class_ { name = playerServiceClass?.name ?: return@class_ }
                mediaSessionCallback = class_ {
                    name = classesList.filter {
                        it.startsWith("android.support.v4.media.session")
                    }.firstOrNull { c ->
                        c.findClass(classloader).declaredMethods.any {
                            it.name == "onSeekTo"
                        }
                    } ?: return@class_
                }
                val musicBackgroundPlayerClass = dexHelper.findMethodUsingString(
                    "MusicBackgroundPlayBack call resetPendingState",
                    true,
                    -1,
                    0,
                    "V",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: dexHelper.findMethodUsingString(
                    "MusicBackgroundPlayBack status changed",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass

                musicBackgroundPlayer = class_ {
                    name = musicBackgroundPlayerClass?.name ?: return@class_
                }
                val musicWrapperPlayerClass = dexHelper.findMethodUsingString(
                    "call playNextVideo",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass
                musicWrapperPlayer = field {
                    name = musicBackgroundPlayerClass?.declaredFields?.firstOrNull {
                        it.type == musicWrapperPlayerClass
                    }?.name ?: return@field
                }
                val ifs = musicWrapperPlayerClass?.interfaces?.flatMap {
                    it.interfaces.asSequence() ?: arrayOf(it).asSequence()
                } ?: return@musicNotification
                val musicPlayerField = musicWrapperPlayerClass.declaredFields.firstOrNull {
                    ifs.contains(it.type.interfaces.firstOrNull())
                } ?: return@musicNotification
                musicPlayer = field {
                    name = musicPlayerField.name
                }
                musicPlayerService = field {
                    name = musicPlayerField.type?.declaredFields?.firstOrNull {
                        it.type == playerServiceClass
                    }?.name ?: return@field
                }
            }
            bangumiParams = bangumiParams {
                val bangumiParamsClass = dexHelper.findMethodUsingString(
                    "UniformSeasonParams(",
                    true,
                    -1,
                    0,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@bangumiParams
                class_ = class_ { name = bangumiParamsClass.name }
                paramsToMap = method {
                    name = bangumiParamsClass.declaredMethods.firstOrNull {
                        it.returnType == java.util.Map::class.java
                    }?.name ?: return@method
                }
            }
            gsonHelper = gsonHelper {
                val gsonClass = dexHelper.findMethodUsingString(
                    "AssertionError (GSON",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@gsonHelper
                val gsonConverterClass =
                    dexHelper.findField(dexHelper.encodeClassIndex(gsonClass), null, false)
                        .asSequence().firstNotNullOfOrNull {
                            dexHelper.decodeFieldIndex(it)?.takeIf { f ->
                                f.isStatic && f.isFinal && f.isPublic
                            }?.declaringClass?.takeIf { c ->
                                c.declaredMethods.any { m ->
                                    m.returnType == gsonClass && m.isNotStatic
                                }
                            }
                        } ?: return@gsonHelper
                gsonConverter = class_ { name = gsonConverterClass.name }
                gson = field {
                    name = gsonConverterClass.declaredMethods.firstOrNull { m ->
                        m.returnType == gsonClass && m.isNotStatic
                    }?.name ?: return@field
                }
                toJson = method {
                    name = gsonClass.declaredMethods.firstOrNull { m ->
                        m.returnType == String::class.java && m.parameterTypes.size == 1 && m.parameterTypes[0] == Object::class.java
                    }?.name ?: return@method
                }
                fromJson = method {
                    name = gsonClass.declaredMethods.firstOrNull { m ->
                        m.returnType == Object::class.java && m.parameterTypes.size == 2 && m.parameterTypes[0] == String::class.java && m.parameterTypes[1] == Class::class.java
                    }?.name ?: return@method
                }
            }
            playerCoreService = playerCoreService {
                val seekToMethod = dexHelper.findMethodUsingString(
                    "[player]seek to",
                    true,
                    -1,
                    1,
                    "VI",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: run {
                    val doSeekToIndex = dexHelper.findMethodUsingString(
                        "[player]seek to",
                        true,
                        -1,
                        2,
                        "VIZ",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull() ?: return@playerCoreService
                    val doSeekToMethod =
                        dexHelper.decodeMethodIndex(doSeekToIndex) ?: return@playerCoreService
                    val playerCoreServiceIndex =
                        dexHelper.encodeClassIndex(doSeekToMethod.declaringClass)
                    dexHelper.findMethodInvoked(
                        doSeekToIndex,
                        -1,
                        1,
                        "VI",
                        playerCoreServiceIndex,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    } ?: run {
                        dexHelper.findMethodInvoked(
                            doSeekToIndex,
                            -1,
                            2,
                            "VIZ",
                            playerCoreServiceIndex,
                            null,
                            null,
                            null,
                            true
                        ).asSequence().firstNotNullOfOrNull {
                            dexHelper.decodeMethodIndex(it)
                        }
                    }
                } ?: return@playerCoreService
                val playerCoreServiceClass = seekToMethod.declaringClass
                seekTo = method { name = seekToMethod.name }
                class_ = class_ { name = playerCoreServiceClass.name }
                val onSeekCompleteMethod = dexHelper.findMethodUsingString(
                    "[player]seek complete",
                    true,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@playerCoreService
                onSeekComplete = method { name = onSeekCompleteMethod.name }
                seekCompleteListener =
                    class_ { name = onSeekCompleteMethod.declaringClass.name }
                updateSpeed = method {
                    name = dexHelper.findMethodUsingString(
                        "[player] player speed type=",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.name ?: return@method
                }
                getDefaultSpeed = method {
                    name = dexHelper.findMethodUsingString(
                        "player_key_video_speed",
                        true,
                        -1,
                        1,
                        "FZ",
                        dexHelper.encodeClassIndex(playerCoreServiceClass),
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.name ?: return@method
                }
                setDefaultSpeed = method {
                    name = dexHelper.findMethodUsingString(
                        "player_key_video_speed",
                        true,
                        -1,
                        1,
                        "VF",
                        dexHelper.encodeClassIndex(playerCoreServiceClass),
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.name ?: return@method
                }
            }
            generalResponse = class_ {
                name = "com.bilibili.okretro.GeneralResponse"
            }
            pegasusFeed = pegasusFeed {
                val fastJSONObject =
                    dexHelper.encodeClassIndex(
                        "com.alibaba.fastjson.JSONObject" from classloader ?: return@pegasusFeed
                    )
                val pegasusFeedClass = dexHelper.findMethodUsingString(
                    "card_type is empty",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    longArrayOf(fastJSONObject),
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@pegasusFeed
                class_ = class_ { name = pegasusFeedClass.name }
                method = method {
                    name = pegasusFeedClass.declaredMethods.firstOrNull {
                        it.parameterTypes.size == 1 && it.parameterTypes[0].name == this@hookInfo.okHttp.responseBody.class_.name
                                && it.returnType != Object::class.java
                    }?.name ?: return@method
                }
            }
            popular = popular {
                class_ = class_ { name = "com.bapis.bilibili.app.show.popular.v1.PopularMoss" }
                method = method {
                    name = "index"
                }
            }
            chronosSwitch = class_ {
                val regex = Regex("""^tv\.danmaku\.chronos\.wrapper\.[^.]*$""")
                name = classesList.filter {
                    it.matches(regex)
                }.firstOrNull { c ->
                    c.findClass(classloader).declaredFields.count {
                        it.type == Boolean::class.javaObjectType
                    } >= 5
                } ?: return@class_
            }
            subtitleSpan = class_ {
                name = classesList.filter {
                    it.startsWith("tv.danmaku.danmaku.subititle") ||
                            it.startsWith("tv.danmaku.danmaku.subtitle")
                }.firstOrNull { c ->
                    c.findClass(classloader).interfaces.contains(LineBackgroundSpan::class.java)
                } ?: return@class_
            }
            val commentSpanClass = "com.bilibili.app.comm.comment2.widget.CommentExpandableTextView"
                .from(classloader) ?: dexHelper.findMethodUsingString(
                "comment.catch_on_draw_exception",
                false,
                -1,
                -1,
                null,
                -1,
                null,
                null,
                null,
                true
            ).asSequence().firstNotNullOfOrNull {
                dexHelper.decodeMethodIndex(it)
            }?.declaringClass
            commentSpan = class_ {
                name = commentSpanClass?.name ?: return@class_
            }
            commentLongClick = class_ {
                val viewIndex = dexHelper.encodeClassIndex(View::class.java)
                val onLongClickListenerIndex = dexHelper.encodeMethodIndex(
                    View::class.java.getDeclaredMethod(
                        "setOnLongClickListener",
                        View.OnLongClickListener::class.java
                    )
                )
                name = dexHelper.findMethodInvoked(
                    onLongClickListenerIndex,
                    -1,
                    2,
                    "VLL",
                    -1,
                    longArrayOf(viewIndex, -1),
                    null,
                    null,
                    false
                ).firstOrNull {
                    dexHelper.decodeMethodIndex(it)?.run {
                        this !is Constructor<*> && isStatic && isPublic && (this as? Method)?.parameterTypes.let { t ->
                            t?.get(0) == View::class.java && t[1] != CharSequence::class.java
                        }
                    } == true
                }?.let {
                    dexHelper.findMethodInvoking(it, -1, 2, "VLL", -1, null, null, null, false)
                        .asSequence()
                        .firstNotNullOfOrNull { m -> dexHelper.decodeMethodIndex(m) }?.declaringClass?.name
                } ?: return@class_
            }
            commentLongClickNew = class_ {
                val setExpandLinesMethod = commentSpanClass?.declaredMethods?.find {
                    it.parameterTypes.size == 1 && it.parameterTypes[0] == Int::class.javaPrimitiveType
                } ?: return@class_
                val setExpandLinesIndex = dexHelper.encodeMethodIndex(setExpandLinesMethod)
                name = dexHelper.findMethodInvoked(
                    setExpandLinesIndex,
                    -1,
                    1,
                    "VL",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.findMethodInvoking(it, -1, 2, "VLL", -1, null, null, null, true)
                        .map { m -> dexHelper.decodeMethodIndex(m) }
                        .firstOrNull()?.declaringClass?.name
                } ?: return@class_
            }
            okio = okIO {
                val responseClassIndex =
                    dexHelper.encodeClassIndex(responseBodyClass ?: return@okIO)
                val createMethodIndex = dexHelper.findMethodUsingString(
                    "source == null",
                    false,
                    -1,
                    -1,
                    null,
                    responseClassIndex,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@okIO
                val wrapperClass = dexHelper.findMethodInvoking(
                    createMethodIndex,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    false
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it).takeIf { m ->
                        m is Constructor<*>
                    }
                }?.declaringClass ?: return@okIO
                class_ = class_ { name = wrapperClass.name }
                val okioField = wrapperClass.declaredFields.firstOrNull { f ->
                    f.type.isAbstract
                } ?: return@okIO
                field = field { name = okioField.name }
                val okioLenField = wrapperClass.declaredFields.firstOrNull { f ->
                    f.type == Long::class.javaPrimitiveType
                } ?: return@okIO
                length = field { name = okioLenField.name }
            }
            okioBuffer = okIOBuffer {
                val okioBufferClass = dexHelper.findMethodUsingString(
                    "already attached to a buffer",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@okIOBuffer
                class_ = class_ { name = okioBufferClass.name }
                val okioInputStreamMethod = okioBufferClass.declaredMethods.firstOrNull {
                    it.parameterTypes.isEmpty() && it.returnType == InputStream::class.java
                } ?: return@okIOBuffer
                inputStream = method { name = okioInputStreamMethod.name }
                val okioReadFromMethod = okioBufferClass.declaredMethods.firstOrNull {
                    it.parameterTypes.size == 1 && it.parameterTypes[0] == InputStream::class.java
                } ?: return@okIOBuffer
                readFrom = method { name = okioReadFromMethod.name }
            }
            classesList.filter {
                it.startsWith("com.bilibili.bplus.followinglist.module.item")
                        && View.OnLongClickListener::class.java.isAssignableFrom(it.on(classloader))
            }.let { l -> dynDescHolderListener.addAll(l.toList().map { class_ { name = it } }) }
            descCopy = descCopy {
                val descViewHolderClass = dexHelper.findMethodUsingString(
                    "AV%d",
                    false,
                    -1,
                    0,
                    "V",
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass ?: return@descCopy
                val descViewHolderIndex = dexHelper.encodeClassIndex(descViewHolderClass)
                val copyMethodIndex = dexHelper.findMethodUsingString(
                    "clipboard",
                    false,
                    -1,
                    -1,
                    null,
                    descViewHolderIndex,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@descCopy
                val toCopyMethodIndex = dexHelper.findMethodInvoked(
                    copyMethodIndex,
                    -1,
                    3,
                    "VLZL",
                    descViewHolderIndex,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@descCopy
                val viewIndex = dexHelper.encodeClassIndex(View::class.java)
                val clickableSpanIndex = dexHelper.encodeClassIndex(ClickableSpan::class.java)
                dexHelper.findMethodInvoked(
                    toCopyMethodIndex,
                    -1,
                    2,
                    "VLL",
                    -1,
                    longArrayOf(viewIndex, clickableSpanIndex),
                    null,
                    null,
                    false
                ).asSequence().mapNotNull {
                    dexHelper.decodeMethodIndex(it)
                }.forEach {
                    classes += class_ { name = it.declaringClass.name }
                    methods += method { name = it.name }
                }
            }
            dexHelper.findMethodUsingString(
                "BangumiAllButton",
                true, -1, 0, null, -1, null, null, null, true
            ).firstOrNull()?.let {
                dexHelper.decodeMethodIndex(it)
            }?.declaringClass?.declaringClass?.let {
                bangumiSeason = class_ { name = it.name }
                bangumiSeasonActivityEntrance = method {
                    name = it.declaredMethods.firstOrNull {
                        it.parameterTypes.isEmpty() && it.genericReturnType.toString()
                            .contains("ActivityEntrance")
                    }?.name ?: return@method
                }
            }
            kanBan = kanBan {
                val statusClass =
                    "tv.danmaku.bili.ui.kanban.KanBanUserStatus".findClassOrNull(classloader)
                        ?: return@kanBan
                statusClass.runCatchingOrNull {
                    getDeclaredMethod("isUseKanBan")
                }?.let {
                    dexHelper.findMethodInvoked(
                        dexHelper.encodeMethodIndex(it),
                        -1,
                        1,
                        "VL",
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()
                }?.let {
                    dexHelper.decodeMethodIndex(it)
                }?.let {
                    class_ = class_ { name = it.declaringClass.name }
                    method = method { name = it.name }
                }
            }
            toastHelper = toastHelper {
                val showToastCallerIndex = dexHelper.findMethodUsingString(
                    "main.lessonmodel.enterdetail.change-pswd-success.click",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull() ?: return@toastHelper
                val contextIndex = dexHelper.encodeClassIndex(Context::class.java)
                val stringIndex = dexHelper.encodeClassIndex(String::class.java)
                val showToastMethod = dexHelper.findMethodInvoking(
                    showToastCallerIndex,
                    -1,
                    3,
                    "VLLI",
                    -1,
                    longArrayOf(contextIndex, stringIndex, -1),
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@toastHelper
                val toastHelperClass = showToastMethod.declaringClass
                class_ = class_ { name = toastHelperClass.name }
                show = method { name = showToastMethod.name }
                cancel = method {
                    name = toastHelperClass.declaredMethods.firstOrNull {
                        it.isStatic && it.parameterTypes.isEmpty()
                    }?.name ?: return@method
                }
            }
            brotliInputStream = class_ {
                name = dexHelper.findMethodUsingString(
                    "Brotli decoder initialization failed",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.declaringClass?.name ?: return@class_
            }
            canTryWatchVipQuality = method {
                name = dexHelper.findMethodUsingString(
                    "user is vip, cannot trywatch",
                    false,
                    dexHelper.encodeClassIndex(Boolean::class.java),
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                }?.name ?: return@method
            }
            dexHelper.findMethodUsingString(
                "player.player.story-button.0.player",
                false,
                -1,
                -1,
                null,
                -1,
                null,
                null,
                null,
                false
            ).asSequence().mapNotNull {
                val clazz = dexHelper.decodeMethodIndex(it)?.declaringClass
                val method = clazz?.declaredMethods?.find { m ->
                    m.isStatic && m.parameterTypes.size == 1 && m.parameterTypes[0] == clazz && m.returnType == Boolean::class.javaPrimitiveType
                }
                if (clazz != null && method != null) {
                    playerFullStoryWidget {
                        class_ = class_ { name = clazz.name }
                        this.method = method { name = method.name }
                    }
                } else null
            }.let { playerFullStoryWidget.addAll(it.toList()) }
            biliCall = biliCall {
                val biliCallClass = "com.bilibili.okretro.call.BiliCall".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        "Any arguments of BiliCall constructor can not be null",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass ?: return@biliCall
                val setParserMethod = biliCallClass.methods.find {
                    it.parameterTypes.size == 1 && it.parameterTypes[0].let { c ->
                        c.isInterface && c.interfaces.size == 1 && c.interfaces[0].declaredMethods.size == 1
                    }
                } ?: return@biliCall
                val requestFiled = biliCallClass.declaredFields.find {
                    it.type.name == this@hookInfo.okHttp.request.class_.name
                } ?: return@biliCall
                class_ = class_ { name = biliCallClass.name }
                parser = class_ { name = setParserMethod.parameterTypes[0].name }
                setParser = method { name = setParserMethod.name }
                request = field { name = requestFiled.name }
            }
            biliConfig = biliConfig {
                val biliConfigClass = "com.bilibili.api.BiliConfig".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        "Call BiliConfig.init() first!",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).asSequence().firstNotNullOfOrNull {
                        dexHelper.decodeMethodIndex(it)
                    }?.declaringClass ?: return@biliConfig
                class_ = class_ {
                    name = biliConfigClass.name
                }
                val biliConfigClassIdx = dexHelper.encodeClassIndex(biliConfigClass)
                val loginMethodIdx = dexHelper.findMethodUsingString(
                    "url_find_pwd_no_sms",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstOrNull() ?: return@biliConfig
                dexHelper.findMethodInvoking(
                    loginMethodIdx,
                    -1,
                    0,
                    null,
                    biliConfigClassIdx,
                    null,
                    null,
                    null,
                    true
                ).asSequence().firstNotNullOfOrNull {
                    dexHelper.decodeMethodIndex(it)
                }?.let {
                    getAppKey = method { name = it.name }
                }
            }
            dexHelper.findMethodUsingString(
                "im.chat-group.msg.repost.click",
                false,
                -1,
                -1,
                null,
                -1,
                null,
                null,
                null,
                true
            ).asSequence().firstNotNullOfOrNull {
                dexHelper.decodeMethodIndex(it) as? Method
            }?.let {
                val getContentStringMethod = it.parameterTypes[1].declaredMethods.find { m ->
                    m.returnType == String::class.java && m.parameterTypes.isEmpty()
                } ?: return@let
                onOperateClick = method { name = it.name }
                getContentString = method { name = getContentStringMethod.name }
            }
            livePagerRecyclerView = class_ {
                val liveVerticalPagerView =
                    "com.bilibili.bililive.room.ui.roomv3.vertical.widget.LiveVerticalPagerView"
                        .from(classloader) ?: return@class_
                name = liveVerticalPagerView.declaredFields.find {
                    View::class.java.isAssignableFrom(it.type)
                }?.type?.name ?: return@class_
            }
            updateInfoSupplier = updateInfoSupplier {
                val checkMethod = dexHelper.findMethodUsingString(
                    "Do sync http request.",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@updateInfoSupplier
                class_ = class_ { name = checkMethod.declaringClass.name }
                check = method { name = checkMethod.name }
            }
            playerPreloadHolder = playerPreloadHolder {
                dexHelper.findMethodUsingString(
                    "preloadKey is null",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.run {
                    val stringClassIndex = dexHelper.encodeClassIndex(String::class.java)
                    dexHelper.findMethodInvoking(
                        this,
                        stringClassIndex,
                        2,
                        "LLL",
                        -1,
                        null,
                        longArrayOf(stringClassIndex),
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }
                }?.let {
                    class_ = class_ { name = it.declaringClass.name }
                    get = method { name = it.name }
                    return@playerPreloadHolder
                }
                // 粉版 7.39.0+
                val geminiPreloadClass = dexHelper.findMethodUsingString(
                    "PreloadData(type=",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)?.declaringClass?.declaringClass
                } ?: return@playerPreloadHolder
                geminiPreloadClass.declaredMethods.firstOrNull {
                    it.isPublic && it.parameterTypes.count() == 1
                }?.let {
                    class_ = class_ { name = it.declaringClass.name }
                    get = method { name = it.name }
                }
            }
            playerSettingHelper = playerSettingHelper {
                val getDefaultQnMethod = dexHelper.findMethodUsingString(
                    "get free data failed",
                    false,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@playerSettingHelper
                class_ = class_ { name = getDefaultQnMethod.declaringClass.name }
                getDefaultQn = method { name = getDefaultQnMethod.name }
            }
            liveRtcHelper = liveRtcHelper {
                val liveRtcEnable = dexHelper.findMethodUsingString(
                    "systemSupportLiveP2P",
                    true,
                    dexHelper.encodeClassIndex(Boolean::class.java),
                    0,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@liveRtcHelper
                liveRtcEnableClass = class_ { name = liveRtcEnable.declaringClass.name }
                liveRtcEnableMethod = method { name = liveRtcEnable.name }
            }
            val themeColorsClass = dexHelper.findMethodUsingString(
                "GarbThemeColors(garb=",
                false,
                -1,
                -1,
                null,
                -1,
                null,
                null,
                null,
                true
            ).firstOrNull()?.let {
                dexHelper.decodeMethodIndex(it)
            }?.declaringClass?.also {
                themeColors = class_ { name = it.name }
            }
            builtInThemes = builtInThemes {
                val themeColorsConstIdx = themeColorsClass?.declaredConstructors?.maxByOrNull {
                    it.parameterTypes.size
                }?.let { dexHelper.encodeMethodIndex(it) } ?: return@builtInThemes
                val clazz = dexHelper.findMethodInvoked(
                    themeColorsConstIdx,
                    -1,
                    -1,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.run {
                    dexHelper.findMethodInvoking(
                        this,
                        dexHelper.encodeClassIndex(Map::class.java),
                        0,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        true
                    ).firstOrNull()?.let {
                        dexHelper.decodeMethodIndex(it)
                    }
                }?.declaringClass ?: return@builtInThemes
                val field = clazz.declaredFields.firstOrNull {
                    it.type == Map::class.java
                } ?: return@builtInThemes
                class_ = class_ { name = clazz.name }
                all = field { name = field.name }
            }
            biliGlobalPreference = biliGlobalPreference {
                val clazz = "com.bilibili.base.BiliGlobalPreferenceHelper".from(classloader)
                    ?: dexHelper.findMethodUsingString(
                        "instance.bili_preference",
                        false,
                        -1,
                        -1,
                        null,
                        -1,
                        null,
                        null,
                        null,
                        false
                    ).asSequence().firstNotNullOfOrNull { idx ->
                        dexHelper.decodeMethodIndex(idx)?.declaringClass?.takeIf { !it.superclass.isAbstract }
                    } ?: return@biliGlobalPreference
                val method = clazz.declaredMethods.firstOrNull {
                    it.isStatic && it.parameterTypes.isEmpty() && it.returnType == SharedPreferences::class.java
                } ?: return@biliGlobalPreference
                class_ = class_ { name = clazz.name }
                get = method { name = method.name }
            }
            cardClickProcessor = cardClickProcessor {
                val method = dexHelper.findMethodUsingString(
                    "action:feed:dislike_reason",
                    false,
                    -1,
                    5,
                    null,
                    -1,
                    null,
                    null,
                    null,
                    true
                ).firstOrNull()?.let {
                    dexHelper.decodeMethodIndex(it)
                } ?: return@cardClickProcessor
                class_ = class_ { name = method.declaringClass.name }
                onFeedClicked = method { name = method.name }
            }
            dexHelper.findMethodUsingString(
                "PublishToFollowingConfig(visible=",
                false,
                -1,
                -1,
                null,
                -1,
                null,
                null,
                null,
                true
            ).firstOrNull()?.let {
                dexHelper.decodeMethodIndex(it)?.declaringClass
            }?.let {
                publishToFollowingConfig = class_ { name = it.name }
            }

            if (!BuildConfig.DEBUG) dexHelper.close()
        }

        @Volatile
        lateinit var instance: BiliBiliPackage
    }
}
