package com.ktran.flashsale_core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa được phân loại", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
  //  USERNAME_INVALID(1003, "Tên đăng nhập phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
   // INVALID_PASSWORD(1004, "Mật khẩu phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD(1004, "Mật khẩu không hợp lệ", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Sai tk", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(1010, "Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST),
    OTP_INVALID(1011, "Mã OTP không đúng", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Tuổi của bạn phải ít nhất {min}", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(2001, "Vai trò không tồn tại", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(2002, "Vai trò đã tồn tại", HttpStatus.NOT_FOUND),
    USER_BLOCKED(3002, "Tài khoản đã bị khóa", HttpStatus.FORBIDDEN),
    PROCUREMENT_NOT_APPROVED(4001, "Đề xuất chưa được duyệt, không thể nhập kho", HttpStatus.BAD_REQUEST),
    IMPORT_EXCEEDS_LIMIT(4002, "Số lượng nhập vượt quá số lượng đề xuất", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(1012, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_NOT_EXISTED(1013, "Đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_EXISTED(1014, "Thanh toán không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1015, "Sản phẩm đã hết", HttpStatus.NOT_FOUND),
  ORDER_ALREADY_PAID(1016, "Đơn hàng thanh toán", HttpStatus.NOT_FOUND),
    ;
    private final int code;
    private final  String message;
    private final HttpStatusCode statusCode;

}
