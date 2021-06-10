package com.example.mamamiadelivery.Connections

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.properties.AppProperties
import com.example.mamamiadelivery.properties.AppProperties.linkToUpdateApkFile
import com.example.mamamiadelivery.ui.RequestPermissionForApp
import com.example.mamamiadelivery.ui.settings.Setting
import com.example.mamamiadelivery.ui.settings.Setting.setVersionLayoutContent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class HttpCheckAndDownloadUpdate (){
    var isSuccessSoapSession = false
    private var error_text = ""
    private var release_already_exist = false
    private var url_folder: String? = ""
    private val file_name_last_version = "last_version.txt"
    private var last_version_release_file_name = ""
    private val app_base_name = "MamamiaDelivery"
    private var local_downloads_dir: File? = null
    private var currentVersionName: String? = null
    private var apkFileVersionName: String? = null
    private var currentVersionCode: Int? = null
    private var apkFileVersionCode: Int? = null

    fun run() {
        val subscribe = Single.fromCallable {
            try { // чтоб небыло проблем при загрузке приложения
                if (!lastVersionReleaseName) {
                    return@fromCallable null
                }
                if (!downloadLastReleaseFile()) {
                    return@fromCallable null
                }
                if (release_already_exist) {
                    //TODO наверное нам это даже неважно, но на будущее заглушка есть
                }
                if (currentAppVersion && appVersionFromApk) {
                    if (currentVersionCode!! <= apkFileVersionCode!!) {
                        if (currentVersionCode === apkFileVersionCode) {
                            if (currentVersionName == apkFileVersionName) {
                                return@fromCallable null // если нет релиза - ничего и не надо делать - выход
                            }
                        }
                        // устанавливаем глобальные переменные про обновление
                        AppProperties.currentAppVersion = currentVersionName!!
                        AppProperties.availableAppVersion = apkFileVersionName!!
                        linkToUpdateApkFile =
                            local_downloads_dir.toString() + "/" + last_version_release_file_name
                        AppProperties.isUpdateAvailable = true
                    }
                }
                isSuccessSoapSession = true
            } catch (e: Exception) {
            }
        }.doOnSubscribe {
            RequestPermissionForApp.RequestPermissionForWriteExternalStorage()
            try { // чтоб небыло проблем при загрузке приложения
                println(">>> onPreExecute >>> HttpCheckAndDownloadUpdate")
                AppProperties.isStartCheckingUpdate = true
                AppProperties.lastTime_CheckAppUpdate = Date()
                AppProperties.isUpdateAvailable = false
                val preferences =
                    PreferenceManager.getDefaultSharedPreferences(MamamiaDeliveryApplication.instance)
                url_folder = preferences.getString("url_to_update", "--")
                println("url_folder = $url_folder")
                //local_downloads_dir = new File(Environment.getDataDirectory().toString()+ File.separator + Environment.DIRECTORY_DOWNLOADS);
                local_downloads_dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString()
                )
                println("local_downloads_dir = $local_downloads_dir")
                println("local_data_dir_patch = " + local_downloads_dir!!.absoluteFile.path)
            } catch (e: Exception) {
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isSuccessSoapSession) {                    // если удачно все удачно завершилась - обновляем информацию
                    println("<<< httpSession <<< OK")
                } else {
                    println("<<< httpSession <<< ERROR")
                }
                AppProperties.isStartCheckingUpdate = false // завершилась проверка обновления
                setVersionLayoutContent()
                println("<<< onPostExecute <<< HttpCheckAndDownloadUpdate")
            }, {

            })
    }
    //############################################################################################

    //############################################################################################

    //############################################################################################
    private val lastVersionReleaseName: Boolean
        private get() {
            // download file with name of last version release apk
            var url_to_last_vers_file: URL? = null
            try {
                url_to_last_vers_file = URL(url_folder + file_name_last_version)
            } catch (e1: MalformedURLException) {
                error_text = "MalformedURLException - file: $file_name_last_version"
                return false
            }
            println("url_to_last_vers_file = $url_to_last_vers_file")
            val outputFile: File
            try {
                local_downloads_dir!!.mkdirs() // создаем директорию если ее нет
                outputFile = File(
                    local_downloads_dir,
                    file_name_last_version
                ) // назначаем имя загружаемого файла
                println("outputFile = $outputFile")
                if (outputFile.exists()) {
                    outputFile.delete()
                } // если есть файл с таким именем - удаляем его
            } catch (e: Exception) {
                error_text = "problem with local file name:$file_name_last_version"
                //publishProgress("Проблема c локальным именем загружаемого файла или директории");
                return false
            }
            if (!downloadFile(url_to_last_vers_file.toString(), outputFile)) {
                return false
            }

            //Read first line from file
            val first_line: String
            try {
                val br = BufferedReader(FileReader(outputFile))
                first_line = br.readLine()
                br.close()
                if (first_line == null) {
                    error_text = "first line is null into file:$file_name_last_version"
                    return false
                }
                last_version_release_file_name = first_line.trim { it <= ' ' }
            } catch (e: IOException) {
                error_text = "IOException - read first line from file:$file_name_last_version"
                return false
            }
            return true
        }

    //############################################################################################
    private fun downloadLastReleaseFile(): Boolean {
        // download last release apk-file
        var url_to_last_release_file: URL? = null
        try {
            url_to_last_release_file = URL(url_folder + last_version_release_file_name)
        } catch (e1: MalformedURLException) {
            error_text = "MalformedURLException - file: $last_version_release_file_name"
            return false
        }
        println("last_version_release_file_name = $last_version_release_file_name")
        val outputFile: File
        try {
            local_downloads_dir!!.mkdirs() // создаем директорию если ее нет
            outputFile = File(
                local_downloads_dir,
                last_version_release_file_name
            ) // назначаем имя загружаемого файла
            println("outputFile = $outputFile")
            if (outputFile.exists()) { // если есть файл с таким именем
                release_already_exist = true
                return true
            } else { // загружаем файл
                if (!downloadFile(url_to_last_release_file.toString(), outputFile)) {
                    return false
                }
            }
        } catch (e: Exception) {
            error_text = "problem with local file name :$last_version_release_file_name"
            //publishProgress("Проблема c локальным именем загружаемого файла или директории");
            return false
        }
        return true
    }

    //############################################################################################
    private fun downloadFile(remote_file: String, localDownloadfile: File): Boolean {
        println(">>>>>>>>>>> START DOWNLOAD FILE")
        var url: URL? = null
        val urlConnection: HttpURLConnection
        val inputStream: InputStream
        val buffer: ByteArray
        var bufferLength: Int
        var fos: FileOutputStream? = null
        return try {
            url = URL(remote_file)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()
            fos = FileOutputStream(localDownloadfile)
            inputStream = urlConnection.inputStream
            //downloadedSize = 0;
            buffer = ByteArray(1024)
            bufferLength = 0
            // читаем со входа и пишем в выход,
            // с каждой итерацией публикуем прогресс
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fos.write(buffer, 0, bufferLength)
            }
            fos.close()
            inputStream.close()
            println("<<<<<<<<<<< FILE DOWNLOADED !!!")
            true
        } catch (e: MalformedURLException) {
            error_text = "MalformedURLException - file: $remote_file"
            println("<<<==ERROR: downloadFile-MalformedURLException !!!")
            e.printStackTrace()
            false
        } catch (e: IOException) {
            error_text = "IOException - download file: $remote_file"
            println("<<<==ERROR: downloadFile-IOException !!!")
            e.printStackTrace()
            false
        } catch (e: Exception) {
            error_text = "Exception - download file: $remote_file"
            println("<<<==ERROR: downloadFile-Exception !!!")
            e.printStackTrace()
            false
        }
    }

    //#############################################################################################
    private val currentAppVersion: Boolean
        private get() {
            try {
                val pinfo: PackageInfo? = MamamiaDeliveryApplication.instance?.getPackageManager()?.getPackageInfo(MamamiaDeliveryApplication.instance?.getPackageName().toString(), 0)
                currentVersionCode = pinfo?.versionCode
                currentVersionName = pinfo?.versionName
                println(">>> currentVersionCode = " + currentVersionCode.toString() + " = currentVersionName = " + currentVersionName)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return false
            }
            return true
        }

    //#############################################################################################
    private val appVersionFromApk: Boolean
        private get() {
            try {
                val pm: PackageManager? = MamamiaDeliveryApplication.instance?.getPackageManager()
                val fullPath = local_downloads_dir.toString() + "/" + last_version_release_file_name
                val info = pm?.getPackageArchiveInfo(fullPath, 0)
                apkFileVersionCode = info!!.versionCode
                apkFileVersionName = info.versionName
                println(">>> apkFileVersionCode = " + apkFileVersionCode.toString() + " = apkFileVersionName = " + apkFileVersionName)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }



}