import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

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
// Para formatear o parsear fechas:
val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
val dateStr = dateFormat.format(Date())  // Para obtener String desde Date
val date = dateFormat.parse("2024-04-22T09:00:00Z")  // Para parsear String a Date