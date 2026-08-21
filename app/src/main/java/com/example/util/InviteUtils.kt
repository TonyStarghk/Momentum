package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object InviteUtils {

    const val APP_WEB_BASE_URL = "https://ais-pre-7acdqz6jdhm5p4itiyuxoy-837179413272.asia-southeast1.run.app"
    const val APP_DEV_BASE_URL = "https://ais-dev-7acdqz6jdhm5p4itiyuxoy-837179413272.asia-southeast1.run.app"
    const val CUSTOM_SCHEME_PREFIX = "momentum://invite"

    /**
     * Builds a full web-accessible invite link containing user details as query params.
     */
    fun generateWebInviteUrl(
        username: String,
        name: String = "",
        region: String = "",
        avatarEmoji: String = ""
    ): String {
        val cleanHandle = if (username.startsWith("@")) username else "@$username"
        val encodedHandle = URLEncoder.encode(cleanHandle, StandardCharsets.UTF_8.toString())
        val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
        val encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8.toString())
        val encodedAvatar = URLEncoder.encode(avatarEmoji, StandardCharsets.UTF_8.toString())

        return "$APP_WEB_BASE_URL?invite=$encodedHandle&name=$encodedName&region=$encodedRegion&avatar=$encodedAvatar"
    }

    /**
     * Builds standard promotional invitation message text.
     */
    fun generateInviteMessage(
        username: String,
        name: String = ""
    ): String {
        val cleanHandle = if (username.startsWith("@")) username else "@$username"
        val inviteLink = generateWebInviteUrl(username = cleanHandle, name = name)
        return """
            ⚡ Join my Momentum habit circle! ⚡
            
            I'm tracking daily habits, consistency streaks, and climbing the tier rankings on Momentum.
            
            Add my handle: $cleanHandle
            Join via invite link: $inviteLink
            
            Let's build unstoppable daily consistency together! 🔥
        """.trimIndent()
    }

    /**
     * Copies the invite link to Android clipboard and displays a feedback toast.
     */
    fun copyInviteLink(context: Context, inviteUrl: String): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Momentum Invite Link", inviteUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "✅ Invite link copied to clipboard!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Copies the user handle/code to clipboard.
     */
    fun copyFriendCode(context: Context, handle: String): Boolean {
        return try {
            val cleanHandle = if (handle.startsWith("@")) handle else "@$handle"
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Momentum Friend Handle", cleanHandle)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "✅ Friend handle $cleanHandle copied!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Launches the system share chooser with fallback to clipboard.
     */
    fun shareInviteIntent(context: Context, username: String, name: String = "") {
        val text = generateInviteMessage(username, name)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_TITLE, "Invite to Momentum Habit Circle")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Invite Friends to Momentum").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            // Fallback to clipboard if no share activity available in container
            copyInviteLink(context, generateWebInviteUrl(username, name))
            Toast.makeText(context, "Invite copied to clipboard!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Safely opens the invite link in external browser or handler.
     */
    fun openLinkInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Link copied to clipboard: $url", Toast.LENGTH_LONG).show()
            copyInviteLink(context, url)
        }
    }

    /**
     * Parses an invite URL or raw handle string into structured friend data.
     */
    data class ParsedInvite(
        val handle: String,
        val name: String? = null,
        val region: String? = null,
        val avatarEmoji: String? = null
    )

    fun parseInviteInput(input: String): ParsedInvite? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return null

        // Case 1: URL format (https://... or http://... or momentum://...)
        if (trimmed.startsWith("http://", ignoreCase = true) || 
            trimmed.startsWith("https://", ignoreCase = true) || 
            trimmed.startsWith("momentum://", ignoreCase = true)) {
            return try {
                val uri = Uri.parse(trimmed)
                val handleParam = uri.getQueryParameter("invite")
                    ?: uri.getQueryParameter("handle")
                    ?: uri.getQueryParameter("username")
                    ?: uri.getQueryParameter("user")
                    ?: uri.lastPathSegment

                if (!handleParam.isNullOrBlank()) {
                    val decodedHandle = URLDecoder.decode(handleParam, StandardCharsets.UTF_8.toString())
                    val cleanHandle = if (decodedHandle.startsWith("@")) decodedHandle else "@$decodedHandle"
                    val nameParam = uri.getQueryParameter("name")?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    val regionParam = uri.getQueryParameter("region")?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    val avatarParam = uri.getQueryParameter("avatar")?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    ParsedInvite(
                        handle = cleanHandle,
                        name = if (!nameParam.isNullOrBlank()) nameParam else cleanHandle.removePrefix("@").replace("_", " ").replaceFirstChar { it.uppercase() },
                        region = regionParam,
                        avatarEmoji = avatarParam
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }

        // Case 2: Plain Handle or username (e.g. @alex or alex or Alex Smith)
        val cleanHandle = if (trimmed.startsWith("@")) trimmed else "@${trimmed.lowercase().replace(" ", "_")}"
        val derivedName = if (trimmed.startsWith("@")) {
            trimmed.removePrefix("@").replace("_", " ").replace(".", " ").replaceFirstChar { it.uppercase() }
        } else {
            trimmed.replaceFirstChar { it.uppercase() }
        }
        return ParsedInvite(
            handle = cleanHandle,
            name = derivedName
        )
    }
}
