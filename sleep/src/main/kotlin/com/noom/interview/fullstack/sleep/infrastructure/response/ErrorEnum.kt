import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import com.noom.interview.fullstack.sleep.infrastructure.exception.InternalServerErrorException
import com.noom.interview.fullstack.sleep.infrastructure.exception.NotFoundException
import kotlin.reflect.KClass

enum class ErrorEnum(
    val title: String,
    val detail: String,
    val statusCode: Int,
    val error: KClass<out Exception>
) {
    BAD_REQUEST_EXCEPTION("BAD REQUEST EXCEPTION", "The request was malformed, omitting required attributes, either in the payload or through URL attributes.", 400, BadRequestException::class),
    NOT_FOUND_EXCEPTION("NOT FOUND EXCEPTION", "The requested resource does not exist or has not been implemented.", 404, NotFoundException::class),
    INTERNAL_SERVER_ERROR_EXCEPTION("INTERNAL SERVER ERROR EXCEPTION", "An error occurred in the service.", 500, InternalServerErrorException::class),
}