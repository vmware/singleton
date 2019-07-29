package com.vmware.vip.test.common.listener;

import java.util.List;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.vmware.g11n.log.GLogger;
import com.vmware.g11n.log.TestCaseConfig;
import com.vmware.vip.test.common.annotation.TestCase;

public class CustomAnnotationListener implements IInvokedMethodListener, ITestListener  {
	private GLogger gLogger = GLogger.getInstance(CustomAnnotationListener.class.getName());


    /* (non-Javadoc)
     * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
     */
    @Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if(method.isTestMethod() && isTestCase(method) ) {
        	String className = method.getTestMethod().getRealClass().getSimpleName();
        	int priority = method.getTestMethod().getPriority();//if not set priority, it will get default 0
        	TestCase testCase = method.getTestMethod().getConstructorOrMethod().
        			getMethod().getAnnotation(TestCase.class);
    		TestCaseConfig testCaseConfig;
			try {
				testCaseConfig = new TestCaseConfig(
						className+"_"+testCase.id(), testCase.name(), testCase.feature(),
						testCase.type().toString(), "p" + priority);
				testCaseConfig.setDescription(testCase.description());
				GLogger.getInstance(method.getTestMethod().getTestClass().getName()).testCaseBegin(testCaseConfig);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }


    private boolean isTestCase(IInvokedMethod method) {
        boolean retVal = method.getTestMethod().getConstructorOrMethod().getMethod().
        		isAnnotationPresent(TestCase.class) ? true : false;
        return retVal;
    }

    @Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    	if(method.isTestMethod() && isTestCase(method) ) {
            	try {
            		gLogger.testCaseEnd();
        		} catch (Exception e) {
        			gLogger.info("log.testCaseEnd exception: " + e.getMessage());
        		}

            	GLogger gLogger = GLogger.getInstance(method.getTestMethod().getTestClass().getName());
            	List<Throwable> verificationFailures = gLogger.getVerificationFailures();
    			//if there are verification failures...
    			if (verificationFailures.size() > 0) {
    				//set the test to failed
    				testResult.setStatus(ITestResult.FAILURE);
    				testResult.setThrowable(verificationFailures.get(0));
    				gLogger.cleanVerificationFailures();
    			}
        }
    }

    @Override
	public void onTestStart(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
	public void onTestSuccess(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
	public void onTestFailure(ITestResult result) {
    	// TODO Auto-generated method stub

    }

    @Override
	public void onTestSkipped(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
	public void onStart(ITestContext context) {
    }

    @Override
	public void onFinish(ITestContext context) {
        // TODO Auto-generated method stub

    }
}