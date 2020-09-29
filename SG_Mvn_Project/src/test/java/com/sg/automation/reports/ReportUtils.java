package com.sg.automation.reports;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sg.automation.driver.DriverScript;

/*
 
 here 4 methods
1.Where it will create Report
2.Where it will end Report
3.Where it will write method
4.Where it will capture screenshot


Here we need method header.

 
 */

public class ReportUtils extends DriverScript{

	/*****************************************
	 * Method Name	: startReport
	 * Purpose		:
	 * Arguments	:
	 * Return Type	:
	 * Author		:
	 ****************************************/
	//Creating extent report object
	//Return object
	//fileName , when we create file we need name for that ex: name.html
	//what ever testCase ID  we pass with same ID it will create folder
	//Inside REsult folder we need to create build folder, inside that testCase folder
	// inside testCase folder  screenshot folder
	//While creating folder we need to have date and time
	
	public ExtentReports startReport(String fileName, String testCaseID, String buildNum)
	{
		String strPath = null;
		File resLocation = null;
		File resScreenshot = null;
	
		try {
			strPath = System.getProperty("user.dir")+"\\Results\\"+buildNum; //inside Results folder creating buildNum folder.
			// ("user.dir")+"\\Results\\  it will give upto Results folder path
			//Inside above folder we need to save resLocation , under this we need resScreenshot folder
			
			if(!new File(strPath).exists()) { //If this path doesn't exist create it.
				new File(strPath).mkdir();
			}
			
			
			resLocation = new File(strPath + "\\" +testCaseID);//resLocation path
			
			if(!resLocation.exists()) { //If its not exist create , if it exist don't create
				resLocation.mkdir();
			}
			
			strScreenShotPath=resLocation +"\\screenshot";
			resScreenshot = new File(strScreenShotPath);//resScreenshot path under resLocation testCaseID folder
			
			if(!resScreenshot.exists()) {//If its not exist create , if it exist don't create
				resScreenshot.mkdir();
			}
			
			//ExtentReports accepts two argument where we want to write file, replacing existing or not  true
			//when ever we give fileName we have to give date and time it is called timeStamp
			//getDateTime method taking from AppIndependent methods
			// copy extent-config.xml
			// go to google search extent-config.xml copy
			// right click project->new->other->xml file->name as extent-config.xml (this name should be same)
			// inside source paste it
			
			
			extent = new ExtentReports(resLocation +"\\"+ fileName +appInd.getDateTime("ddMMyyyy_hhmmss")+".html", true);
			extent.addSystemInfo("Host Name", System.getProperty("os.name"));//adding information to file, Host Name is out computer
			extent.addSystemInfo("User Name", System.getProperty("user.name"));//our system user Name , who executed our framework
			extent.loadConfig(new File(System.getProperty("user.dir")+"\\extent-config.xml"));//loading extent-config.xml file
			return extent;
		}catch(Exception e)
		{
			System.out.println("Exception in startReport() method. "+e.getMessage());
			return null;//we are returning class , if class not it is null
		}
		finally {
			strPath = null;
			resLocation = null;
			resScreenshot = null;
		}
	}
	
	//After doing we have to end report
	//Here we don't want to return anything.
	//Here we need extentTest as argument.
	/*****************************************
	 * Method Name	: endTest
	 * Purpose		:
	 * Arguments	:
	 * Return Type	:
	 * Author		:
	 ****************************************/
	public void endTest(ExtentTest test) {
		try {
			extent.endTest(test);//passing test object we created
			extent.flush();//It will write result only after writing flush(). untill there it will be stored in buffer.
		}catch(Exception e)
		{
			System.out.println("Exception in endTest() method. "+e.getMessage());
		}
	}
	
	
	
	
	//for capturing screenshot
	///screenCapture will happen based on WebDriver.
	//screen capture is one of the file .jpeg , so creating file object
	// TakesScreenshot class used to take screenshot , it takes based on driver
	//FileUtils class import from commons.io
	/*****************************************
	 * Method Name	: captureScreenshot
	 * Purpose		:
	 * Arguments	:
	 * Return Type	:
	 * Author		:
	 ****************************************/
	public String captureScreenshot(WebDriver oDriver)
	{
		File srcFile = null;//capturing file
		String destPath = null;//storing source file in destination
		try {
			TakesScreenshot screenShot = (TakesScreenshot) oDriver;//casting screenshot
			srcFile = screenShot.getScreenshotAs(OutputType.FILE);//screenshot is captured
			//strScreenShot path , screenshot is filr name providing date and time for that.we written date and time method ,.png is format
			destPath = strScreenShotPath +"\\ScreenShot_" + appInd.getDateTime("ddMMYYYY_hhmmss")+".png";
			FileUtils.copyFile(srcFile, new File(destPath));//copyfile method isnide FileUtils here provide sourcefile and destination
			return destPath;
		}catch(Exception e)
		{
			System.out.println("Exception in captureScreenshot() method. "+e.getMessage());
			return destPath;
		}
		finally {
			srcFile = null;
		}
	}
	
	
	
	//write actual result in extent report
	//we need webdriver as argument to capture screenshot, we need status pass/fail,
	// what information we need to print ,which test we need to write report , whether to capture screenshot or not to capture.
	// screen shot is required only when we perform action on UI , so if we dont want sometime so make it boolean.
	// If we need screenshot , put what are status case we will have.
	 // pass test object ,log the report ,what message we log so LogStatus
	// for that pass description , within description call method which capture screenshot
	// use test object and addScreenCapture() readily available ,we have written captureScreenshot under reports
	
	/*****************************************
	 * Method Name	: writeResult
	 * Purpose		:
	 * Arguments	:
	 * Return Type	:
	 * Author		:
	 ****************************************/
	public void writeResult(WebDriver oDriver, String status, String description, 
									ExtentTest test, boolean screenReqd)
	{
		try {
			if(screenReqd) {                     //If screenshot is required , by default it will be true
				switch(status.toLowerCase()) {   //passing the status
					case "pass":                //If it is pass
					       test.log(LogStatus.PASS, description +":"+ test.addScreenCapture(    //Logging the status , it will give color description.within description call the method which has screenshot
								reports.captureScreenshot(oDriver)));  //It will capture screenshot
						break;
					case "fail":
						test.log(LogStatus.FAIL, description +":"+ test.addScreenCapture(
								reports.captureScreenshot(oDriver)));
						break;
					case "warning":
						test.log(LogStatus.WARNING, description +":"+ test.addScreenCapture(
								reports.captureScreenshot(oDriver)));
						break;
					case "info":
						test.log(LogStatus.INFO, description +":"+ test.addScreenCapture(
								reports.captureScreenshot(oDriver)));
						break;
					case "exception":
						test.log(LogStatus.FATAL, description +":"+ test.addScreenCapture(
								reports.captureScreenshot(oDriver)));
						break;
					default:
						System.out.println("Invalid result status '"+status+"' was specified. please enter the valid status for the results to write");
				}
			}else {  //If we don't want to capture screenshot
				switch(status.toLowerCase()) {
					case "pass":
						test.log(LogStatus.PASS, description);
						break;
					case "fail":
						test.log(LogStatus.FAIL, description);
						break;
					case "warning":
						test.log(LogStatus.WARNING, description);
						break;
					case "info":
						test.log(LogStatus.INFO, description);
						break;
					case "exception":
						test.log(LogStatus.FATAL, description);
						break;
					default:
						System.out.println("Invalid result status '"+status+"' was specified. please enter the valid status for the results to write");
			}
			}
		}catch(Exception e)
		{
			System.out.println("Exception in writeResult() method. "+e.getMessage());
		}
	}
	
}
