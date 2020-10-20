package info.colarietitosti.supertools.backend.config.profiling;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ProfilerAspect {

    @Pointcut("@annotation(info.colarietitosti.supertools.backend.config.profiling.Profiled)")
    public void profile() {
    }

    @Pointcut("within(@info.colarietitosti.supertools.backend.config.profiling.Profiled *)")
    public void profileClass() {
    }

    @Around("profile()")
    public Object auditMethod(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        return logPerformance(thisJoinPoint);
    }

    @Around("profileClass()")
    public Object auditClass(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        return logPerformance(thisJoinPoint);
    }

    private Object logPerformance(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object obj = thisJoinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;

        String methodName = thisJoinPoint.getSignature().getName();
        String className = thisJoinPoint.getSignature().getDeclaringTypeName();
        String argsConcat = extractArgs(thisJoinPoint);
        log.info("PROFILING {} \n{} took {} ms for args: {}", className, methodName, timeTaken, argsConcat);

        return obj;
    }

    private String extractArgs(ProceedingJoinPoint thisJoinPoint) {
        StringBuilder argsConcat = new StringBuilder();
        for (int i = 0; i < thisJoinPoint.getArgs().length; i++){
            argsConcat.append(thisJoinPoint.getArgs()[i].toString());
            argsConcat.append(" ");
        }
        return argsConcat.toString();
    }
}