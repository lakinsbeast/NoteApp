package code.with.me.testroomandnavigationdrawertest.data

data class DataClassAdapter(
    val clickListener: (Int) -> Unit,
    val titleList: List<String>,
    val textList: List<String>,
    val cameraImg: List<String>,
    val audioList: List<String>,
    val draw: List<String>,
    val image: List<String>,
    val colors: List<String>
)
