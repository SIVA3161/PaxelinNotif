package com.paxel.paxelinnotif

import android.app.Notification
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.paxel.paxelinnotif.databinding.MainFragmentBinding
import kotlin.random.Random

/**
 * Created by Siva G Gurusamy on 09,Feb,2022
 * email : siva@paxel.co
 */
class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        removeBrokenChannel()
        initNotificationChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            showNotification()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this.requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${requireContext().packageName}/${R.raw.pxl_sound}"))
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(Random.nextInt(), builder.build())
    }

    fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelName = getString(R.string.high_channel_title)
        val channelDescription = getString(R.string.high_channel_description)
        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance).apply {
            setName(channelName)
            setDescription(channelDescription)
            setSound(
                Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${requireContext().packageName}/raw/pxl_sound"),
                Notification.AUDIO_ATTRIBUTES_DEFAULT
            )
        }
        NotificationManagerCompat.from(requireContext()).createNotificationChannel(channel.build())
    }

    private fun getChannelsInfo(): String =
        NotificationManagerCompat.from(requireContext())
            .notificationChannelsCompat
            .joinToString(separator = "\n,") { "id =[${it.id}] with sound URI =[${it.sound}]" }
            .takeIf { it.isNotEmpty() } ?: "empty channel info"

    private fun removeBrokenChannel() {
        NotificationManagerCompat.from(requireContext())
            .deleteNotificationChannel(BROKEN_CHANNEL_ID)
    }


    companion object {
        const val BROKEN_CHANNEL_ID: String = "general_channel"
        const val CHANNEL_ID: String = "general_channel_new"
    }
}