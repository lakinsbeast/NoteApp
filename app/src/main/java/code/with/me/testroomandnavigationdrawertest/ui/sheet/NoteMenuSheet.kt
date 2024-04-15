package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import code.with.me.testroomandnavigationdrawertest.appComponent
import code.with.me.testroomandnavigationdrawertest.data.const.const.Companion.ROUNDED_CORNERS_SHEET
import code.with.me.testroomandnavigationdrawertest.data.const.const.Companion.ROUNDED_CORNERS_STROKE
import code.with.me.testroomandnavigationdrawertest.data.utils.setUpperRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.enums.NoteItemsCallback
import code.with.me.testroomandnavigationdrawertest.databinding.NoteMenuSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteMenuSheetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class NoteMenuSheet(private val idOfNote: Long, private val action: (NoteItemsCallback) -> Unit) :
    BaseSheet<NoteMenuSheetBinding>(NoteMenuSheetBinding::inflate) {
    @Inject
    @Named("noteMenuSheetVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: NoteMenuSheetViewModel by lazy {
        ViewModelProvider(this, factory)[NoteMenuSheetViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppComponent()
    }

    private fun initAppComponent() {
        activity?.let {
            appComponent.inject(this)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
        binding.mainLayout.setUpperRoundedCornersView(
            ROUNDED_CORNERS_SHEET,
            Color.WHITE,
            Color.BLACK,
            ROUNDED_CORNERS_STROKE,
        )
        binding.apply {
            progressBar.playAnimation()
            initClickListeners()
        }
        setStateCollapsed()
    }

    private fun NoteMenuSheetBinding.initClickListeners() {
        shareLay.setOnClickListener {
            showCustomProgressBar(true)
            viewModel.shareText(idOfNote)
        }
        moveLay.setOnClickListener {
            action.invoke(NoteItemsCallback.MOVE)
            dismiss()
        }
        // TODO сделать так, чтоб в зависимости от того в избранном он или нет менялся текст и иконка
        favoriteLay.setOnClickListener {
            action.invoke(NoteItemsCallback.FAVORITE)
            dismiss()
        }
        lockLay.setOnClickListener {
            action.invoke(NoteItemsCallback.LOCK)
            dismiss()
        }
        deleteLay.setOnClickListener {
            action.invoke(NoteItemsCallback.DELETE)
            dismiss()
        }
        closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun showCustomProgressBar(show: Boolean) {
        binding.progressBarLay.isGone = !show
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.shareTextLiveData.drop(1).flowOn(Dispatchers.Main.immediate)
                    .collect { shareText ->
                        if (shareText.isNotBlank()) {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, shareText)
                            startActivity(Intent.createChooser(intent, "Поделиться текстом с"))
                        }
                        showCustomProgressBar(false)
                    }
            }
        }
    }
}
