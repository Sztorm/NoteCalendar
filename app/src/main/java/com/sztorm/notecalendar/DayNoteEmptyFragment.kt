package com.sztorm.notecalendar

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayNoteEmptyBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber
import java.time.LocalDate

class DayNoteEmptyFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteEmptyBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment
    private var args: Arguments? = null

    constructor(dayFragment: DayFragment, args: Arguments? = null) : this() {
        this.dayFragment = dayFragment
        this.args = args
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteEmptyBinding.inflate(inflater, container, false)
        setTheme()
        setBtnNoteAddClickListener()
        setBtnNoteUndoDeletionClickListener()
        handleArgs(args)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        val args = this.args

        if (args is UndoNoteDeleteOption) {
            deleteNote(args.note)
            this.args = null
        }
    }

    private fun handleArgs(args: Arguments?) {
        when (args) {
            is CreateOrEditNoteRequest -> {
                dayFragment.setFragment(DayNoteAddFragment(dayFragment))
                this.args = null
            }

            is UndoNoteDeleteOption -> {
                val note: NoteData = args.note
                val noteDeletionCallback = NoteDeletionCallback(mainActivity, note)
                val swipeListener = object : OnDirectionalSwipeListener {
                    override fun onSwipe(
                        direction: SwipeDirection, swipeTouchListener: OnSwipeListener
                    ) {
                        dayFragment.swipeListener.removeOnSwipeListener(this)
                        mainActivity.application
                            .unregisterActivityLifecycleCallbacks(noteDeletionCallback)

                        if (direction == SwipeDirection.Left || direction == SwipeDirection.Right) {
                            deleteNote(note)
                        }
                    }
                }
                binding.btnUndoDeletion.visibility = View.VISIBLE
                mainActivity.application.registerActivityLifecycleCallbacks(noteDeletionCallback)
                dayFragment.swipeListener.addOnSwipeListener(swipeListener)
            }
        }
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        themePainter.paintButton(binding.btnNoteAdd)
        themePainter.paintButton(binding.btnUndoDeletion)
    }

    private fun deleteNote(note: NoteData) {
        NoteRepository.delete(note)

        if (mainActivity.notificationManager
                .tryCancelScheduledNotification(LocalDate.parse(note.date))
        ) {
            Timber.i("${LogTags.NOTIFICATIONS} Canceled notification after note deletion")
        }
    }

    private fun setBtnNoteAddClickListener() =
        binding.btnNoteAdd.setOnClickListener {
            dayFragment.setFragment(DayNoteAddFragment(dayFragment))
        }

    private fun setBtnNoteUndoDeletionClickListener() =
        binding.btnUndoDeletion.setOnClickListener {
            dayFragment.setFragment(
                DayNoteFragment(dayFragment, (args as UndoNoteDeleteOption).note)
            )
        }

    private class NoteDeletionCallback(
        val mainActivity: MainActivity, val note: NoteData
    ) : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

        override fun onActivityStarted(activity: Activity) { }

        override fun onActivityResumed(activity: Activity) { }

        override fun onActivityPaused(activity: Activity) { }

        override fun onActivityStopped(activity: Activity) { }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

        override fun onActivityDestroyed(activity: Activity) {
            activity.application.unregisterActivityLifecycleCallbacks(this)
            NoteRepository.delete(note)

            if (mainActivity.notificationManager
                    .tryCancelScheduledNotification(LocalDate.parse(note.date))
            ) {
                Timber.i("${LogTags.NOTIFICATIONS} Canceled notification after note deletion")
            }
        }
    }
}

