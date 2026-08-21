package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, zero-latency procedural audio synthesizer & soft haptics engine.
 * Delivers studio-grade acoustic chimes, harmonic tones, and tactile feedback for habit achievements.
 */
class FeedbackManager(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    // ==========================================
    // Public Achievement Feedback Triggers
    // ==========================================

    /**
     * Triggered when a habit is completed / checked in.
     * Ascending crystalline bell chime + soft tactile double-tap.
     */
    fun onHabitCompleted() {
        triggerHapticSoftDoubleTap()
        playChimeSound(
            notes = listOf(
                Note(freq = 523.25f, startMs = 0, durationMs = 280, volume = 0.45f),   // C5
                Note(freq = 659.25f, startMs = 45, durationMs = 300, volume = 0.55f),  // E5
                Note(freq = 1046.50f, startMs = 90, durationMs = 420, volume = 0.65f)  // C6
            )
        )
    }

    /**
     * Triggered when a habit completion is undone.
     * Subtle soft descending pop + gentle tick.
     */
    fun onHabitUncompleted() {
        triggerHapticLightTick()
        playChimeSound(
            notes = listOf(
                Note(freq = 493.88f, startMs = 0, durationMs = 120, volume = 0.35f),   // B4
                Note(freq = 369.99f, startMs = 30, durationMs = 150, volume = 0.30f)   // F#4
            )
        )
    }

    /**
     * Triggered when all habits for the selected day are completed (100% Perfect Day).
     * Radiant 5-note major arpeggio flourish + celebratory triple pulse.
     */
    fun onPerfectDayAchieved() {
        triggerHapticCelebrationPulse()
        playChimeSound(
            notes = listOf(
                Note(freq = 523.25f, startMs = 0, durationMs = 400, volume = 0.40f),    // C5
                Note(freq = 659.25f, startMs = 70, durationMs = 450, volume = 0.45f),   // E5
                Note(freq = 783.99f, startMs = 140, durationMs = 500, volume = 0.55f),  // G5
                Note(freq = 1046.50f, startMs = 210, durationMs = 600, volume = 0.70f), // C6
                Note(freq = 1318.51f, startMs = 280, durationMs = 750, volume = 0.75f)  // E6
            )
        )
    }

    /**
     * Triggered on streak milestones (e.g., 3, 7, 14, 30, 100 days).
     * Shimmering crystal chime + victory cadence.
     */
    fun onStreakMilestoneAchieved() {
        triggerHapticVictoryPulse()
        playChimeSound(
            notes = listOf(
                Note(freq = 587.33f, startMs = 0, durationMs = 350, volume = 0.45f),    // D5
                Note(freq = 739.99f, startMs = 60, durationMs = 400, volume = 0.55f),   // F#5
                Note(freq = 880.00f, startMs = 120, durationMs = 450, volume = 0.65f),  // A5
                Note(freq = 1174.66f, startMs = 180, durationMs = 600, volume = 0.75f)  // D6
            )
        )
    }

    /**
     * Triggered when cheering someone in the leaderboard arena.
     * Soft buoyant heart pop + gentle tap.
     */
    fun onCheerTriggered() {
        triggerHapticLightTick()
        playChimeSound(
            notes = listOf(
                Note(freq = 784.0f, startMs = 0, durationMs = 160, volume = 0.40f),
                Note(freq = 1175.0f, startMs = 40, durationMs = 260, volume = 0.50f)
            )
        )
    }

    /**
     * Triggered when a new habit is created or updated.
     * Warm harmonic affirmative chord.
     */
    fun onHabitCreatedOrSaved() {
        triggerHapticSoftTap()
        playChimeSound(
            notes = listOf(
                Note(freq = 440.00f, startMs = 0, durationMs = 220, volume = 0.40f),
                Note(freq = 554.37f, startMs = 40, durationMs = 280, volume = 0.50f),
                Note(freq = 659.25f, startMs = 80, durationMs = 340, volume = 0.60f)
            )
        )
    }

    /**
     * Triggered when sharing progress card or copying to clipboard.
     */
    fun onShareTriggered() {
        triggerHapticSoftTap()
        playChimeSound(
            notes = listOf(
                Note(freq = 659.25f, startMs = 0, durationMs = 180, volume = 0.40f),
                Note(freq = 880.00f, startMs = 50, durationMs = 240, volume = 0.50f)
            )
        )
    }

    /**
     * Triggered when user advances to a new Tier (e.g. Amateur -> Rookie -> ... -> Champion).
     * Grand triumphal 6-note major fanfare + prolonged celebratory victory pulse.
     */
    fun onTierUpgraded() {
        triggerHapticCelebrationPulse()
        playChimeSound(
            notes = listOf(
                Note(freq = 440.00f, startMs = 0, durationMs = 200, volume = 0.45f),   // A4
                Note(freq = 554.37f, startMs = 60, durationMs = 220, volume = 0.50f),  // C#5
                Note(freq = 659.25f, startMs = 120, durationMs = 260, volume = 0.55f), // E5
                Note(freq = 880.00f, startMs = 180, durationMs = 300, volume = 0.65f), // A5
                Note(freq = 1108.73f, startMs = 240, durationMs = 360, volume = 0.70f),// C#6
                Note(freq = 1318.51f, startMs = 300, durationMs = 650, volume = 0.80f) // E6
            )
        )
    }

    /**
     * Triggered when a streak freeze is activated / consumed to protect consistency.
     * Crystalline frost sound + double soft tick.
     */
    fun onStreakFreezeUsed() {
        triggerHapticSoftDoubleTap()
        playChimeSound(
            notes = listOf(
                Note(freq = 1046.50f, startMs = 0, durationMs = 150, volume = 0.45f), // C6
                Note(freq = 1318.51f, startMs = 40, durationMs = 200, volume = 0.55f),// E6
                Note(freq = 1567.98f, startMs = 80, durationMs = 350, volume = 0.60f) // G6
            )
        )
    }

    /**
     * Triggered when a streak is broken or consistency penalty is assessed (-XP).
     * Low solemn descending minor chime + alert tick.
     */
    fun onStreakBrokenPenalty() {
        triggerHapticWarningTick()
        playChimeSound(
            notes = listOf(
                Note(freq = 349.23f, startMs = 0, durationMs = 250, volume = 0.45f),  // F4
                Note(freq = 311.13f, startMs = 80, durationMs = 300, volume = 0.40f),  // Eb4
                Note(freq = 261.63f, startMs = 160, durationMs = 450, volume = 0.35f)  // C4
            )
        )
    }

    // ==========================================
    // Soft Tactile Haptic Generators
    // ==========================================

    private fun triggerHapticLightTick() {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(18, 60))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(18)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticWarningTick() {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 60, 40)
                val amplitudes = intArrayOf(0, 160, 0, 120)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 40, 60, 40), -1)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticSoftTap() {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(28, 110))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(28)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticSoftDoubleTap() {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 22, 45, 28)
                val amplitudes = intArrayOf(0, 90, 0, 150)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 22, 45, 28), -1)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticCelebrationPulse() {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 25, 45, 35, 45, 50)
                val amplitudes = intArrayOf(0, 80, 0, 140, 0, 210)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 25, 45, 35, 45, 50), -1)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticVictoryPulse() {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 30, 40, 35, 40, 40)
                val amplitudes = intArrayOf(0, 100, 0, 160, 0, 180)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 30, 40, 35, 40, 40), -1)
            }
        } catch (_: Exception) {}
    }

    // ==========================================
    // Procedural Audio Synthesizer Engine
    // ==========================================

    private data class Note(
        val freq: Float,
        val startMs: Int,
        val durationMs: Int,
        val volume: Float
    )

    private fun playChimeSound(notes: List<Note>) {
        if (!soundEnabled) return

        coroutineScope.launch {
            try {
                val sampleRate = 44100
                val totalDurationMs = notes.maxOf { it.startMs + it.durationMs } + 20
                val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
                val audioBuffer = FloatArray(totalSamples)

                for (note in notes) {
                    val startSample = (sampleRate * (note.startMs / 1000.0)).toInt()
                    val noteSamples = (sampleRate * (note.durationMs / 1000.0)).toInt()
                    val decayRate = 5.0 / noteSamples

                    for (i in 0 until noteSamples) {
                        val bufferIdx = startSample + i
                        if (bufferIdx >= totalSamples) break

                        val t = i.toDouble() / sampleRate

                        // Multi-harmonic glass bell synthesis:
                        // Fundamental + 2nd overtone + 3rd harmonic for rich crystalline shimmer
                        val fundamental = sin(2.0 * PI * note.freq * t)
                        val overtone1 = 0.28 * sin(2.0 * PI * (note.freq * 2.0) * t)
                        val overtone2 = 0.12 * sin(2.0 * PI * (note.freq * 3.01) * t)

                        // Smooth attack envelope (5ms) to prevent audio clicks
                        val attackSamples = (sampleRate * 0.005).toInt()
                        val attack = if (i < attackSamples) i.toFloat() / attackSamples else 1.0f

                        // Exponential decay envelope
                        val envelope = attack * exp(-decayRate * i).toFloat()

                        val sampleValue = ((fundamental + overtone1 + overtone2) * note.volume * envelope).toFloat()
                        audioBuffer[bufferIdx] += sampleValue
                    }
                }

                // Convert float buffer to 16-bit PCM short array with smooth clamping
                val pcmBuffer = ShortArray(totalSamples)
                for (i in 0 until totalSamples) {
                    val clamped = audioBuffer[i].coerceIn(-1.0f, 1.0f)
                    pcmBuffer[i] = (clamped * Short.MAX_VALUE).toInt().toShort()
                }

                // Playback via low-latency static AudioTrack
                val bufferSize = pcmBuffer.size * 2
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(pcmBuffer, 0, pcmBuffer.size)
                audioTrack.play()

                // Release track after playback completes
                launch(Dispatchers.IO) {
                    kotlinx.coroutines.delay(totalDurationMs.toLong() + 100L)
                    try {
                        audioTrack.stop()
                        audioTrack.release()
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {
                // Graceful fallback if audio device is unavailable
            }
        }
    }
}
