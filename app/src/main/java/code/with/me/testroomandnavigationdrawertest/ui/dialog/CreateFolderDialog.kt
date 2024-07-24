package code.with.me.testroomandnavigationdrawertest.ui.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.utils.launchMainScope
import code.with.me.testroomandnavigationdrawertest.data.utils.setCancelButton
import code.with.me.testroomandnavigationdrawertest.data.utils.setChipSelectedDesign
import code.with.me.testroomandnavigationdrawertest.data.utils.setChipUnSelectedDesign
import code.with.me.testroomandnavigationdrawertest.data.utils.setConfirmButton
import code.with.me.testroomandnavigationdrawertest.databinding.CreateFolderDialogBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseDialog
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.CreateFolderViewModel
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class CreateFolderDialog() :
    BaseDialog<CreateFolderDialogBinding>(CreateFolderDialogBinding::inflate) {
//    @Inject
//    @Named("createFolderVMFactory")
//    lateinit var folderVmFactory: ViewModelProvider.Factory
    private val viewModel: CreateFolderViewModel by viewModels()
//    lazy {
//        ViewModelProvider(
//            (context as MainActivity).viewModelStore,
//            folderVmFactory,
//        )[CreateFolderViewModel::class.java]
//    }

    private var textListener =
        object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {
                binding.apply {
                    if (text != null) {
                        findTextGroupChip(text)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initTextListeners()
        binding.cancelButton.setCancelButton()
        binding.confirmButton.setConfirmButton()
        generateChip()
        setCornerRounded(binding.mainLayout)
        listenVM()
    }

    /** похоже это бесполезно, потому что вне зависимости от выбора размытие работает
     * на заднем фоне*/
    private fun listenVM() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isUseBehindBlurEnabled.collect {
                    isBehindNeedBlurred = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isUseBackgroundBlurEnabled.collect {
                    isBackgroundNeedBlurred = it
                }
            }
        }
    }

    private fun initTextListeners() {
        binding.apply {
            folderNameInput.addTextChangedListener(textListener)
        }
    }

    /** находит указанный параметром текст chip из chipGroup и выделяет белым цветом
     * при помощи .setChipSelectedDesign */
    private fun findTextGroupChip(text: CharSequence) {
        for (chip in 0 until binding.chipGroup.childCount) {
            val selectedChip = (binding.chipGroup.getChildAt(chip) as Chip)
            if (TextUtils.equals(selectedChip.text, text)) {
                selectedChip.isChecked = true
                selectedChip.setChipSelectedDesign()
            } else {
                selectedChip.isChecked = false
                selectedChip.setChipUnSelectedDesign()
            }
        }
    }

    /** проходится по списку чипов(chipGroup) и делает их "невыбранными" */
    private fun clearAllSelectedChip() {
        for (chip in 0 until binding.chipGroup.childCount) {
            (binding.chipGroup.getChildAt(chip) as Chip).setChipUnSelectedDesign()
        }
    }

    private fun initClickListeners() {
        binding.apply {
            /** при клике на любой chip ищет нужный чип и выделяет его белым цветом */
            chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                clearAllSelectedChip()
                val checkedChip = root.findViewById<Chip>(chipGroup.checkedChipId)
//                if (chipGroup.checkedChipId != -1) {
//                    viewModel.selectedChip = chipGroup.checkedChipId
//                }
                checkedChip?.let {
                    it.setChipSelectedDesign()
                    val chipText = it.text.toString()
                    viewModel.selectedText = chipText
                    folderNameInput.removeTextChangedListener(textListener)
                    folderNameInput.text.clear()
                    folderNameInput.addTextChangedListener(textListener)
                    folderNameInput.append(chipText)
                }
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
            confirmButton.setOnClickListener {
                if (folderNameInput.text.isEmpty()) {
                    folderNameInput.error = "Введите название!"
                    return@setOnClickListener
                }
                launchMainScope {
                    viewModel.insertFolder(
                        Folder(
                            folderNameInput.text.toString(),
                            System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            "",
                            isFavoriteSwitch.isChecked,
                        ),
                    )
                }
                dismiss()
            }
        }
    }

    /** заготовленные имена папок, на рандом достаются 4 штуки */
    val folderNames =
        mutableListOf(
            "🏞️ Путешествия",
            "🍽️ Еда и рецепты",
            "🎥 Развлечения",
            "💡 Идеи и планы",
            "🚗 Транспорт",
            "🎓 Образование",
            "🎂 События и праздники",
            "💰 Финансы",
            "📅 Планирование",
            "🎨 Творчество",
            "📰 Новости",
            "🏋️ Фитнес и здоровье",
            "🏡 Дом и быт",
            "🌍 Планы на путешествия",
            "📚 Книги и чтение",
            "🎯 Цели и достижения",
            "🎵 Музыка",
            "💼 Работа и задачи",
            "🌆 Городская жизнь",
            "🌱 Хобби и увлечения",
            "💖 Личное",
            "👨‍👩‍👧‍👦 Семья и друзья",
            "🐶 Питомцы",
            "🛠️ DIY и ремонт",
            "🛍️ Покупки и шопинг",
            "💻 Технологии",
            "🩺 Медицина и здоровье",
            "🏛️ Культура и искусство",
            "🌳 Природа и экология",
            "🚀 Наука и космос",
            "🙏 Благотворительность",
            "🎧 Подкасты и аудиокниги",
            "🎬 Кино и сериалы",
            "🎮 Игры",
            "🎤 Музыкальные инструменты",
            "✍️ Письмо и журналистика",
            "📸 Фотография",
            "💃🕺 Танцы",
            "🤸‍♀️ Спорт",
            "🧘‍♀️ Медитация и йога",
        )

    /** создает 4 chip'a со случайным названием из folderNames */
    private fun generateChip() {
        binding.chipGroup.removeAllViewsInLayout()
        repeat(4) {
            val chip = Chip(context)
            chip.isCheckable = true
            val index = folderNames.indices.random()
            chip.text = folderNames[index]
            folderNames.removeAt(index)
            binding.chipGroup.addView(chip)
        }
        clearAllSelectedChip()
    }
}
