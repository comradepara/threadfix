package com.denimgroup.threadfix.selenium.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.denimgroup.threadfix.selenium.pages.ApplicationDetailPage;
import com.denimgroup.threadfix.selenium.pages.DefectTrackerIndexPage;
import com.denimgroup.threadfix.selenium.pages.LoginPage;
import com.denimgroup.threadfix.selenium.pages.TeamIndexPage;

public class VulnTests extends BaseTest {
	public VulnTests(String browser) {
		super(browser);
		// TODO Auto-generated constructor stub
	}
	
	private RemoteWebDriver driver;
	private static LoginPage loginPage;
	private String teamName = getRandomString(8);
	private String appName = getRandomString(8);
	private String defectTrackerName = getRandomString(8);
	
	private final static int JIRA = 0;
	private final static int BUG = 1;
	private final static int TFS = 2;
	
	private static final String TEST_BUGZILLA_URL = DefectTrackerIndexPage.DT_URL;
	private static final String TEST_JIRA_URL = DefectTrackerIndexPage.JIRA_URL;
	private static final String JIRA_USERNAME = System.getProperty("JIRA_USERNAME");
	private static final String JIRA_PASSWORD = System.getProperty("JIRA_PASSWORD");
	private static final String JIRA_URL = System.getProperty("JIRA_URL");
	private static final String JIRAPROJECTNAME = System.getProperty("JIRAPROJECTNAME");
	private static final String BUGZILLA_USERNAME = System.getProperty("BUGZILLA_USERNAME");
	private static final String BUGZILLA_PASSWORD = System.getProperty("BUGZILLA_PASSWORD");
	private static final String BUGZILLA_URL = System.getProperty("BUGZILLA_URL");
	private static final String BUGZILLAPROJECTNAME = "For ThreadFix";
	private static final String TFS_USERNAME = System.getProperty("TFS_USERNAME");
	private static final String TFS_PASSWORD = System.getProperty("TFS_PASSWORD");
	private static final String TFS_URL = System.getProperty("TFS_URL");
	private static final String TFS_PROJECTNAME = System.getProperty("TFS_PROJECTNAME");
	
	@Before
	public void init() {
		super.init();
		driver = (RemoteWebDriver)super.getDriver();
		loginPage = LoginPage.open(driver);
		}

	
	@After
	public void shutDown() {
		driver.quit();
	}
	
	@Test
	public void mergeSingleVulnJira(){
		assertTrue("Jira",build(JIRA));
		//merge here
		//nav to app detail page
		ApplicationDetailPage ad = loginPage.login("user", "password")
											.clickOrganizationHeaderLink()
											.expandTeamRowByIndex(teamName)
											.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-157")
				.clickMergeDefectSubmit();
		
		//verify
		ad = ad.clickExpandAllVulns();
		
		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 1);

        ad.clickOrganizationHeaderLink();
		
		ad.logout();
		destroy();
	}

	@Test
	public void mergeSingleVulnBugzilla(){
		assertTrue("bug",build(BUG));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickMergeDefectLink()
				.selectMergeDefect("2")
				.clickMergeDefectSubmit();

		//verify
		ad = ad.clickExpandAllVulns();


		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 1);

        ad.clickOrganizationHeaderLink();

        ad.logout();
		destroy();
	}
    @Ignore
	@Test
	public void mergeSingleVulnTFS(){
		assertTrue("bug",build(TFS));
		//merge here
		destroy();
	}
	
	@Test
	public void mergeMultiVulnJira(){
		assertTrue("Jira",build(JIRA));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-157")
				.clickMergeDefectSubmit();

		//verify
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 3);

        ad.clickOrganizationHeaderLink();

		ad.logout();
		destroy();
	}
	@Test
	public void mergeMultiVulnBugzilla(){
		assertTrue("bug",build(BUG));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("2")
				.clickMergeDefectSubmit();

		//verify
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 3);

        ad.clickOrganizationHeaderLink();

		ad.logout();
		destroy();
	}
	@Ignore
	@Test
	public void mergeMultiVulnTFS(){
		assertTrue("bug",build(TFS));
		//merge here
		destroy();
	}
	
	@Test
	public void changeMergeSingleVulnJira(){
		assertTrue("Jira",build(JIRA));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-156")
				.clickMergeDefectSubmit();

		//change merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-155")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 1);

        ad.clickOrganizationHeaderLink();

		ad.logout();
		destroy();
	}

	@Test
	public void changeMergeSingleVulnBugzilla(){
		assertTrue("bug",build(BUG));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickMergeDefectLink()
				.selectMergeDefect("2")
				.clickMergeDefectSubmit();

		//change merge
		ad = ad.clickExpandAllVulns()
                .clickVulnCheckBox(1)
				.clickMergeDefectLink()
				.selectMergeDefect("4")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 1);

        ad.clickOrganizationHeaderLink();

		ad.logout();
		destroy();
	}
	@Ignore
	@Test
	public void changeMergeSingleVulnTFS(){
		assertTrue("bug",build(TFS));
		//merge here
		destroy();
	}
	
	@Test
	public void changeMergeMultiVulnJira(){
		assertTrue("Jira",build(JIRA));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-150")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-157")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 3);

        ad.clickOrganizationHeaderLink();

		ad.logout();
		destroy();
	}

	@Test
	public void changeMergeMultiVulnBugzilla(){
		assertTrue("bug",build(BUG));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("2")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("4")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 3);

        ad.clickOrganizationHeaderLink();

   		ad.logout();
		
		destroy();
	}
	@Ignore
	@Test
	public void changeMergeMultiVulnTFS(){
		assertTrue("bug",build(TFS));
		//merge here
		destroy();
	}
	
	@Test
	public void changeMergeMultiDiffVulnJira(){
		assertTrue("Jira",build(JIRA));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-115")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(4)
				.clickVulnCheckBox(5)
				.clickVulnCheckBox(6)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-140")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(4)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("THREAD-141")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 6);

        ad.clickOrganizationHeaderLink();

		ad.logout();
		destroy();
	}

	@Test
	public void changeMergeMultiDiffVulnBugzilla(){
		assertTrue("bug",build(BUG));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName);
		//submit merge
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(2)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("2")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(4)
				.clickVulnCheckBox(5)
				.clickVulnCheckBox(6)
				.clickMergeDefectLink()
				.selectMergeDefect("4")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickVulnCheckBox(6)
				.clickVulnCheckBox(3)
				.clickMergeDefectLink()
				.selectMergeDefect("3")
				.clickMergeDefectSubmit();
		
		ad = ad.clickExpandAllVulns();

		assertTrue("Number of submitted vulns is incorrect",ad.getNumOfSubmitedDefects() == 6);

        ad.clickOrganizationHeaderLink();

		ad.logout();
		destroy();

	}
	@Ignore
	@Test
	public void changeMergeMultiDiffVulnTFS(){
		assertTrue("bug",build(TFS));
		//merge here
		destroy();
	}
	@Test
	public void submitBlankDefect(){
		assertTrue("blank defect",build(JIRA));
		ApplicationDetailPage ad = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.expandTeamRowByIndex(teamName)
				.clickViewAppLink(appName, teamName)
				.clickExpandAllVulns()
				.clickVulnCheckBox(1)
				.clickSubmitDefectLink()
				.submitDefect();
		assertTrue("defect was not submitted",ad.getAlert().contains("The Defect was submitted to the tracker."));
        ad.clickOrganizationHeaderLink();
		ad.logout();
		destroy();
		
	}
	
	private boolean build(int dtType){
		String defectTrackerAppName = "";
		String defectTrackerURL = "";
		String userName = "";
		String password = "";
		String projectName = "";
		String whiteHatAppName = "Demo Site BE";
		String whiteHatKey = System.getProperty("WHITEHAT_KEY");
		switch(dtType){
			case JIRA:
				defectTrackerAppName = "Jira";
				defectTrackerURL = JIRA_URL;
				userName = JIRA_USERNAME;
				password = JIRA_PASSWORD;
				projectName = JIRAPROJECTNAME;
				break;
			case BUG:
				defectTrackerAppName = "Bugzilla";
				defectTrackerURL = BUGZILLA_URL;
				userName = BUGZILLA_USERNAME;
				password = BUGZILLA_PASSWORD;
				projectName = BUGZILLAPROJECTNAME;
				break;
			case TFS:
				defectTrackerAppName = "Microsoft TFS";
				defectTrackerURL = TFS_URL;
				userName = TFS_USERNAME;
				password = TFS_PASSWORD;
				projectName = TFS_PROJECTNAME;
				break;
			default:
				return false;
		}

        System.out.println("defect tracker app name: " + defectTrackerAppName);
        System.out.println("defect Tracker URL: " + defectTrackerURL);
        System.out.println("Username: " + userName);
        System.out.println("password: " + password);
        System.out.println("project name: " + projectName);
        System.out.println("white hat appName: " + whiteHatAppName);
        System.out.println( "defect tracker type: " + dtType);


		//add team
		TeamIndexPage teamIndexPage = loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.clickAddTeamButton()
				.setTeamName(teamName)
				.addNewTeam();

        sleep(2000);
		
		//add app
		teamIndexPage = teamIndexPage.expandTeamRowByIndex(teamName)
					.addNewApplication(teamName, appName, "", "Low")
					.saveApplication(teamName);
		
		//add defect Tracker
		DefectTrackerIndexPage defectTrackerIndexPage = teamIndexPage.clickDefectTrackersLink()
																	.clickAddDefectTrackerButton()
																	.enterName(null, defectTrackerName)
																	.enterType(null, defectTrackerAppName)
																	.enterURL(null, defectTrackerURL)
																	.clickSaveNewDefectTracker();

		//attach defect Tracker
		ApplicationDetailPage applicationDetailPage = defectTrackerIndexPage.clickOrganizationHeaderLink()
																			.expandTeamRowByIndex(teamName)
																			.clickViewAppLink(appName, teamName)
																			.addDefectTracker(defectTrackerName, userName, password, projectName);
		//import scan
		applicationDetailPage.clickRemoteProvidersLink()
                            .clickRemoteProvidersLink()
                            .clickRemoteProvidersLink()
                            .clickRemoteProvidersLink()
							.clickConfigureWhiteHat()
							.setWhiteHatAPI(whiteHatKey)
							.saveWhiteHat()
							.clickEditMapping(whiteHatAppName)
							.setTeamMapping(whiteHatAppName, teamName)
							.setAppMapping(whiteHatAppName, appName)
							.clickSaveMapping(whiteHatAppName)
							.clickImportScan(whiteHatAppName)
							.clickOrganizationHeaderLink()
							.logout();

		
		
		return true;
	}
	
	private void destroy(){
		loginPage.login("user", "password")
				.clickOrganizationHeaderLink()
				.clickViewTeamLink(teamName)
				.clickDeleteButton()
				.clickRemoteProvidersLink()
				.clickRemoveWhiteHatConfig()
				.clickDefectTrackersLink()
				.clickDeleteButton(defectTrackerName)
                .clickOrganizationHeaderLink()
                .logout();
	}


}