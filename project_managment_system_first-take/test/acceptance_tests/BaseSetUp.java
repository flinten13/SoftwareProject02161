package acceptance_tests;

import planner.app.Planner;
import planner.domain.Project;
import planner.domain.User;

public class BaseSetUp {

    private Planner planner;
    private User user;
    private User admin;
    private Project project;

    private ErrorMessageHolder errorMessage;
    public UserHelper userHelper;
    public AdminHelper adminHelper;
    public ProjectHelper projectHelper;

    public BaseSetUp(Planner planner, ErrorMessageHolder errorMessage, UserHelper userHelper, ProjectHelper projectHelper, AdminHelper adminHelper) {
        this.planner = planner;
        this.errorMessage = errorMessage;
        this.userHelper = userHelper;
        this.projectHelper = projectHelper;
        this.adminHelper = adminHelper;
    }



}
