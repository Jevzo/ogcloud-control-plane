package de.ogwars.ogcloud.exception.mapper

import de.ogwars.ogcloud.exception.GroupExistsException
import de.ogwars.ogcloud.exception.ServiceFoundException
import de.ogwars.ogcloud.exception.StatefulSetFoundException
import de.ogwars.ogcloud.exception.VolumeFoundException
import de.ogwars.ogcloud.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionMapper {

    @ExceptionHandler(GroupExistsException::class)
    fun handleGroupExistsException(exception: GroupExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(exception.message!!))

    @ExceptionHandler(ServiceFoundException::class)
    fun handleServiceFoundException(exception: ServiceFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(exception.message!!))

    @ExceptionHandler(VolumeFoundException::class)
    fun handleVolumeFoundException(exception: VolumeFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(exception.message!!))

    @ExceptionHandler(StatefulSetFoundException::class)
    fun handleStatefulSetFoundException(exception: StatefulSetFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(exception.message!!))
}