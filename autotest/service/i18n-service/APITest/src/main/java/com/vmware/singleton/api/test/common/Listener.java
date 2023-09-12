package com.vmware.singleton.api.test.common;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listener implements ITestListener{

		@Override
		public void onTestStart(ITestResult result) {
		// TOdo Auto-generated method stub
		}
		@Override
		public void onTestSuccess(ITestResult result) {
		// TOdo Auto-generated method stub
		System.out.println("Success of test cases and no error : "+ result.getThrowable());
		}
		@Override
		public void onTestFailure(ITestResult result) {
		// TOdo Auto-generated method stub
		System.out.println("Failure of test cases and its details are : "+ result.getThrowable());
		}
		@Override
		public void onTestSkipped(ITestResult result) {
		// TOdo Auto-generated method stub
		System.out.println("Skip of test cases and its details are : "+ result.getName() + result.getThrowable());
		}
		@Override
		public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TOdo Auto-generated method stub
		System.out.println("Failure of test cases and its details are : "+ result.getName() + result.getThrowable());
		}
		@Override
		public void onStart(ITestContext context) {
		// TOdo Auto-generated method stub
		}
		@Override
		public void onFinish(ITestContext context) {
		// TOdo Auto-generated method stub
		}

}
