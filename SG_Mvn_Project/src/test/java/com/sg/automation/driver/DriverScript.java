package com.sg.automation.driver;

import java.lang.reflect.Method;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeSuite;


import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.sg.automation.methods.AppDependentMethods;
import com.sg.automation.methods.AppIndependentMethods;
import com.sg.automation.methods.Datatable;
import com.sg.automation.methods.TaskModuleMethods;
import com.sg.automation.methods.UserModuleMethods;
import com.sg.automation.reports.ReportUtils;

public class DriverScript {
	public static AppIndependentMethods appInd = null;
	public static AppDependentMethods appDep = null;
	public static Datatable datatable = null;
	public static UserModuleMethods userMethods = null;
	public static TaskModuleMethods taskMethods = null;
	public static ReportUtils reports = null;
	public static ExtentReports extent = null; //It is loaded from report
	public static ExtentTest test = null;  //It is loaded from testScript
	public static String controller = null;
	public static String strScreenShotPath = null; //It is loaded from report
	public static String moduleName = null;//make module name global so declare in class
	public static String testCaseID =null;
	
	@BeforeSuite      //when ever framework start it will run first
	public void loadClasses() {  //loading memory
		appInd = new AppIndependentMethods();
		appDep = new AppDependentMethods();
		datatable = new Datatable();
		userMethods = new UserModuleMethods();
		taskMethods = new TaskModuleMethods();
		reports = new ReportUtils();
		controller = System.getProperty("user.dir")+"\\ExecutionController\\Controller.xlsx"; //controller path
	}
	
	//Reading controller execution excel sheet one by one
	@Test
	public void executeTests() {    //going inside and reading one by one value
		String executionStatus = null;
		int pRows = 0;
		int mRows = 0;
		int tcRows = 0;
		Class cls = null; //to apply reflection concept we need class ,Object,Method
		Object obj = null;
		Method meth = null;
		try {
			//going to project sheet ,  getRowNumber will give row
			pRows = datatable.getRowNumber(controller, "Projects");//filename,sheetName
			//after getting row loop
			for(int i=1; i<=pRows; i++)
			{
				//In project sheet if execution status is Yes
				executionStatus = datatable.getCellData(controller, "Projects", "ExecuteProject", i); //filename,sheetName,columnName,RowNumber
				if(executionStatus.equalsIgnoreCase("Yes")) { //If it is yes read project name , connect that sheet
					String projectName = datatable.getCellData(controller, "Projects", "ProjectName", i); //projectName is column , i is rowNumber
					
					//after reading project sheet going to that sheet
					mRows = datatable.getRowNumber(controller, projectName); //file name , sheet name
					//after getting sheetRowNumber loop it.
					for(int j=1; j<=mRows; j++)
					{
						executionStatus = datatable.getCellData(controller, projectName, "ExecuteModule", j); //executeModel is columnName
						if(executionStatus.equalsIgnoreCase("Yes")) {
							//make module name global so declare in class
							moduleName = datatable.getCellData(controller, projectName, "ModuleNames", j);
							
							//once we get module name go to that sheet row
							tcRows = datatable.getRowNumber(controller, moduleName);//fileName,sheetName
							for(int k=1; k<=tcRows; k++)
							{
								executionStatus = datatable.getCellData(controller, moduleName, "ExecuteTest", k);
								if(executionStatus.equalsIgnoreCase("Yes")) //If it is yes apply reflection concept
								{
									String className = datatable.getCellData(controller, moduleName, "ClassName", k);//filename,sheetname,columnName,row
									String scriptName = datatable.getCellData(controller, moduleName, "TestScriptName", k);
									 testCaseID=datatable.getCellData(controller, moduleName, "TestCaseID", k);
									cls = Class.forName(className);//reading class name
									obj = cls.newInstance();
									meth = obj.getClass().getMethod(scriptName);//pass methodName i,e. scriptName
									//reflection is object so converting it to string
									// meth.invoke(obj) is used to run
									if(String.valueOf(meth.invoke(obj)).equalsIgnoreCase("True")){ //write a report to excel sheet
										datatable.setCellData(controller, moduleName, "Status", k, "PASSED");//writing report
									}else {
										datatable.setCellData(controller, moduleName, "Status", k, "FAILED");
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e)
		{
			System.out.println("Exception in executeTests() method. "+e.getMessage());
		}
		finally {
			cls = null;
			obj = null;
			meth = null;
		}
	}
}
