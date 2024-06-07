import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
// Constructor para inicializar los campos con valores predeterminados.
// Requiere al menos Android Oreo (API 26) debido a la anotación @RequiresApi.
@IgnoreExtraProperties
data class BookModel @RequiresApi(Build.VERSION_CODES.O) constructor(
    var id: String = "",
    val userId: String = "",
    val lockerId: String = "",
    val lockerName: String = "",
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val userEmail: String = "",
    val userName: String = "",
    val status: String = ""
)
// Formateador de fechas que sigue el formato ISO 8601.
val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
