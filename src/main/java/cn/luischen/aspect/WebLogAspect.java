package cn.luischen.aspect;

import cn.luischen.service.log.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/*
 * 请求的日志处理
 * Created by winterchen on 2018/4/28.
 * Advice，在切入点上执行的增强处理，主要有五个注解：
 * @Before 在切点方法之前执行
 * @After 在切点方法之后执行
 * @AfterReturning 切点方法返回后执行
 * @AfterThrowing 切点方法抛异常执行
 * @Around 属于环绕增强，能控制切点执行前，执行后
 */
@Aspect
@Component
public class WebLogAspect {

    @Autowired
    private LogService logService;

    private static Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /*
     *     定义切点,切点为对应controller
     * 注： execution表达式第一个*表示匹配任意的方法返回值，
     *     第二个*表示所有controller包下的类，
     *     第三个*表示所有方法,
     *     第一个..表示任意参数个数。
     */
    @Pointcut("execution(public * cn.luischen.controller..*.*(..))")
    public void webLog(){}


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){

        startTime.set(System.currentTimeMillis());

        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession();
        // 记录下请求内容
        LOGGER.info("URL : " + request.getRequestURL().toString());
        LOGGER.info("HTTP_METHOD : " + request.getMethod());
        LOGGER.info("IP : " + request.getRemoteAddr());
        LOGGER.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        LOGGER.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        LOGGER.info("RESPONSE : " + ret);
        LOGGER.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
        startTime.remove();//用完之后记得清除，不然可能导致内存泄露;
    }

}
