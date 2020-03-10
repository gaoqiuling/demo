package com.itisacat.basic.framework.rest.monitor;

import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.consts.SysRestConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.exception.AppException;
import com.itisacat.basic.framework.core.exception.SysException;
import com.itisacat.basic.framework.rest.filter.logging.RestServerLoggingUtil;
import com.itisacat.basic.framework.rest.filter.logging.ServerRequestWrapper;
import com.itisacat.basic.framework.rest.model.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	public static final String DEFAULT_ERROR_VIEW = "error";

//	@Autowired(required = false)
//	private IErrorMsgHandler hander;

	private static final Map<Class<?>, Integer> EXCEPTIONS = new HashMap<>();

	static {
		EXCEPTIONS.put(org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException.class,
				HttpServletResponse.SC_NOT_FOUND);
		EXCEPTIONS.put(HttpRequestMethodNotSupportedException.class, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		EXCEPTIONS.put(HttpMediaTypeNotSupportedException.class, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		EXCEPTIONS.put(HttpMediaTypeNotAcceptableException.class, HttpServletResponse.SC_NOT_ACCEPTABLE);
		EXCEPTIONS.put(MissingPathVariableException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		EXCEPTIONS.put(MissingServletRequestParameterException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(ServletRequestBindingException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(ConversionNotSupportedException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		EXCEPTIONS.put(TypeMismatchException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(HttpMessageNotReadableException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(HttpMessageNotWritableException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		EXCEPTIONS.put(MethodArgumentNotValidException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(MissingServletRequestPartException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(BindException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(NoHandlerFoundException.class, HttpServletResponse.SC_NOT_FOUND);
		EXCEPTIONS.put(IllegalArgumentException.class, HttpServletResponse.SC_BAD_REQUEST);
		EXCEPTIONS.put(MethodArgumentTypeMismatchException.class, HttpServletResponse.SC_BAD_REQUEST);
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public DataResult<Object> defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, Object handler,
			Exception e) {
		Exception ex = e;
		if (!(ex instanceof AppException)) {
			String id = MDC.get(SysRestConsts.REQUEST_ID);
			ServerRequestWrapper requestWrapper = new ServerRequestWrapper(id, req);
            boolean outputBody = BaseProperties.getProperty(PropConsts.Rest.HTTP_BODY_SHOWLOG_ENABLE, Boolean.class, false);
            StringBuilder sb = new StringBuilder(RestServerLoggingUtil.getInstance().getRequestMessage(requestWrapper, outputBody));
            sb.append(StringUtils.LF);
            sb.append(String.format("%s > %s", id, ExceptionUtils.getStackTrace(e)));
			log.error(sb.toString());
		}

//		if (hander != null) {
//			SysException sysException = hander.process(e);
//			if (sysException != null) {
//				return buildErrorResponse(sysException.getCode(), sysException.getMessage());
//			}
//		}

		if (ex instanceof SysException) {
			SysException syse = (SysException) ex;
			return buildErrorResponse(syse.getCode(), syse.getMessage());
		} else if (ex instanceof AppException) {
			AppException appe = (AppException) ex;
			return buildErrorResponse(appe.getCode(), appe.getMessage(), appe.getReturnObj());
		} else {
			return getStatusByEx(ex);
		}

	}

	public DataResult<Object> buildErrorResponse(int status, String message) {
		DataResult<Object> dataResult = new DataResult<>();
		dataResult.setStatus(status);
		dataResult.setMessage(message);
		dataResult.setTime(new Date());
		return dataResult;
	}

	public DataResult<Object> buildErrorResponse(int status, String message, Object data) {
		DataResult<Object> dataResult = new DataResult<>();
		dataResult.setStatus(status);
		dataResult.setMessage(message);
		dataResult.setData(data);
		dataResult.setTime(new Date());
		return dataResult;
	}

//    private String getReqUrl(HttpServletRequest req) {
//        String path = req.getRequestURI();
//        String queryStr = req.getQueryString();
//        if (queryStr != null) {
//            path += "?" + req.getQueryString();
//        }
//        return path;
//    }

	private DataResult<Object> getStatusByEx(Exception ex) {
		Integer status = GlobalExceptionHandler.EXCEPTIONS.get(ex.getClass());
		String message = ex.getMessage();
		if (ex instanceof MethodArgumentTypeMismatchException) {
			message = message + "  which type cannot match the filed "
					+ ((MethodArgumentTypeMismatchException) ex).getName();
		}

		if (status == null) {
			EXCEPTIONS.put(ex.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status = 500;
			message = "system error!";
		}
		return buildErrorResponse(status * 100, message);
	}

}
